package com.plnt.client.audio

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.AudioAttributes
import android.media.AudioDeviceCallback
import android.media.AudioDeviceInfo
import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioRecord
import android.media.AudioTrack
import android.media.MediaRecorder
import android.media.audiofx.AcousticEchoCanceler
import android.media.audiofx.NoiseSuppressor
import android.os.Build
import android.util.Log
import com.plnt.client.model.InputRoute
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicReference
import kotlin.concurrent.thread
import kotlin.math.roundToInt

private const val TAG = "plnt.audio"

/** plnt-core only ever speaks 20 ms mono frames at this rate (see core/src/lib.rs PCM_FRAME_SAMPLES). */
const val CORE_SAMPLE_RATE_HZ = 48_000
const val CORE_FRAME_SAMPLES = 960 // 48_000 * 0.020

/**
 * Owns the two device audio streams (mic capture, call playback) and the
 * resampling glue between "whatever the device's current route natively
 * runs at" and the fixed 48 kHz mono 20 ms frame plnt-core expects.
 *
 * Lifecycle: constructed once per call by [com.plnt.client.service.VoiceService]
 * (`start()` on call connect, `shutdown()` on disconnect). Push-to-talk does
 * NOT stop/start capture — see [setSending] — because tearing down the
 * capture stream drops the Bluetooth SCO link and reconnecting it audibly
 * clips the first syllable after every PTT press.
 */
