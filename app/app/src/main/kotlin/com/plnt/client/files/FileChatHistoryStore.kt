package com.plnt.client.files

import java.io.File

/**
 * File-backed [ChatHistory.Store]: one tab-separated line per message, text
 * escaped, written atomically (temp file + rename) so a crash mid-save cannot
 * lose the existing history.
 */
class FileChatHistoryStore(private val file: File) : ChatHistory.Store {
    override fun load(): List<StoredMessage> {
        if (!file.exists()) return emptyList()
        return file.readLines(Charsets.UTF_8).mapNotNull { decode(it) }
    }

    override fun save(all: List<StoredMessage>) {
        val tmp = File(file.parentFile, file.name + ".tmp")
        tmp.writeText(all.joinToString("\n") { encode(it) }, Charsets.UTF_8)
        if (!tmp.renameTo(file)) { tmp.copyTo(file, overwrite = true); tmp.delete() }
    }

    companion object {
        fun encode(m: StoredMessage) =
            listOf(m.conversation, m.atMillis.toString(), if (m.isSelf) "1" else "0", m.from, m.text).joinToString("\t") { esc(it) }

        fun decode(line: String): StoredMessage? {
            val p = line.split('\t')
            if (p.size != 5) return null
            val at = p[1].toLongOrNull() ?: return null
            return StoredMessage(unesc(p[0]), at, unesc(p[3]), p[2] == "1", unesc(p[4]))
        }

        private fun esc(s: String) = buildString {
            for (c in s) when (c) {
                '\\' -> append("\\\\"); '\t' -> append("\\t"); '\n' -> append("\\n"); '\r' -> append("\\r")
                else -> append(c)
            }
        }

        private fun unesc(s: String) = buildString {
            var i = 0
            while (i < s.length) {
                val c = s[i]
                if (c == '\\' && i + 1 < s.length) {
                    when (s[i + 1]) { 't' -> append('\t'); 'n' -> append('\n'); 'r' -> append('\r'); else -> append(s[i + 1]) }
                    i += 2
                } else { append(c); i++ }
            }
        }
    }
}
