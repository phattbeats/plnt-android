package com.plnt.client.stream

import org.junit.Assert.assertEquals
import org.junit.Test

class StreamQualityPolicyTest {
    private val auto = StreamQualityPolicy.AUTO

    @Test
    fun automaticIsMaxOnWifiAndLighterOnMobileData() {
        assertEquals(StreamQualityPolicy.MAX, StreamQualityPolicy.resolve(auto, auto, auto, metered = false))
        assertEquals(StreamQualityPolicy.METERED, StreamQualityPolicy.resolve(auto, auto, auto, metered = true))
        assertEquals(StreamQuality(1920, 60, 8000), StreamQualityPolicy.MAX)
    }

    @Test
    fun aPickedValueWinsOverAutomaticAndTheRestStayAutomatic() {
        assertEquals(StreamQuality(854, 60, 8000), StreamQualityPolicy.resolve(854, auto, auto, metered = false))
        assertEquals(StreamQuality(1280, 15, 1000), StreamQualityPolicy.resolve(auto, 15, 1000, metered = true))
    }

    @Test
    fun anUnknownStoredValueFallsBackToAutomatic() {
        assertEquals(StreamQualityPolicy.MAX, StreamQualityPolicy.resolve(4000, 144, 99, metered = false))
    }

    @Test
    fun frameSizeScalesTheLongEdgeKeepsAspectAndIsEven() {
        // Galaxy S24 portrait, 1080x2340.
        assertEquals(886 to 1920, StreamQualityPolicy.frameSize(1920, 1080, 2340))
        assertEquals(590 to 1280, StreamQualityPolicy.frameSize(1280, 1080, 2340))
        // Never upscaled.
        assertEquals(720 to 1280, StreamQualityPolicy.frameSize(1920, 720, 1280))
        // Landscape.
        assertEquals(1280 to 590, StreamQualityPolicy.frameSize(1280, 2340, 1080))
    }

    @Test
    fun labels() {
        assertEquals("1080p", StreamQualityPolicy.resolutionLabel(1920))
        assertEquals("8 Mbps", StreamQualityPolicy.bitrateLabel(8000))
        assertEquals("4.5 Mbps", StreamQualityPolicy.bitrateLabel(4500))
    }
}
