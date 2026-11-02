package com.plnt.client.service

/**
 * PHA-4856 (WS8): scrubs a diagnostic line before it is written to the trace
 * file that [Diag] may upload. The trace is for *why a connection dropped*,
 * which needs timings, states and error text — not who the user is, where
 * they connect to, or anything that would let someone else get in.
 *
 * Pure, no Android deps, so it is tested on the host.
 */
object DiagRedactor {
    // TeamSpeak command style `key=value`, and `key: value` in our own lines.
    // Any key naming a secret, an identity or an address loses its value.
    private val SENSITIVE_PAIR = Regex(
        """(?i)\b([a-z_]*(?:pass|pw|secret|token|key|auth|cookie|nick|name|uid|unique_identifier|identity|ip|addr|host|hwid|email|login)[a-z_]*)(\s*[=:]\s*)("[^"]*"|\S+)""",
    )
    private val URL = Regex("""(?i)\b[a-z][a-z0-9+.-]*://\S+""")
    private val EMAIL = Regex("""[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}""")
    private val IPV4 = Regex("""\b(?:\d{1,3}\.){3}\d{1,3}\b""")
    // A compressed (`::`) or full (five-plus groups) IPv6 address. Fewer
    // groups would also catch clock times like 12:34:56.
    private val IPV6 = Regex("""(?i)(?:\b[0-9a-f]{1,4})?(?::[0-9a-f]{1,4})*::(?:[0-9a-f]{1,4}(?::[0-9a-f]{1,4})*)?\b|\b(?:[0-9a-f]{1,4}:){4,7}[0-9a-f]{1,4}\b""")
    // Domain names, by top-level domain. A generic "dotted words" rule would
    // also eat the class names in stack traces and thread dumps, which are
    // the useful part of a trace; the lookahead keeps `java.net.Socket` intact.
    private val HOSTNAME = Regex(
        """(?i)\b(?:[a-z0-9](?:[a-z0-9-]{0,61}[a-z0-9])?\.)+(?:com|net|org|io|gg|de|uk|vip|tech|club|me|tv|eu|us|ca|au|ru|fr|nl|pl|se|no|fi|dk|at|ch|be|cz|it|es|info|xyz|app|dev|co|cc|biz|online|site|host|server|games|lan|local|home|arpa)(?![\w.(-])""",
    )
    // Keys, identities, tokens: long runs of base64/hex with no spaces.
    private val BLOB = Regex("""[A-Za-z0-9+/=_-]{32,}""")

    fun redact(line: String): String = line
        .replace(URL, "<url>")
        .replace(EMAIL, "<email>")
        .replace(SENSITIVE_PAIR) { m -> m.groupValues[1] + m.groupValues[2] + "<redacted>" }
        .replace(BLOB, "<blob>")
        .replace(IPV4, "<ip>")
        .replace(IPV6, "<ip>")
        .replace(HOSTNAME, "<host>")
}
