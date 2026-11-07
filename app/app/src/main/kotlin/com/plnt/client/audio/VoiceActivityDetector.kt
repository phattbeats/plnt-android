package com.plnt.client.audio

/**
 * Lightweight, deterministic voice-activity gate for 20 ms PCM frames.
 *
 * This is intentionally an energy gate rather than a bundled speech model:
 * it adds no third-party binary, has no model/license surface, and keeps the
 * capture callback real-time safe. The fixed threshold is a conservative
 * starting point; physical-device validation remains required because a
 * handset's microphone gain and Android's built-in NS vary by route.
 */
internal class VoiceActivityDetector(
    private val thresholdRms: Float = DEFAULT_THRESHOLD_RMS,
    private val hangoverFrames: Int = DEFAULT_HANGOVER_FRAMES,
) {
    init {
        require(thresholdRms >= 0f) { "thresholdRms must not be negative" }
        require(hangoverFrames >= 0) { "hangoverFrames must not be negative" }
    }

    private var remainingHangoverFrames = 0

    /** True for voiced frames and the short tail after speech stops. */
    fun process(frame: FloatArray): Boolean {
        if (frame.isEmpty()) return false

        var sumSquares = 0.0
        for (sample in frame) {
            val value = sample.toDouble()
            sumSquares += value * value
        }
        val voiced = sumSquares / frame.size >= (thresholdRms * thresholdRms).toDouble()
        if (voiced) {
            remainingHangoverFrames = hangoverFrames
            return true
        }
        if (remainingHangoverFrames == 0) return false

        remainingHangoverFrames -= 1
        return true
    }

    fun reset() {
        remainingHangoverFrames = 0
    }

    private companion object {
        // ~= -36 dBFS RMS. Values above typical silence but below ordinary
        // speech on VOICE_COMMUNICATION capture; device matrix tunes this next.
        const val DEFAULT_THRESHOLD_RMS = 0.015f
        // 12 × 20 ms = 240 ms. Preserves word endings without holding an open
        // microphone through a meaningful silence.
        const val DEFAULT_HANGOVER_FRAMES = 12
    }
}
