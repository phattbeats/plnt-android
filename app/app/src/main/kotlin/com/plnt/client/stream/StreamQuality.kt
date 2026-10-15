package com.plnt.client.stream

/** What one screen share is encoded at. */
data class StreamQuality(
    /** Long edge of the captured frame, px. Never above the display's own. */
    val longEdge: Int,
    val fps: Int,
    /** Encoder ceiling, and the `bitrate` the server is told in `setupstream`. */
    val bitrateKbps: Int,
)

/**
 * PHA-4176: screen-share video settings. Each of resolution, frame rate and
 * bitrate is either picked in Settings or left on Automatic (0), which means
 * the most the share can carry on an unmetered network (Wi-Fi) and a lighter
 * profile on metered (mobile) data. Pure, so it unit-tests on the host.
 */
object StreamQualityPolicy {
    const val AUTO = 0

    /** Long edges: 1080p, 720p, 480p. */
    val RESOLUTIONS = listOf(1920, 1280, 854)
    val FRAME_RATES = listOf(60, 30, 15)
    val BITRATES_KBPS = listOf(8000, 4500, 2500, 1000)

    val MAX = StreamQuality(longEdge = 1920, fps = 60, bitrateKbps = 8000)
    val METERED = StreamQuality(longEdge = 1280, fps = 30, bitrateKbps = 2500)

    /** Settings values (0 = Automatic) plus the network, to the profile to encode at. */
    fun resolve(resolution: Int, fps: Int, bitrateKbps: Int, metered: Boolean): StreamQuality {
        val auto = if (metered) METERED else MAX
        return StreamQuality(
            longEdge = resolution.takeIf { it in RESOLUTIONS } ?: auto.longEdge,
            fps = fps.takeIf { it in FRAME_RATES } ?: auto.fps,
            bitrateKbps = bitrateKbps.takeIf { it in BITRATES_KBPS } ?: auto.bitrateKbps,
        )
    }

    /**
     * Capture size for a [displayWidth] x [displayHeight] screen: scaled so
     * the long edge is at most [longEdge], never upscaled, aspect kept, both
     * sides even (H264 needs it).
     */
    fun frameSize(longEdge: Int, displayWidth: Int, displayHeight: Int): Pair<Int, Int> {
        val long = maxOf(displayWidth, displayHeight)
        if (long <= longEdge) return (displayWidth / 2 * 2) to (displayHeight / 2 * 2)
        // Integer maths: a float scale lands 2340 * (1920/2340) on 1919.99.
        val short = (minOf(displayWidth, displayHeight).toLong() * longEdge / long).toInt() / 2 * 2
        val edge = longEdge / 2 * 2
        return if (displayWidth >= displayHeight) edge to short else short to edge
    }

    fun resolutionLabel(longEdge: Int): String = when (longEdge) {
        1920 -> "1080p"
        1280 -> "720p"
        854 -> "480p"
        else -> "${longEdge}px"
    }

    fun bitrateLabel(kbps: Int): String =
        if (kbps % 1000 == 0) "${kbps / 1000} Mbps" else "${kbps / 1000.0} Mbps"
}
