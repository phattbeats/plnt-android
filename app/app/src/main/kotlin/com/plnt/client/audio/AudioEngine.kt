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
import com.plnt.client.model.AudioDeviceOption
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
    /** Fires with (inputs, outputs) whenever the physically-present device set changes (plug/unplug, BT connect). */
    private val onAudioDevicesChanged: (List<AudioDeviceOption>, List<AudioDeviceOption>) -> Unit = { _, _ -> },
) {
    private val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager

    private val running = AtomicBoolean(false)
    /** Gates whether captured frames are forwarded to [onCaptureFrame]. Capture itself never stops. */
    private val sending = AtomicBoolean(false)

    // PHA-3282 device overrides, both null (= Automatic) by default. See
    // [setPreferredInputDevice] for what "Automatic" costs: nothing at all.
    @Volatile private var preferredInputKey: String? = null
    @Volatile private var preferredOutputKey: String? = null

    private var captureThread: Thread? = null
    private var record: AudioRecord? = null
    private var echoCanceler: AcousticEchoCanceler? = null
    private var noiseSuppressor: NoiseSuppressor? = null

    private val playbackTrack = AtomicReference<AudioTrack?>(null)

    private val deviceCallback = object : AudioDeviceCallback() {
        override fun onAudioDevicesAdded(addedDevices: Array<out AudioDeviceInfo>) {
            emitDeviceLists()
            if (addedDevices.any { it.isRelevant() }) rehome("device added: ${addedDevices.joinToString { it.describe() }}")
        }

        override fun onAudioDevicesRemoved(removedDevices: Array<out AudioDeviceInfo>) {
            emitDeviceLists()
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
        emitDeviceLists()
        // Route to the preferred device *before* opening the streams —
        // AudioRecord/AudioTrack pick up whatever route is active at open
        // time, they don't hot-follow it.
        scoRouter.routeToPreferredDeviceAndWait(preferredInputKey, preferredOutputKey)

        openPlaybackTrack()
        openCaptureAndStartThread()
        Log.i(TAG, "AudioEngine started (mode=MODE_IN_COMMUNICATION, in=${preferredInputKey ?: "auto"}, out=${preferredOutputKey ?: "auto"})")
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
     * User-initiated override of the auto-priority chain (PHA-3282: pick a specific
     * mic; supersedes PHA-3132's coarser route-category override). `null` restores
     * Automatic. Re-routes immediately if a call is live — unlike [setSending] this
     * is a deliberate route change, not a PTT toggle, so re-opening the streams here
     * is correct (see the class doc for why PTT itself must not do this).
     */
    fun setPreferredInputDevice(key: String?) {
        if (preferredInputKey == key) return
        preferredInputKey = key
        if (running.get()) rehome("input device changed to ${key ?: "automatic"}")
    }

    /** Output half of [setPreferredInputDevice]; `null` restores Automatic. */
    fun setPreferredOutputDevice(key: String?) {
        if (preferredOutputKey == key) return
        preferredOutputKey = key
        if (running.get()) rehome("output device changed to ${key ?: "automatic"}")
    }

    private fun emitDeviceLists() {
        onAudioDevicesChanged(
            audioManager.getDevices(AudioManager.GET_DEVICES_INPUTS).toOptions(),
            audioManager.getDevices(AudioManager.GET_DEVICES_OUTPUTS).toOptions(),
        )
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
        scoRouter.routeToPreferredDeviceAndWait(preferredInputKey, preferredOutputKey)
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
                // Automatic (null) never calls setPreferredDevice, so the route is
                // decided solely by scoRouter's chain — byte-for-byte the pre-PHA-3282
                // behaviour. A pinned device that is no longer present resolves to null
                // and is likewise skipped, falling back to Automatic rather than failing
                // to open the stream.
                preferredInputKey?.let { key -> findDevice(AudioManager.GET_DEVICES_INPUTS, key)?.let(rec::setPreferredDevice) }
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
        // Same reasoning as buildAudioRecord(): Automatic leaves this untouched.
        preferredOutputKey?.let { key -> findDevice(AudioManager.GET_DEVICES_OUTPUTS, key)?.let(track::setPreferredDevice) }
        track.play()
        playbackTrack.set(track)
    }

    private fun findDevice(flags: Int, key: String): AudioDeviceInfo? =
        audioManager.getDevices(flags).firstOrNull { it.routeKey() == key }

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
 * Identifier for one [AudioDeviceInfo] as persisted in Settings — see
 * [AudioDeviceOption] for why this is type+address and not the platform id.
 */
internal fun AudioDeviceInfo.routeKey(): String = "$type:$address"

/** The `AudioDeviceInfo.type` a [routeKey] names, or null if the key is malformed. */
internal fun keyDeviceType(key: String): Int? = key.substringBefore(':').toIntOrNull()

private fun deviceTypeLabel(type: Int): String = when (type) {
    AudioDeviceInfo.TYPE_BUILTIN_MIC -> "Phone mic"
    AudioDeviceInfo.TYPE_BUILTIN_EARPIECE -> "Earpiece"
    AudioDeviceInfo.TYPE_BUILTIN_SPEAKER -> "Speaker"
    AudioDeviceInfo.TYPE_WIRED_HEADSET -> "Wired headset"
    AudioDeviceInfo.TYPE_WIRED_HEADPHONES -> "Wired headphones"
    AudioDeviceInfo.TYPE_BLUETOOTH_SCO -> "Bluetooth"
    AudioDeviceInfo.TYPE_BLUETOOTH_A2DP -> "Bluetooth (media)"
    AudioDeviceInfo.TYPE_USB_HEADSET -> "USB headset"
    AudioDeviceInfo.TYPE_USB_DEVICE -> "USB audio"
    AudioDeviceInfo.TYPE_DOCK -> "Dock"
    AudioDeviceInfo.TYPE_HDMI -> "HDMI"
    AudioDeviceInfo.TYPE_TELEPHONY -> "Telephony"
    else -> "Audio device"
}

/**
 * "<product name> (<type>)", or just the type when the platform reports no
 * useful product name — which it routinely doesn't for built-in devices, where
 * `productName` is the phone's model name and would read as the same string on
 * every single row.
 */
internal fun AudioDeviceInfo.routeLabel(): String {
    val typeLabel = deviceTypeLabel(type)
    val name = productName?.toString()?.trim()
        ?.takeIf { it.isNotEmpty() && !it.equals("unknown", ignoreCase = true) && !it.equals(typeLabel, ignoreCase = true) }
    // Built-ins all carry the handset's model name; showing it would produce
    // "Pixel 8 (Phone mic)" / "Pixel 8 (Speaker)" and add nothing.
    return if (name == null || type in builtInDeviceTypes) typeLabel else "$name ($typeLabel)"
}

internal fun AudioDeviceInfo.toOption(): AudioDeviceOption = AudioDeviceOption(routeKey(), routeLabel())

/**
 * A `getDevices()` result as picker rows, collapsing entries that share a
 * [routeKey]. Handsets routinely report several `TYPE_BUILTIN_MIC` elements —
 * bottom, top, back — all with an empty address, which would otherwise render as
 * two or three identical "Phone mic" rows that highlight as one. They really are
 * a single choice here: [AudioEngine.findDevice] takes the first key match
 * either way, and the platform picks among the physical capsules itself.
 */
internal fun Array<AudioDeviceInfo>.toOptions(): List<AudioDeviceOption> =
    map { it.toOption() }.distinctBy { it.key }

private val bluetoothDeviceTypes = setOf(
    AudioDeviceInfo.TYPE_BLUETOOTH_SCO,
    AudioDeviceInfo.TYPE_BLUETOOTH_A2DP,
)

private val builtInDeviceTypes = setOf(
    AudioDeviceInfo.TYPE_BUILTIN_MIC,
    AudioDeviceInfo.TYPE_BUILTIN_EARPIECE,
    AudioDeviceInfo.TYPE_BUILTIN_SPEAKER,
    AudioDeviceInfo.TYPE_TELEPHONY,
)

/**
 * Device enumeration that needs only a [Context], not a live [AudioEngine]
 * (PHA-3282). Settings has to list devices before the user has ever connected,
 * and there is no engine until [com.plnt.client.service.VoiceService] starts a
 * call — while a call *is* live the engine pushes fresher lists through its own
 * device callback instead.
 */
object AudioDevices {
    fun listInputs(context: Context): List<AudioDeviceOption> = list(context, AudioManager.GET_DEVICES_INPUTS)

    fun listOutputs(context: Context): List<AudioDeviceOption> = list(context, AudioManager.GET_DEVICES_OUTPUTS)

    private fun list(context: Context, flags: Int): List<AudioDeviceOption> {
        val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        return audioManager.getDevices(flags).toOptions()
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

    /**
     * [inputKey] / [outputKey] are [AudioDeviceOption] keys, or null for Automatic.
     * Both null is the pre-PHA-3282 path exactly: the priority chain below picks the
     * route on its own, which is what PHA-3077/PHA-3080 verified on real hardware.
     *
     * `setCommunicationDevice()` is a single device for both directions, so when the
     * user has pinned each end to a different device the *input* pin wins here — the
     * mic side is what SCO actually gates, and the playback stream still carries its
     * own `AudioTrack.setPreferredDevice()`.
     */
    @SuppressLint("MissingPermission") // BLUETOOTH_CONNECT is required + declared in the manifest
    fun routeToPreferredDeviceAndWait(inputKey: String? = null, outputKey: String? = null) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            routeViaCommunicationDevice(inputKey, outputKey)
            return
        }

        // Pre-API 31 there is no per-device selection API beyond SCO on/off, so a pin
        // collapses to the one bit that API exposes: does the user want the Bluetooth
        // link or not. A non-Bluetooth pin means "don't force SCO" and lets Android's
        // own wired > built-in fallback apply.
        val pinnedType = keyDeviceType(inputKey ?: outputKey ?: "")
        if (pinnedType != null && pinnedType !in bluetoothDeviceTypes) {
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
    private fun routeViaCommunicationDevice(inputKey: String?, outputKey: String?) {
        val devices = audioManager.availableCommunicationDevices
        // `availableCommunicationDevices` are output devices, so an output pin can
        // match one outright; an input pin has to be mapped to the output that shares
        // its route (a pinned built-in mic means the earpiece, etc.) — the same
        // mapping PHA-3132's InputRoute.BUILTIN_MIC -> TYPE_BUILTIN_EARPIECE used.
        val pinned = inputKey?.let { key ->
            communicationTypeForInput(keyDeviceType(key))?.let { t -> devices.firstOrNull { it.type == t } }
        } ?: outputKey?.let { key ->
            devices.firstOrNull { it.routeKey() == key }
                ?: keyDeviceType(key)?.let { t -> devices.firstOrNull { it.type == t } }
        }
        // A pin falls back to the auto chain if the requested device has since
        // disappeared (e.g. the headset was unplugged mid-call). With no pin at all
        // this is the only branch that runs, unchanged from before PHA-3282.
        val preferred = pinned
            ?: devices.firstOrNull { it.type == AudioDeviceInfo.TYPE_BLUETOOTH_SCO }
            ?: devices.firstOrNull { it.type == AudioDeviceInfo.TYPE_WIRED_HEADSET }
            ?: devices.firstOrNull { it.type == AudioDeviceInfo.TYPE_USB_HEADSET }
            ?: devices.firstOrNull { it.type == AudioDeviceInfo.TYPE_BUILTIN_EARPIECE }
        if (preferred != null) {
            val ok = audioManager.setCommunicationDevice(preferred)
            Log.i(TAG, "setCommunicationDevice(${preferred.type}, in=${inputKey ?: "auto"}, out=${outputKey ?: "auto"}) -> $ok")
        }
    }

    /** The communication (output) device type that shares a route with a pinned input type. */
    private fun communicationTypeForInput(inputType: Int?): Int? = when (inputType) {
        AudioDeviceInfo.TYPE_BLUETOOTH_SCO -> AudioDeviceInfo.TYPE_BLUETOOTH_SCO
        AudioDeviceInfo.TYPE_WIRED_HEADSET -> AudioDeviceInfo.TYPE_WIRED_HEADSET
        AudioDeviceInfo.TYPE_USB_HEADSET -> AudioDeviceInfo.TYPE_USB_HEADSET
        AudioDeviceInfo.TYPE_USB_DEVICE -> AudioDeviceInfo.TYPE_USB_DEVICE
        AudioDeviceInfo.TYPE_BUILTIN_MIC -> AudioDeviceInfo.TYPE_BUILTIN_EARPIECE
        else -> null
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