class AudioEngine(
    private val context: Context,
    private val onCaptureFrame: (FloatArray) -> Unit,
    /** Fires whenever the set of physically-present input routes changes (plug/unplug, BT connect). */
    private val onInputRoutesChanged: (Set<InputRoute>) -> Unit = {},
) {
    private val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager

    private val running = AtomicBoolean(false)
    /** Gates whether captured frames are forwarded to [onCaptureFrame]. Capture itself never stops. */
    private val sending = AtomicBoolean(false)

    @Volatile private var preferredRoute: InputRoute = InputRoute.AUTO

    private var captureThread: Thread? = null
    private var record: AudioRecord? = null
    private var echoCanceler: AcousticEchoCanceler? = null
    private var noiseSuppressor: NoiseSuppressor? = null

    private val playbackTrack = AtomicReference<AudioTrack?>(null)

    private val deviceCallback = object : AudioDeviceCallback() {
        override fun onAudioDevicesAdded(addedDevices: Array<out AudioDeviceInfo>) {
            onInputRoutesChanged(computeAvailableInputRoutes())
            if (addedDevices.any { it.isRelevant() }) rehome("device added: ${addedDevices.joinToString { it.describe() }}")
        }

        override fun onAudioDevicesRemoved(removedDevices: Array<out AudioDeviceInfo>) {
            onInputRoutesChanged(computeAvailableInputRoutes())
            if (removedDevices.any { it.isRelevant() }) rehome("device removed: ${removedDevices.joinToString { it.describe() }}")
        }
    }

    private val scoRouter = BluetoothScoRouter(context, audioManager)

    /** Starts capture + playback on whatever route Android currently has active. */
    @SuppressLint("MissingPermission") // caller (VoiceService) checks RECORD_AUDIO before calling start()
    fun start() {
        if (!running.compareAndSet(false, true)) return

        audioManager.mode = AudioManager.MODE_IN_COMMUNICATION
        audioManager.registerAudioDeviceCallback(deviceCallback, null)
        onInputRoutesChanged(computeAvailableInputRoutes())
        // Route to the preferred device *before* opening the streams —
        // AudioRecord/AudioTrack pick up whatever route is active at open
        // time, they don't hot-follow it.
        scoRouter.routeToPreferredDeviceAndWait(preferredRoute)

        openPlaybackTrack()
        openCaptureAndStartThread()
        Log.i(TAG, "AudioEngine started (mode=MODE_IN_COMMUNICATION, preferredRoute=$preferredRoute)")
    }

    fun shutdown() {
        if (!running.compareAndSet(true, false)) return

        audioManager.unregisterAudioDeviceCallback(deviceCallback)
        scoRouter.release()
        captureThread?.join(500)
        captureThread = null

        echoCanceler?.release(); echoCanceler = null
        noiseSuppressor?.release(); noiseSuppressor = null
        record?.let { runCatching { it.stop() }; it.release() }
        record = null

        playbackTrack.getAndSet(null)?.let { runCatching { it.stop() }; it.release() }

        audioManager.mode = AudioManager.MODE_NORMAL
        Log.i(TAG, "AudioEngine stopped")
    }

    /**
     * Push-to-talk gate. `true` = open mic / PTT pressed (frames are sent);
     * `false` = muted for send purposes only. Capture keeps running either
     * way so the Bluetooth SCO connection never drops between presses.
     */
    fun setSending(value: Boolean) {
        sending.set(value)
    }

    /**
     * Which [InputRoute]s the hardware currently offers. AUTO and BUILTIN_MIC are always
     * present; the rest reflect whatever [AudioManager] currently reports connected.
     */
    fun availableInputRoutes(): Set<InputRoute> = computeAvailableInputRoutes()

    /**
     * User-initiated override of the auto-priority chain (PHA-3132 follow-up: "switch
     * inputs"). Re-routes immediately if a call is live — unlike [setSending] this is a
     * deliberate route change, not a PTT toggle, so re-opening the streams here is correct
     * (see the class doc for why PTT itself must not do this).
     */
    fun setPreferredInputRoute(route: InputRoute) {
        preferredRoute = route
        if (running.get()) rehome("input route changed to $route")
    }

    private fun computeAvailableInputRoutes(): Set<InputRoute> {
        val present = audioManager.getDevices(AudioManager.GET_DEVICES_INPUTS).map { it.type }.toSet()
        val routes = mutableSetOf(InputRoute.AUTO, InputRoute.BUILTIN_MIC)
        if (AudioDeviceInfo.TYPE_BLUETOOTH_SCO in present) routes += InputRoute.BLUETOOTH
        if (AudioDeviceInfo.TYPE_WIRED_HEADSET in present) routes += InputRoute.WIRED_HEADSET
        if (AudioDeviceInfo.TYPE_USB_HEADSET in present) routes += InputRoute.USB_HEADSET
        return routes
    }

    /** Feed one decoded 20 ms / 960-sample / 48 kHz mono frame from plnt-core to the speaker/headset. */
    fun onPlaybackFrame(samples: FloatArray) {
        val track = playbackTrack.get() ?: return
        if (samples.size != CORE_FRAME_SAMPLES) {
            Log.w(TAG, "onPlaybackFrame: got ${samples.size} samples, expected $CORE_FRAME_SAMPLES")
        }
        track.write(samples, 0, samples.size, AudioTrack.WRITE_NON_BLOCKING)
    }

    // ---- device rehoming --------------------------------------------------

    private fun rehome(reason: String) {
        if (!running.get()) return
        Log.i(TAG, "rehoming audio streams: $reason")
        // Tear down and reopen both streams against the new route. AudioRecord/
        // AudioTrack don't hot-swap devices; MODE_IN_COMMUNICATION + the new
        // active AudioDeviceInfo means a fresh instance picks up the new route
        // and native sample rate automatically.
        scoRouter.routeToPreferredDeviceAndWait(preferredRoute)
        openPlaybackTrack(reopen = true)
        reopenCapture()
    }

    private fun AudioDeviceInfo.isRelevant(): Boolean = type in relevantDeviceTypes

    private fun AudioDeviceInfo.describe(): String = "${productName}(type=$type)"

    // ---- capture ------------------------------------------------------------

    private fun openCaptureAndStartThread() {
        val rec = buildAudioRecord()
        record = rec
        rec.startRecording()

        val nativeRate = rec.sampleRate
        val resampler = Resampler(fromHz = nativeRate, toHz = CORE_SAMPLE_RATE_HZ)
        // Read chunk sized to ~20 ms of the *native* rate; the resampler then
        // produces however many 48 kHz samples that maps to and we frame those
        // into exact 960-sample (20 ms @ 48 kHz) blocks before forwarding.
        val readChunk = (nativeRate / 50).coerceAtLeast(160) // 20 ms at native rate
        val nativeBuf = ShortArray(readChunk)
        val frameAccumulator = FloatFrameAccumulator(CORE_FRAME_SAMPLES)

        captureThread = thread(name = "plnt-audio-capture", isDaemon = true) {
            while (running.get()) {
                val currentRecord = record ?: break
                val n = currentRecord.read(nativeBuf, 0, nativeBuf.size, AudioRecord.READ_BLOCKING)
                if (n <= 0) continue
                val resampled = resampler.process(nativeBuf, n)
                frameAccumulator.push(resampled) { frame ->
                    // Capture never stops; only forwarding is gated by PTT.
                    if (sending.get()) onCaptureFrame(frame)
                }
            }
        }
    }

    private fun reopenCapture() {
        val old = record
        record = null
        echoCanceler?.release(); echoCanceler = null
        noiseSuppressor?.release(); noiseSuppressor = null
        old?.let { runCatching { it.stop() }; it.release() }
        captureThread?.join(500)
        openCaptureAndStartThread()
    }

    @SuppressLint("MissingPermission")
    private fun buildAudioRecord(): AudioRecord {
        // VOICE_COMMUNICATION source is what routes AEC/NS + Bluetooth SCO
        // correctly on stock Android; it also makes this stream cooperate
        // with MODE_IN_COMMUNICATION for automatic route selection.
        val candidateRates = intArrayOf(CORE_SAMPLE_RATE_HZ, 44_100, 16_000, 8_000)
        var lastError: Throwable? = null
        for (rate in candidateRates) {
            val minBuf = AudioRecord.getMinBufferSize(rate, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT)
            if (minBuf <= 0) continue
            try {
                val rec = AudioRecord(
                    MediaRecorder.AudioSource.VOICE_COMMUNICATION,
                    rate,
                    AudioFormat.CHANNEL_IN_MONO,
                    AudioFormat.ENCODING_PCM_16BIT,
                    minBuf * 4,
                )
                if (rec.state != AudioRecord.STATE_INITIALIZED) {
                    rec.release()
                    continue
                }
                attachEffects(rec.audioSessionId)
                return rec
            } catch (t: Throwable) {
                lastError = t
            }
        }
        throw IllegalStateException("AudioEngine: could not open AudioRecord on any candidate rate", lastError)
    }

    private fun attachEffects(audioSessionId: Int) {
        if (AcousticEchoCanceler.isAvailable()) {
            echoCanceler = AcousticEchoCanceler.create(audioSessionId)?.apply { enabled = true }
        } else {
            Log.w(TAG, "AcousticEchoCanceler not available on this device")
        }
        if (NoiseSuppressor.isAvailable()) {
            noiseSuppressor = NoiseSuppressor.create(audioSessionId)?.apply { enabled = true }
        } else {
            Log.w(TAG, "NoiseSuppressor not available on this device")
        }
    }

    // ---- playback -------------------------------------------------------------

    private fun openPlaybackTrack(reopen: Boolean = false) {
        val old = if (reopen) playbackTrack.getAndSet(null) else playbackTrack.get()
        old?.let { runCatching { it.stop() }; it.release() }
        if (!reopen && playbackTrack.get() != null) return

        val attrs = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_VOICE_COMMUNICATION)
            .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
            .build()
        val format = AudioFormat.Builder()
            .setEncoding(AudioFormat.ENCODING_PCM_FLOAT)
            .setSampleRate(CORE_SAMPLE_RATE_HZ)
            .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
            .build()
        val minBuf = AudioTrack.getMinBufferSize(CORE_SAMPLE_RATE_HZ, AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_FLOAT)
        val track = AudioTrack(
            attrs,
            format,
            (minBuf.coerceAtLeast(CORE_FRAME_SAMPLES * 4)) * 4,
            AudioTrack.MODE_STREAM,
            AudioManager.AUDIO_SESSION_ID_GENERATE,
        )
        track.play()
        playbackTrack.set(track)
    }

    companion object {
        private val relevantDeviceTypes = setOf(
            AudioDeviceInfo.TYPE_BLUETOOTH_SCO,
            AudioDeviceInfo.TYPE_BLUETOOTH_A2DP,
            AudioDeviceInfo.TYPE_WIRED_HEADSET,
            AudioDeviceInfo.TYPE_WIRED_HEADPHONES,
            AudioDeviceInfo.TYPE_USB_HEADSET,
            AudioDeviceInfo.TYPE_BUILTIN_SPEAKER,
            AudioDeviceInfo.TYPE_BUILTIN_MIC,
        )
    }
}

