package com.plnt.client.stream

/**
 * Reshapes a libwebrtc screen-share offer into what a TeamSpeak 6 desktop
 * viewer can safely take (PHA-3289). Pure string work so it unit-tests on the
 * host.
 *
 * Video codecs are cut down to the ones TS streamers themselves send:
 *  - **H264 High** (`profile-level-id=64xxxx`), preferred, and
 *  - **VP8** as the fallback every libwebrtc build can encode in software,
 * plus each kept codec's `rtx`. Everything else goes, above all **H264
 * Constrained Baseline (42e01f)**: the TS6 6.0 viewer accepts it in its answer
 * and then crashes the whole client because it has no decoder for it.
 */
object TsOfferShape {

    /** The shaped SDP, or null if no TeamSpeak-safe video codec is on offer. */
    fun apply(sdp: String): String? {
        val nl = if (sdp.contains("\r\n")) "\r\n" else "\n"
        val lines = sdp.split(nl).let { if (it.lastOrNull() == "") it.dropLast(1) else it }

        // Section boundaries: session header, then one block per m= line.
        val sections = mutableListOf<MutableList<String>>(mutableListOf())
        for (l in lines) {
            if (l.startsWith("m=")) sections.add(mutableListOf())
            sections.last().add(l)
        }

        var foundVideo = false
        val out = mutableListOf<String>()
        out += sections.first()
        for (sec in sections.drop(1)) {
            if (!sec.first().startsWith("m=video")) {
                out += sec
                continue
            }
            val shaped = shapeVideo(sec) ?: return null
            foundVideo = true
            out += shaped
        }
        if (!foundVideo) return null
        return out.joinToString(nl) + nl
    }

    /** Drops every inline `a=candidate` / `a=end-of-candidates`; TS trickles them instead. */
    fun withoutCandidates(sdp: String): String {
        val nl = if (sdp.contains("\r\n")) "\r\n" else "\n"
        return sdp.split(nl)
            .filterNot { it.startsWith("a=candidate:") || it.startsWith("a=end-of-candidates") }
            .joinToString(nl)
    }

    private fun shapeVideo(sec: List<String>): List<String>? {
        val rtpmap = HashMap<String, String>() // pt -> "H264/90000"
        val fmtp = HashMap<String, String>()   // pt -> params
        for (l in sec) {
            Regex("^a=rtpmap:(\\d+) (\\S+)").find(l)?.let { rtpmap[it.groupValues[1]] = it.groupValues[2] }
            Regex("^a=fmtp:(\\d+) (.*)$").find(l)?.let { fmtp[it.groupValues[1]] = it.groupValues[2] }
        }
        val mParts = sec.first().split(" ")
        if (mParts.size < 4) return null
        val offered = mParts.drop(3)

        fun codec(pt: String) = rtpmap[pt]?.substringBefore('/')?.lowercase()
        fun isHigh(pt: String): Boolean {
            if (codec(pt) != "h264") return false
            val pli = Regex("profile-level-id=([0-9a-fA-F]{6})").find(fmtp[pt] ?: "")?.groupValues?.get(1) ?: return false
            return pli.lowercase().startsWith("64")
        }

        val high = offered.filter(::isHigh)
        val vp8 = offered.filter { codec(it) == "vp8" }
        val primary = high + vp8
        if (primary.isEmpty()) return null
        // Each kept codec's rtx, in the same order as the codecs themselves.
        val rtx = primary.mapNotNull { p ->
            offered.firstOrNull { pt ->
                codec(pt) == "rtx" && Regex("apt=(\\d+)").find(fmtp[pt] ?: "")?.groupValues?.get(1) == p
            }
        }
        val keep = (primary + rtx).toSet()
        val order = primary + rtx

        val out = mutableListOf<String>()
        out += (mParts.take(3) + order).joinToString(" ")
        for (l in sec.drop(1)) {
            val pt = Regex("^a=(?:rtpmap|fmtp|rtcp-fb):(\\d+)").find(l)?.groupValues?.get(1)
            if (pt != null && pt !in keep) continue
            out += l
        }
        return out
    }
}
