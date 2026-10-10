package com.plnt.client.stream

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class TsOfferShapeTest {
    // Trimmed from a real libwebrtc M125 Android offer: Baseline + High H264,
    // VP8, VP9, AV1, each with rtx, plus red/ulpfec and an inactive audio section.
    private val offer = listOf(
        "v=0",
        "o=- 1 2 IN IP4 127.0.0.1",
        "s=-",
        "t=0 0",
        "a=group:BUNDLE 0 1",
        "a=msid-semantic: WMS outgoing_video",
        "m=video 9 UDP/TLS/RTP/SAVPF 96 97 102 103 104 105 98 99 39 40 117 118 119",
        "c=IN IP4 0.0.0.0",
        "a=mid:0",
        "a=sendonly",
        "a=msid:outgoing_video t1",
        "a=rtpmap:96 VP8/90000",
        "a=rtcp-fb:96 nack",
        "a=rtpmap:97 rtx/90000",
        "a=fmtp:97 apt=96",
        "a=rtpmap:102 H264/90000",
        "a=rtcp-fb:102 nack pli",
        "a=fmtp:102 level-asymmetry-allowed=1;packetization-mode=1;profile-level-id=42e01f",
        "a=rtpmap:103 rtx/90000",
        "a=fmtp:103 apt=102",
        "a=rtpmap:104 H264/90000",
        "a=fmtp:104 level-asymmetry-allowed=1;packetization-mode=1;profile-level-id=640c1f",
        "a=rtpmap:105 rtx/90000",
        "a=fmtp:105 apt=104",
        "a=rtpmap:98 VP9/90000",
        "a=rtpmap:99 rtx/90000",
        "a=fmtp:99 apt=98",
        "a=rtpmap:39 AV1/90000",
        "a=rtpmap:40 rtx/90000",
        "a=fmtp:40 apt=39",
        "a=rtpmap:117 red/90000",
        "a=rtpmap:118 rtx/90000",
        "a=fmtp:118 apt=117",
        "a=rtpmap:119 ulpfec/90000",
        "a=candidate:1 1 udp 2122260223 192.168.1.5 50000 typ host",
        "m=audio 9 UDP/TLS/RTP/SAVPF 111",
        "a=mid:1",
        "a=inactive",
        "a=rtpmap:111 opus/48000/2",
        "",
    ).joinToString("\r\n")

    private fun videoPts(sdp: String) =
        sdp.split("\r\n").first { it.startsWith("m=video") }.split(" ").drop(3)

    @Test
    fun keepsHighThenVp8WithTheirRtxOnly() {
        val out = TsOfferShape.apply(offer)!!
        assertEquals(listOf("104", "96", "105", "97"), videoPts(out))
    }

    @Test
    fun neverOffersBaselineH264() {
        val out = TsOfferShape.apply(offer)!!
        assertFalse(out.contains("42e01f"))
        assertFalse(out.contains("a=rtpmap:102 "))
        assertFalse(out.contains("a=rtcp-fb:102 "))
        assertFalse(out.contains("VP9") || out.contains("AV1") || out.contains("ulpfec"))
    }

    @Test
    fun leavesAudioAndSessionLinesAlone() {
        val out = TsOfferShape.apply(offer)!!
        assertTrue(out.contains("m=audio 9 UDP/TLS/RTP/SAVPF 111\r\n"))
        assertTrue(out.contains("a=inactive"))
        assertTrue(out.contains("a=msid:outgoing_video t1"))
        assertTrue(out.endsWith("\r\n"))
    }

    @Test
    fun fallsBackToVp8WhenNoHighProfile() {
        val noHigh = offer.replace("profile-level-id=640c1f", "profile-level-id=42001f")
        assertEquals(listOf("96", "97"), videoPts(TsOfferShape.apply(noHigh)!!))
    }

    @Test
    fun nullWhenNothingSafe() {
        val onlyBaseline = offer
            .replace("profile-level-id=640c1f", "profile-level-id=42001f")
            .replace("VP8/90000", "VP7/90000")
        assertNull(TsOfferShape.apply(onlyBaseline))
    }

    @Test
    fun stripsInlineCandidates() {
        val out = TsOfferShape.withoutCandidates(TsOfferShape.apply(offer)!!)
        assertFalse(out.contains("a=candidate:"))
        assertNotNull(out)
    }
}