/**
 * Minimal linear-interpolation resampler, mono, [Short] PCM in / [Float] PCM
 * (range roughly [-1, 1]) out. Good enough for voice — plnt-core's Opus
 * encode is the actual quality bottleneck, not this resampler.
 */
internal class Resampler(private val fromHz: Int, private val toHz: Int) {
    fun process(input: ShortArray, length: Int): FloatArray {
        if (fromHz == toHz) {
            return FloatArray(length) { input[it] / 32768f }
        }
        val ratio = toHz.toDouble() / fromHz.toDouble()
        val outLen = (length * ratio).roundToInt().coerceAtLeast(1)
        val out = FloatArray(outLen)
        for (i in 0 until outLen) {
            val srcPos = i / ratio
            val idx0 = srcPos.toInt().coerceIn(0, length - 1)
            val idx1 = (idx0 + 1).coerceAtMost(length - 1)
            val frac = srcPos - idx0
            val s0 = input[idx0] / 32768f
            val s1 = input[idx1] / 32768f
            out[i] = (s0 + (s1 - s0) * frac).toFloat()
        }
        return out
    }
}

/** Buffers resampled float samples until a full [frameSize]-sample frame is available. */
internal class FloatFrameAccumulator(private val frameSize: Int) {
    private var buf = FloatArray(0)

    fun push(samples: FloatArray, onFrame: (FloatArray) -> Unit) {
        buf += samples
        while (buf.size >= frameSize) {
            onFrame(buf.copyOfRange(0, frameSize))
            buf = buf.copyOfRange(frameSize, buf.size)
        }
    }
}

