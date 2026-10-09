package com.plnt.client.model

import java.net.URLDecoder
import java.net.URLEncoder

/**
 * One-tap invite (PHA-4108): the standard TeamSpeak server link,
 * `ts3server://host?port=9987&password=..&channel=..`, which every TeamSpeak
 * client since TS3 has understood. PLNT both opens these and produces them,
 * so a link shared from a desktop client works here and one shared from here
 * works on a desktop.
 *
 * Pure JVM on purpose (no android.net.Uri), so it unit-tests on the host.
 * Parameters PLNT does not act on (`token`, `cid`) are accepted and ignored.
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
) {
    /** The link as a URI string, ready to share. Never carries a nickname: everyone picks their own. */
    fun toUri(): String = buildString {
        append("ts3server://")
        append(if (':' in address) "[$address]" else address)
        append("?port=").append(port)
        serverPassword?.let { append("&password=").append(encode(it)) }
        channel?.let { append("&channel=").append(encode(it)) }
        channelPassword?.let { append("&channelpassword=").append(encode(it)) }
        label?.let { append("&addbookmark=").append(encode(it)) }
    }

    companion object {
        const val DEFAULT_PORT = 9987

        /** Schemes we register for: TeamSpeak's own, plus the shorter variants some sites emit. */
        val SCHEMES = listOf("ts3server", "tsserver", "teamspeak")

        private val LINK_IN_TEXT = Regex("""(?i)\b(?:ts3server|tsserver|teamspeak)://[^\s<>"']+""")
        private val HOSTNAME = Regex("""^[A-Za-z0-9]([A-Za-z0-9.-]*[A-Za-z0-9])?$""")
        private val IPV6 = Regex("""^[0-9A-Fa-f:.]+$""")

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
            val schemeEnd = uri.indexOf("://")
            if (schemeEnd <= 0 || uri.substring(0, schemeEnd).lowercase() !in SCHEMES) return null
            val rest = uri.substring(schemeEnd + 3)
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
