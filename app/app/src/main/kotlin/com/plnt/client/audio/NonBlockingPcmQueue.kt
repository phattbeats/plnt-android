package com.plnt.client.audio

/**
 * Preserves ordering across partial [android.media.AudioTrack.WRITE_NON_BLOCKING]
 * writes without ever turning an audio callback into a blocking wait.
 *
 * A full track can accept zero samples. In that case the remaining PCM is held
 * until the next callback. The queue is deliberately capped: once the output
 * is more than [maxBufferedSamples] behind, keeping old speech produces a much
 * worse user experience than dropping the oldest unplayed samples.
 */
internal class NonBlockingPcmQueue(
    private val maxBufferedSamples: Int = CORE_FRAME_SAMPLES * MAX_BUFFERED_FRAMES,
) {
    init {
        require(maxBufferedSamples > 0) { "maxBufferedSamples must be positive" }
    }

    data class Outcome(
        val bufferedSamples: Int,
        val droppedSamples: Int,
        /** Negative AudioTrack-style error code, when the writer rejected a call. */
        val errorCode: Int? = null,
    )

    private var pending = FloatArray(0)

    val bufferedSamples: Int
        get() = pending.size

    /** Drops queued audio, for a route change, mute, or a dead output track. */
    fun clear() {
        pending = FloatArray(0)
    }

    /**
     * Tries to drain older audio first, then the new frame. [write] must have
     * the same return contract as AudioTrack.write: number of samples written,
     * zero for temporary back-pressure, or a negative error code.
     */
    fun offer(
        frame: FloatArray,
        write: (samples: FloatArray, offset: Int, size: Int) -> Int,
    ): Outcome {
        val drainError = drain(write)
        if (drainError != null) {
            clear()
            return Outcome(bufferedSamples = 0, droppedSamples = 0, errorCode = drainError)
        }

        // The track is still full. Append the new frame behind the old one so
        // a short partial write cannot reorder speech.
        if (pending.isNotEmpty()) {
            val dropped = append(frame)
            return Outcome(bufferedSamples = pending.size, droppedSamples = dropped)
        }

        val written = write(frame, 0, frame.size)
        if (written < 0) {
            clear()
            return Outcome(bufferedSamples = 0, droppedSamples = 0, errorCode = written)
        }

        val accepted = written.coerceAtMost(frame.size)
        val dropped = if (accepted == frame.size) 0 else append(frame.copyOfRange(accepted, frame.size))
        return Outcome(bufferedSamples = pending.size, droppedSamples = dropped)
    }

    private fun drain(write: (FloatArray, Int, Int) -> Int): Int? {
        while (pending.isNotEmpty()) {
            val written = write(pending, 0, pending.size)
            when {
                written < 0 -> return written
                written == 0 -> return null
                written >= pending.size -> clear()
                else -> pending = pending.copyOfRange(written, pending.size)
            }
        }
        return null
    }

    /**
     * Adds PCM to the tail and bounds latency by discarding only the oldest
     * samples once the cap is exceeded. This path allocates only while the
     * device is back-pressured; a normal full write allocates nothing here.
     */
    private fun append(samples: FloatArray): Int {
        if (samples.isEmpty()) return 0
        val total = pending.size + samples.size
        if (total <= maxBufferedSamples) {
            val combined = FloatArray(total)
            pending.copyInto(combined)
            samples.copyInto(combined, destinationOffset = pending.size)
            pending = combined
            return 0
        }

        val combined = FloatArray(total)
        pending.copyInto(combined)
        samples.copyInto(combined, destinationOffset = pending.size)
        val dropped = total - maxBufferedSamples
        pending = combined.copyOfRange(dropped, total)
        return dropped
    }

    private companion object {
        // 200 ms: enough to absorb a momentary non-blocking short write, short
        // enough that an unhealthy device cannot make remote speech stale.
        const val MAX_BUFFERED_FRAMES = 10
    }
}
