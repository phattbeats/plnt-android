package com.plnt.client.audio

import org.junit.Test
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue

class VoiceActivityGateTest {
    private fun frame(level: Float) = FloatArray(960) { level }

    @Test fun silenceStaysClosed() {
        val g = VoiceActivityGate()
        repeat(50) { assertFalse(g.accept(frame(0.001f))) }
    }

    @Test fun speechOpensAndHangoverHoldsThenCloses() {
        val g = VoiceActivityGate(hangoverFrames = 5)
        assertTrue(g.accept(frame(0.1f)))
        repeat(5) { assertTrue(g.accept(frame(0.0f))) }
        assertFalse(g.accept(frame(0.0f)))
    }

    @Test fun midLevelKeepsOpenViaHysteresis() {
        val g = VoiceActivityGate(hangoverFrames = 2)
        g.accept(frame(0.1f))
        repeat(20) { assertTrue(g.accept(frame(0.012f))) }
    }
}
