package com.plnt.client.model

import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder

/**
 * PHA-4849: a TeamSpeak *invite* — the `tmspk.gg/<code>` link the TeamSpeak
 * app produces with "Create Invite".
 *
 * The code is not an address: it is looked up in TeamSpeak's own invite
 * service, which answers with the server it points at and, when the invite
 * is to a channel, that channel too. An invite expires (30 days by default);
 * after that the service no longer knows it.
 */
data class TeamSpeakInvite(
    /** The server's address, as the service gives it — `host` or `host:port`. */
    val address: String,
    /** The server's name, when the service knows one. */
    val name: String? = null,
    /** The channel the invite points at, when it points at one. */
    val channelId: Long? = null,
    /** That channel's name. */
    val channelName: String? = null,
) {
    /**
     * The port the invite's address names, or null when it names none.
     *
     * A hostname without a port is left without one on purpose: TeamSpeak's
     * own clients then let the server's DNS entry (`_ts3._udp.` SRV, or
     * TSDNS) say which host and port to use, which is not always the
     * default 9987.
     */
    val explicitPort: Int?
        get() {
            val host = address.substringBefore(':')
            if (host.contains(':')) return null // a bare IPv6 address has colons of its own
            val port = address.substringAfter(':', "").toIntOrNull() ?: return null
            return port.takeIf { it in 1..65535 }
        }

    /** The address without any port, so it can be handed to the resolver as it is. */
    val host: String
        get() {
            val bracketed = address.startsWith("[")
            if (bracketed) {
                val close = address.indexOf(']')
                if (close > 0) return address.substring(0, close + 1)
            }
            if (address.count { it == ':' } == 1) return address.substringBefore(':')
            return address
        }
}

/**
 * TeamSpeak's invite service — the same one the desktop client asks when
 * someone opens an invite link.
 */
object TeamSpeakInviteService {
    /** TeamSpeak's own invite service. */
    const val ENDPOINT = "https://invites.teamspeak.com/query/?id="

    /** How long to wait for the service before giving up. */
    const val TIMEOUT_MS = 8_000

    /** The URL the code is looked up at. */
    fun lookupUrl(code: String): String = ENDPOINT + URLEncoder.encode(code, "UTF-8")

    /**
     * The invite in the service's answer, or null when it is not one we can
     * use: not to a server, expired by its own time, or without an address.
     *
     * [nowSeconds] is the clock to judge expiry against — a parameter so a
     * test can pin it.
     */
    fun parse(json: String, nowSeconds: Long = System.currentTimeMillis() / 1000): TeamSpeakInvite? {
        val obj = runCatching { JSONObject(json) }.getOrNull() ?: return null
        val type = obj.optString("type").takeIf { it.isNotEmpty() }
        if (type != null && type != "server") return null
        val expires = obj.optLong("expires", 0L)
        if (expires in 1 until nowSeconds) return null
        val address = obj.optString("address").trim()
        if (address.isEmpty()) return null
        val channelId = obj.optLong("channel_id", 0L).takeIf { it > 0 }
        return TeamSpeakInvite(
            address = address,
            name = obj.optString("name").trim().takeIf { it.isNotEmpty() },
            channelId = channelId,
            channelName = obj.optString("channel_name").trim().takeIf { it.isNotEmpty() },
        )
    }

    /**
     * Look the code up over the network. Null when the service does not know
     * the invite (it expired, or never existed) or could not be reached.
     */
    fun lookup(code: String, timeoutMs: Int = TIMEOUT_MS): TeamSpeakInvite? {
        val body = fetch(lookupUrl(code), timeoutMs) ?: return null
        return parse(body)
    }

    /**
     * The service's answer, or null when it could not be reached or does not
     * know the invite. The body is capped: an invite is a few hundred bytes,
     * and an unbounded read is an invitation to a slow server.
     */
    private fun fetch(url: String, timeoutMs: Int): String? = runCatching {
        val con = (URL(url).openConnection() as HttpURLConnection).apply {
            connectTimeout = timeoutMs
            readTimeout = timeoutMs
            instanceFollowRedirects = false
            requestMethod = "GET"
        }
        try {
            if (con.responseCode != 200) return@runCatching null
            con.inputStream.use { input ->
                val out = java.io.ByteArrayOutputStream()
                val buf = ByteArray(4096)
                while (out.size() < 16_384) {
                    val n = input.read(buf)
                    if (n < 0) break
                    out.write(buf, 0, n)
                }
                out.toString(Charsets.UTF_8.name())
            }
        } finally {
            con.disconnect()
        }
    }.getOrNull()
}
