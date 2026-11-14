package com.plnt.client.stream

import org.webrtc.PeerConnection

/** One ICE server, WebRTC-free so the policy unit-tests on the host. */
data class IceServerSpec(val url: String, val username: String = "", val credential: String = "")

/**
 * PHA-4852: ICE servers for stream peers. STUN is always present; a relay is
 * only added when the user supplies their own TURN server in Settings. We never
 * ship or host a default relay, so there is no third-party relay spend.
 */
object IceServerConfig {
    val STUN = listOf(
        IceServerSpec("stun:turn.teamspeak.com:3478"),
        IceServerSpec("stun:stun.l.google.com:19302"),
    )

    /** True for a `turn:` / `turns:` URL with a host. */
    fun isValidTurnUrl(url: String): Boolean {
        val u = url.trim()
        val rest = when {
            u.startsWith("turn:", ignoreCase = true) -> u.substring(5)
            u.startsWith("turns:", ignoreCase = true) -> u.substring(6)
            else -> return false
        }
        return rest.substringBefore('?').isNotBlank() && rest.none { it.isWhitespace() }
    }

    /** STUN plus the user's TURN server when [turnUrl] is a valid one. */
    fun build(turnUrl: String, username: String, credential: String): List<IceServerSpec> {
        val url = turnUrl.trim()
        if (!isValidTurnUrl(url)) return STUN
        return STUN + IceServerSpec(url, username.trim(), credential)
    }

    fun toWebRtc(specs: List<IceServerSpec>): List<PeerConnection.IceServer> = specs.map {
        PeerConnection.IceServer.builder(it.url).apply {
            if (it.username.isNotEmpty()) setUsername(it.username)
            if (it.credential.isNotEmpty()) setPassword(it.credential)
        }.createIceServer()
    }
}
