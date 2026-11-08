package com.plnt.client.audio

import kotlin.math.sqrt

/**
 * Energy-based voice activity gate for open-mic mode. Pure Kotlin (no third-party
 * DSP, so no licence exposure — see WS3_LICENSE_AUDIT.md). Opens when frame RMS
 * crosses [openThreshold]; stays open for [hangoverFrames] quiet frames so word
 * tails and short pauses are not clipped. Hysteresis: it takes [closeThreshold]
 * (lower than open) to count a frame as quiet.
 */
class VoiceActivityGate(
    private val openThreshold: Float = 0.015f,
    private val closeThreshold: Float = 0.010f,
    private val hangoverFrames: Int = 15, // 300 ms at 20 ms frames
) {
    private var hang = 0

    /** True if this 20 ms frame should be transmitted. */
    fun accept(frame: FloatArray): Boolean {
        val rms = rms(frame)
        when {
            rms >= openThreshold -> hang = hangoverFrames
            rms >= closeThreshold && hang > 0 -> hang = hangoverFrames
            hang > 0 -> hang--
        }
        return hang > 0
    }

    fun reset() { hang = 0 }

    companion object {
        fun rms(frame: FloatArray): Float {
            if (frame.isEmpty()) return 0f
            var s = 0.0
            for (x in frame) s += x * x
            return sqrt(s / frame.size).toFloat()
        }
    }
}