/**
 * Picks the best available route and, pre-API 31, drives the classic
 * `startBluetoothSco()`/`ACTION_SCO_AUDIO_STATE_UPDATED` handshake so the
 * SCO link is actually up before [AudioEngine] opens its streams against it.
 *
 * On API 31+ `setCommunicationDevice()` supersedes SCO management — Android
 * negotiates the link itself, this just picks which [AudioDeviceInfo] to ask
 * for (preferring Bluetooth SCO > wired > USB > built-in earpiece).
 */
internal class BluetoothScoRouter(
    private val context: Context,
    private val audioManager: AudioManager,
) {
    private var scoReceiverRegistered = false
    private var scoRequested = false

    private val scoStateReceiver = object : BroadcastReceiver() {
        override fun onReceive(ctx: Context?, intent: Intent?) {
            val state = intent?.getIntExtra(AudioManager.EXTRA_SCO_AUDIO_STATE, -1) ?: return
            Log.i(TAG, "SCO_AUDIO_STATE_UPDATED: $state")
            if (state == AudioManager.SCO_AUDIO_STATE_CONNECTED) {
                latch?.countDown()
            }
        }
    }
    private var latch: CountDownLatch? = null

    @SuppressLint("MissingPermission") // BLUETOOTH_CONNECT is required + declared in the manifest
    fun routeToPreferredDeviceAndWait(preference: InputRoute = InputRoute.AUTO) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            routeViaCommunicationDevice(preference)
            return
        }

        // Pre-API 31 there is no per-device selection API beyond SCO on/off;
        // an explicit non-Bluetooth preference just means "don't force SCO"
        // and let Android's own wired > built-in fallback apply.
        if (preference != InputRoute.AUTO && preference != InputRoute.BLUETOOTH) {
            stopSco()
            return
        }

        val hasScoDevice = audioManager.getDevices(AudioManager.GET_DEVICES_OUTPUTS)
            .any { it.type == AudioDeviceInfo.TYPE_BLUETOOTH_SCO }
        if (!hasScoDevice && !audioManager.isBluetoothScoAvailableOffCall) return

        if (!scoReceiverRegistered) {
            context.registerReceiver(scoStateReceiver, IntentFilter(AudioManager.ACTION_SCO_AUDIO_STATE_UPDATED))
            scoReceiverRegistered = true
        }
        latch = CountDownLatch(1)
        audioManager.isBluetoothScoOn = true
        audioManager.startBluetoothSco()
        scoRequested = true
        // Bounded wait: SCO connect is typically <1s but can take longer on
        // some headsets. If it times out we still proceed — better to run on
        // whatever route Android picked than to hang the call setup.
        latch?.await(2, TimeUnit.SECONDS)
    }

    private fun stopSco() {
        if (!scoRequested) return
        audioManager.stopBluetoothSco()
        audioManager.isBluetoothScoOn = false
        scoRequested = false
    }

    @SuppressLint("MissingPermission")
    private fun routeViaCommunicationDevice(preference: InputRoute) {
        val devices = audioManager.availableCommunicationDevices
        val byPreference: List<AudioDeviceInfo> = when (preference) {
            InputRoute.BLUETOOTH -> devices.filter { it.type == AudioDeviceInfo.TYPE_BLUETOOTH_SCO }
            InputRoute.WIRED_HEADSET -> devices.filter { it.type == AudioDeviceInfo.TYPE_WIRED_HEADSET }
            InputRoute.USB_HEADSET -> devices.filter { it.type == AudioDeviceInfo.TYPE_USB_HEADSET }
            InputRoute.BUILTIN_MIC -> devices.filter { it.type == AudioDeviceInfo.TYPE_BUILTIN_EARPIECE }
            InputRoute.AUTO -> emptyList()
        }
        // A specific preference falls back to the auto chain if the requested
        // device has since disappeared (e.g. the headset was unplugged mid-call).
        val preferred = byPreference.firstOrNull()
            ?: devices.firstOrNull { it.type == AudioDeviceInfo.TYPE_BLUETOOTH_SCO }
            ?: devices.firstOrNull { it.type == AudioDeviceInfo.TYPE_WIRED_HEADSET }
            ?: devices.firstOrNull { it.type == AudioDeviceInfo.TYPE_USB_HEADSET }
            ?: devices.firstOrNull { it.type == AudioDeviceInfo.TYPE_BUILTIN_EARPIECE }
        if (preferred != null) {
            val ok = audioManager.setCommunicationDevice(preferred)
            Log.i(TAG, "setCommunicationDevice(${preferred.type}, preference=$preference) -> $ok")
        }
    }

    fun release() {
        stopSco()
        if (scoReceiverRegistered) {
            runCatching { context.unregisterReceiver(scoStateReceiver) }
            scoReceiverRegistered = false
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            audioManager.clearCommunicationDevice()
        }
    }
}
