package com.plnt.client.model

import java.net.URLDecoder
import java.net.URLEncoder

/**
 * One-tap invite (PHA-4108): the standard TeamSpeak server link,
 * `ts3server://host?port=9987&password=***&channel=..`, which every TeamSpeak
 * client since TS3 has understood. PLNT both opens these and produces them,
 * so a link shared from a desktop client works here and one shared from here
 * works on a desktop.
 *
 * PHA-4849 adds TeamSpeak's own link host, `tmspk.gg`, in both of its shapes:
 * - `tmspk.gg/s=host?port=…` — the same as `teamspeak://host?…`;
 * - `tmspk.gg/<code>` — an *invite*: a code that TeamSpeak's invite service
 *   resolves to a server (and sometimes a channel). The server is not known
 *   until that lookup happens, so [inviteCode] is set and [address] is empty.
 *
 * Pure JVM on purpose (no android.net.Uri), so it is unit-tested on the host.
 * Parameters PLNT does not act on (`token` is acted on — it is a privilege
 * key) are accepted and ignored.
 */
data class ServerLink(
    val address: String,
    val port: Int = DEFAULT_PORT,
    val nickname: String? = null,
    val serverPassword: String? = null,
    /** Channel path, `/`-separated from the top level: `Movies/Lobby`. */
    val channel: String? = null,
    val channelPassword: String? = null,
    /** TeamSpeak's `addbookmark=` — a suggested bookmark label. */
    val label: String? = null,
    /**
     * PHA-4849: the channel the link names by id (`cid=`). A link may carry
     * both a path and an id; the id is the precise one, the path is the
     * human-readable one.
     */
    val channelId: Long? = null,
    /**
     * PHA-4849: a privilege key (`token=` / `privilegekey=`) — rights the
     * server's admin handed out. It is used once, on the connection it comes
     * with, and never written to a log.
     */
    val privilegeKey: String? = null,
    /**
     * PHA-4849: a `tmspk.gg/<code>` invite. The server it points at is
     * looked up on demand — see [TeamSpeakInvite].
     */
    val inviteCode: String? = null,
) {
    /** True when this link names an invite code rather than a server. */
    val isInvite: Boolean get() = inviteCode != null

    /**
     * The link as a URI string, ready to share. Never carries a nickname:
     * everyone picks their own.
     *
     * Never carries a privilege key either. A key is good for one use and
     * grants the holder the issuer's rights; quietly pasting one into a link
     * that gets shared around a room is not a decision this function can make
     * for the user.
     */
    fun toUri(): String = buildString {
        append("ts3server://")
        append(if (':' in address) "[$address]" else address)
        append("?port=").append(port)
        serverPassword?.let { append("&password=").append(encode(it)) }
        channel?.let { append("&channel=").append(encode(it)) }
        channelPassword?.let { append("&channelpassword=").append(encode(it)) }
        channelId?.let { append("&cid=").append(it) }
        label?.let { append("&addbookmark=").append(encode(it)) }
    }

    companion object {
        const val DEFAULT_PORT = 9987

        /** Schemes we register for: TeamSpeak's own, plus the shorter variants some sites emit. */
        val SCHEMES = listOf("ts3server", "tsserver", "teamspeak")

        /**
         * A link inside a blob of text: a TeamSpeak server link, or one of
         * TeamSpeak's own `tmspk.gg` links.
         */
        private val LINK_IN_TEXT = Regex(
            """(?i)\b(?:ts3server|tsserver|teamspeak)://[^\s<>"']+|\b(?:https?://)?(?:www\.)?tmspk\.gg/[^\s<>"']+"""
        )
        private val HOSTNAME = Regex("""^[A-Za-z0-9]([A-Za-z0-9.-]*[A-Za-z0-9])?$""")
        private val IPV6 = Regex("""^[0-9A-Fa-f:.]+$""")

        /**
         * PHA-4849: TeamSpeak's own link host. `tmspk.gg/s=host?…` is a
         * server link; `tmspk.gg/<code>` is an invite.
         */
        private val TMSPK = Regex("""^(?:https?://)?(?:www\.)?tmspk\.gg/(.*)$""", RegexOption.IGNORE_CASE)

        /** An invite code: short, URL-safe, and not empty. */
        private val INVITE_CODE = Regex("""^[A-Za-z0-9_-]{4,64}$""")

        /**
         * Finds and parses the first server link anywhere in [text] — a whole
         * chat message pasted from the clipboard works, not just a bare URI.
         * Returns null when there is no link or it names no usable host.
         */
        fun find(text: String?): ServerLink? {
            if (text.isNullOrBlank()) return null
            val raw = LINK_IN_TEXT.find(text)?.value ?: return null
            return parse(raw.trimEnd('.', ',', ')', '!', '?', ';', ':'))
        }

        fun parse(uri: String): ServerLink? {
            val trimmed = uri.trim()

            // PHA-4849: TeamSpeak's own link host, before anything else — it
            // is not one of our schemes.
            TMSPK.matchEntire(trimmed)?.let { m ->
                val path = m.groupValues[1]
                return if (path.startsWith("s=", ignoreCase = true) || path.startsWith("s/", ignoreCase = true)) {
                    // `tmspk.gg/s=host?port=…` is the same as `teamspeak://host?…`.
                    parse("teamspeak://" + path.substring(2))
                } else {
                    val code = path.substringBefore('?').substringBefore('#').trim().trimEnd('/')
                    if (INVITE_CODE.matches(code)) ServerLink(address = "", inviteCode = code) else null
                }
            }

            val schemeEnd = trimmed.indexOf("://")
            if (schemeEnd <= 0 || trimmed.substring(0, schemeEnd).lowercase() !in SCHEMES) return null
            val rest = trimmed.substring(schemeEnd + 3)
            val queryStart = rest.indexOf('?')
            val authority = (if (queryStart >= 0) rest.substring(0, queryStart) else rest).trimEnd('/')
            val query = parseQuery(if (queryStart >= 0) rest.substring(queryStart + 1).substringBefore('#') else "")

            var (host, inlinePort) = splitHostPort(authority.substringBefore('/'))
            // `teamspeak://connect?address=host:port` shape, seen on some web launchers.
            if (host.equals("connect", ignoreCase = true) && query["address"] != null) {
                val split = splitHostPort(query.getValue("address"))
                host = split.first
                inlinePort = split.second
            }
            if (!isValidHost(host)) return null

            val port = (query["port"]?.toIntOrNull() ?: inlinePort ?: DEFAULT_PORT)
                .takeIf { it in 1..65535 } ?: return null
            return ServerLink(
                address = host,
                port = port,
                nickname = query["nickname"]?.takeIf { it.isNotBlank() },
                serverPassword = query["password"]?.takeIf { it.isNotEmpty() },
                channel = query["channel"]?.trim('/')?.takeIf { it.isNotBlank() },
                channelPassword = query["channelpassword"]?.takeIf { it.isNotEmpty() },
                label = query["addbookmark"]?.takeIf { it.isNotBlank() && it != "1" },
                channelId = query["cid"]?.toLongOrNull()?.takeIf { it > 0 },
                privilegeKey = (query["token"] ?: query["privilegekey"])
                    ?.trim()?.takeIf { it.isNotEmpty() },
            )
        }

        private fun splitHostPort(authority: String): Pair<String, Int?> {
            if (authority.startsWith('[')) {
                val close = authority.indexOf(']')
                if (close < 0) return authority to null
                val port = authority.substring(close + 1).removePrefix(":").toIntOrNull()
                return authority.substring(1, close) to port
            }
            // A bare IPv6 address has several colons and no port.
            if (authority.count { it == ':' } == 1) {
                return authority.substringBefore(':') to authority.substringAfter(':').toIntOrNull()
            }
            return authority to null
        }

        private fun isValidHost(host: String): Boolean =
            host.isNotEmpty() && host.length <= 253 &&
                (HOSTNAME.matches(host) || (':' in host && IPV6.matches(host)))

        private fun parseQuery(query: String): Map<String, String> =
            query.split('&').filter { it.isNotEmpty() }.associate { pair ->
                val key = pair.substringBefore('=').lowercase()
                val value = if ('=' in pair) pair.substringAfter('=') else ""
                key to decode(value)
            }

        private fun decode(s: String): String =
            runCatching { URLDecoder.decode(s, "UTF-8") }.getOrDefault(s)

        // URLEncoder is form encoding; a space must be %20 in a URI query, not '+'.
        private fun encode(s: String): String = URLEncoder.encode(s, "UTF-8").replace("+", "%20")
    }
}
