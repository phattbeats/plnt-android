package com.plnt.client.files

/**
 * Path and filename hygiene for the TeamSpeak file browser (PHA-4853).
 *
 * Two directions matter: names the *server* sends us must never escape the
 * download folder on the device, and paths we send the server must not walk
 * out of the channel root. Pure, no Android deps.
 */
object FilePaths {
    private const val MAX_NAME = 120
    private val RESERVED = setOf("", ".", "..")

    /** Turns a remote file name into a single safe local file name (never a path). */
    fun safeLocalName(remote: String): String {
        // Take the last segment on either separator, so `..\..\x` and `a/b/x` both reduce to `x`.
        val last = remote.substringAfterLast('/').substringAfterLast('\\')
        val cleaned = buildString {
            for (c in last) append(if (c.isISOControl() || c in "<>:\"|?*\\/") '_' else c)
        }.trim().trimEnd('.')
        val name = if (cleaned in RESERVED) "file" else cleaned
        return if (name.length <= MAX_NAME) name else {
            val ext = name.substringAfterLast('.', "").take(12)
            if (ext.isEmpty() || ext.length == name.length) name.take(MAX_NAME)
            else name.take(MAX_NAME - ext.length - 1) + "." + ext
        }
    }

    /** Picks a name not in [taken] by appending ` (n)` before the extension. */
    fun uniqueName(name: String, taken: Set<String>): String {
        if (name !in taken) return name
        val stem = name.substringBeforeLast('.', name)
        val ext = name.substringAfterLast('.', "").let { if (it.isEmpty() || stem == name) "" else ".$it" }
        var n = 1
        while ("$stem ($n)$ext" in taken) n++
        return "$stem ($n)$ext"
    }

    /**
     * Normalises a remote path to `/a/b` form. Returns null if it contains `..`
     * segments, NULs or control characters — rejected, never silently resolved,
     * so a traversal attempt is visible instead of landing somewhere unexpected.
     */
    fun normalizeRemote(path: String): String? {
        if (path.any { it.isISOControl() } || '\\' in path) return null
        val parts = path.split('/').filter { it.isNotEmpty() && it != "." }
        if (parts.any { it == ".." }) return null
        return "/" + parts.joinToString("/")
    }

    /** Joins a normalised directory and a single child name; null if the name isn't a plain name. */
    fun child(dir: String, name: String): String? {
        val base = normalizeRemote(dir) ?: return null
        if (name in RESERVED || '/' in name || '\\' in name || name.any { it.isISOControl() }) return null
        return if (base == "/") "/$name" else "$base/$name"
    }
}
