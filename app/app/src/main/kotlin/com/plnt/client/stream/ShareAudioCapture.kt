package com.plnt.client.stream

import android.annotation.SuppressLint
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioPlaybackCaptureConfiguration
import android.media.AudioRecord
import android.media.projection.MediaProjection
import android.os.Build
import androidx.annotation.RequiresApi
import java.nio.ByteBuffer

/**
 * The audio half of a screen share (PHA-4176): what the phone is playing —
 * Plex, a game, a video — taken with Android's playback capture on the same
 * MediaProjection grant as the picture.
 *
 * WebRTC's Java audio module only knows how to record a microphone, so this
 * is fed in through its `AudioBufferCallback`: every 10 ms buffer the module
 * hands over is overwritten with captured playback before it is encoded.
 * The microphone never reaches the share — on any failure the buffer is
 * zeroed, not left as mic audio. The user's voice goes through the TS voice
 * call as before, and that call's own playback (USAGE_VOICE_COMMUNICATION)
 * cannot be captured, so other people's voices do not echo into the share.
 *
 * Apps that opt out of capture (most DRM video) come through as silence.
 * Playback capture is Android 10+; below that every buffer is silence.
 */
internal class ShareAudioCapture(
    private val projection: MediaProjection,
    private val log: (String) -> Unit,
) {
    @Volatile private var record: AudioRecord? = null
    private var reader: Thread? = null
    private var sampleRate = 0
    private var channels = 0
    @Volatile private var closed = false
    private var failed = false

    // Playback arrives on the reader thread and leaves on WebRTC's record
    // thread; the two 10 ms clocks drift, so a small ring sits between them.
    private val lock = Object()
    private var ring = ByteArray(0)
    private var head = 0
    private var size = 0
    private var primed = false
    private var primeBytes = 0
    private var scratch = ByteArray(0)

    /**
     * WebRTC's record thread. Replaces [bytes] bytes of [buffer] (16-bit PCM,
     * [channelCount] channels at [rate] Hz, as the module recorded it).
     */
    fun fill(buffer: ByteBuffer, audioFormat: Int, channelCount: Int, rate: Int, bytes: Int) {
        if (scratch.size < bytes) scratch = ByteArray(bytes)
        var got = 0
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q &&
            audioFormat == AudioFormat.ENCODING_PCM_16BIT &&
            ensureOpen(rate, channelCount)
        ) {
            got = synchronized(lock) {
                if (!primed && size >= primeBytes) primed = true
                if (primed) take(scratch, bytes) else 0
            }
            // Ran dry: wait for a fresh cushion instead of crackling on every buffer.
            if (got < bytes) synchronized(lock) { primed = false }
        }
        java.util.Arrays.fill(scratch, got, bytes, 0)
        val dst = buffer.duplicate()
        dst.clear()
        dst.put(scratch, 0, minOf(bytes, dst.capacity()))
    }

    fun release() {
        val r: AudioRecord?
        val t: Thread?
        synchronized(this) {
            if (closed) return
            closed = true
            r = record
            t = reader
            record = null
            reader = null
        }
        runCatching { r?.stop() }
        runCatching { t?.join(500) }
        runCatching { r?.release() }
    }

    @SuppressLint("MissingPermission") // The owner only builds this with RECORD_AUDIO granted.
    @RequiresApi(Build.VERSION_CODES.Q)
    @Synchronized
    private fun ensureOpen(rate: Int, channelCount: Int): Boolean {
        if (closed || failed) return false
        if (record != null && rate == sampleRate && channelCount == channels) return true
        stopRecord()
        val mask = if (channelCount >= 2) AudioFormat.CHANNEL_IN_STEREO else AudioFormat.CHANNEL_IN_MONO
        val bytesPerSecond = rate * channelCount.coerceIn(1, 2) * 2
        val rec = runCatching {
            val config = AudioPlaybackCaptureConfiguration.Builder(projection)
                .addMatchingUsage(AudioAttributes.USAGE_MEDIA)
                .addMatchingUsage(AudioAttributes.USAGE_GAME)
                .addMatchingUsage(AudioAttributes.USAGE_UNKNOWN)
                .build()
            val format = AudioFormat.Builder()
                .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                .setSampleRate(rate)
                .setChannelMask(mask)
                .build()
            val min = AudioRecord.getMinBufferSize(rate, mask, AudioFormat.ENCODING_PCM_16BIT)
            AudioRecord.Builder()
                .setAudioFormat(format)
                .setBufferSizeInBytes(maxOf(min, bytesPerSecond / 5))
                .setAudioPlaybackCaptureConfig(config)
                .build()
                .also { check(it.state == AudioRecord.STATE_INITIALIZED) { "AudioRecord not initialized" } }
                .also { it.startRecording() }
        }.getOrElse {
            failed = true
            log("playback capture unavailable, sharing without sound: $it")
            return false
        }
        sampleRate = rate
        channels = channelCount
        synchronized(lock) {
            ring = ByteArray(bytesPerSecond / 2) // 500 ms
            head = 0
            size = 0
            primed = false
            primeBytes = bytesPerSecond / 25 // 40 ms
        }
        record = rec
        val chunk = ByteArray(bytesPerSecond / 100) // 10 ms
        reader = Thread({
            while (!closed && record === rec) {
                val n = rec.read(chunk, 0, chunk.size)
                if (n < 0) {
                    log("playback capture read failed ($n)")
                    break
                }
                if (n > 0) synchronized(lock) { put(chunk, n) }
            }
        }, "plnt-share-audio").apply { isDaemon = true; start() }
        log("playback capture on: ${rate} Hz, $channelCount ch")
        return true
    }

    /** Caller holds the instance lock. */
    private fun stopRecord() {
        val r = record ?: return
        record = null
        runCatching { r.stop() }
        runCatching { reader?.join(500) }
        reader = null
        runCatching { r.release() }
    }

    /** Appends [n] bytes; past capacity the oldest audio goes. Caller holds [lock]. */
    private fun put(src: ByteArray, n: Int) {
        val cap = ring.size
        if (cap == 0) return
        var from = 0
        var len = n
        if (len > cap) { from = len - cap; len = cap }
        val overflow = size + len - cap
        if (overflow > 0) {
            head = (head + overflow) % cap
            size -= overflow
        }
        var tail = (head + size) % cap
        var i = 0
        while (i < len) {
            val run = minOf(len - i, cap - tail)
            System.arraycopy(src, from + i, ring, tail, run)
            tail = (tail + run) % cap
            i += run
        }
        size += len
    }

    /** Moves up to [n] bytes into [dst]; returns how many. Caller holds [lock]. */
    private fun take(dst: ByteArray, n: Int): Int {
        val cap = ring.size
        val len = minOf(n, size)
        var i = 0
        while (i < len) {
            val run = minOf(len - i, cap - head)
            System.arraycopy(ring, head, dst, i, run)
            head = (head + run) % cap
            i += run
        }
        size -= len
        return len
    }
}
