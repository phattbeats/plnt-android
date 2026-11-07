package com.plnt.client.audio

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class AudioReliabilityPrimitivesTest {
    @Test
    fun partialNonBlockingWritesKeepAllSamplesInOrder() {
        val queue = NonBlockingPcmQueue(maxBufferedSamples = 16)
        val written = mutableListOf<Float>()
        val allowances = ArrayDeque(listOf(2, 0, 6, 2))
        val writer: (FloatArray, Int, Int) -> Int = { samples, offset, size ->
            val accepted = allowances.removeFirst().coerceAtMost(size)
            repeat(accepted) { written += samples[offset + it] }
            accepted
        }

        queue.offer(floatArrayOf(1f, 2f, 3f, 4f), writer)
        assertEquals(2, queue.bufferedSamples)
        queue.offer(floatArrayOf(5f, 6f, 7f, 8f), writer)
        assertEquals(6, queue.bufferedSamples)
        queue.offer(floatArrayOf(9f, 10f), writer)

        assertEquals((1..10).map(Int::toFloat), written)
        assertEquals(0, queue.bufferedSamples)
    }

    @Test
    fun prolongedBackPressureBoundsLatencyByDroppingOldestSamples() {
        val queue = NonBlockingPcmQueue(maxBufferedSamples = 4)
        val fullTrack: (FloatArray, Int, Int) -> Int = { _, _, _ -> 0 }

        queue.offer(floatArrayOf(1f, 2f, 3f, 4f), fullTrack)
        val outcome = queue.offer(floatArrayOf(5f, 6f), fullTrack)

        assertEquals(4, outcome.bufferedSamples)
        assertEquals(2, outcome.droppedSamples)
        assertEquals(4, queue.bufferedSamples)
    }

    @Test
    fun writeErrorClearsStaleQueuedAudioAndReportsTheCode() {
        val queue = NonBlockingPcmQueue(maxBufferedSamples = 8)
        queue.offer(floatArrayOf(1f, 2f), { _, _, _ -> 0 })

        val outcome = queue.offer(floatArrayOf(3f, 4f), { _, _, _ -> -3 })

        assertEquals(-3, outcome.errorCode)
        assertEquals(0, outcome.bufferedSamples)
        assertEquals(0, queue.bufferedSamples)
    }

    @Test
    fun vadUsesHangoverToPreserveWordEndingsButNotSilence() {
        val detector = VoiceActivityDetector(thresholdRms = 0.1f, hangoverFrames = 2)
        val silence = FloatArray(4)
        val voiced = FloatArray(4) { 0.2f }

        assertFalse(detector.process(silence))
        assertTrue(detector.process(voiced))
        assertTrue(detector.process(silence))
        assertTrue(detector.process(silence))
        assertFalse(detector.process(silence))
    }

    @Test
    fun vadResetClosesAnOpenHangoverImmediately() {
        val detector = VoiceActivityDetector(thresholdRms = 0.1f, hangoverFrames = 2)
        detector.process(FloatArray(4) { 0.2f })
        detector.reset()

        assertFalse(detector.process(FloatArray(4)))
    }
}
