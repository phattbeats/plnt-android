package com.plnt.client.audio

import org.junit.Assert.*
import org.junit.Test

class PcmMixerTest {
    private val out = mutableListOf<FloatArray>()
    private val m = PcmMixer(2, { out += it }, maxFramesPerSource = 2)

    @Test fun twoSpeakersEmitOneSummedFramePerTick() {
        m.push(1, floatArrayOf(0.25f, 0.5f)); m.push(2, floatArrayOf(0.25f, 0.25f)); m.tick()
        assertEquals(1, out.size); assertArrayEquals(floatArrayOf(0.5f, 0.75f), out[0], 0f)
    }

    @Test fun sumIsClamped() {
        m.push(1, floatArrayOf(0.9f, -0.9f)); m.push(2, floatArrayOf(0.9f, -0.9f)); m.tick()
        assertArrayEquals(floatArrayOf(1f, -1f), out[0], 0f)
    }

    @Test fun burstFromOneSourceKeepsNewestAndEmitsOnePerTick() {
        m.push(1, floatArrayOf(1f, 1f)); m.push(1, floatArrayOf(2f, 2f)); m.push(1, floatArrayOf(3f, 3f))
        m.tick(); m.tick(); m.tick()
        assertEquals(2, out.size); assertEquals(2f, out[0][0], 0f); assertEquals(3f, out[1][0], 0f)
    }

    @Test fun idleEmitsNothingAndWrongSizeIgnored() {
        m.tick(); m.push(1, floatArrayOf(1f)); m.tick(); assertTrue(out.isEmpty())
    }

    @Test fun frameIsNotAliasedToProducer() {
        val f = floatArrayOf(0.5f, 0.5f); m.push(1, f); f.fill(0f); m.tick()
        assertEquals(0.5f, out[0][0], 0f)
    }
}
