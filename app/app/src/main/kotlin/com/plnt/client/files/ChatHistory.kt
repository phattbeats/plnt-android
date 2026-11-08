package com.plnt.client.files

/** One persisted chat line. [conversation] is `channel:<id>` or `private:<clientUid>`. */
data class StoredMessage(val conversation: String, val atMillis: Long, val from: String, val isSelf: Boolean, val text: String)

/**
 * Durable-history model (PHA-4853): append, retention, delete, export, unread.
 * Storage is behind [Store] so Room/DataStore/file can back it; the logic here
 * is pure and unit-tested.
 */
class ChatHistory(private val store: Store, private val clock: () -> Long = System::currentTimeMillis) {
    interface Store {
        fun load(): List<StoredMessage>
        fun save(all: List<StoredMessage>)
    }

    private val messages = store.load().toMutableList()
    private val lastRead = mutableMapOf<String, Long>()
    /** Peers who poked us and haven't been acknowledged. */
    private val pokes = mutableListOf<Pair<String, String>>()

    @Synchronized fun append(m: StoredMessage) { messages += m; store.save(messages) }

    @Synchronized fun conversation(id: String) = messages.filter { it.conversation == id }.sortedBy { it.atMillis }

    @Synchronized fun unread(id: String): Int =
        messages.count { it.conversation == id && !it.isSelf && it.atMillis > (lastRead[id] ?: 0L) }

    @Synchronized fun totalUnread() = messages.map { it.conversation }.distinct().sumOf { unread(it) }

    @Synchronized fun markRead(id: String) { lastRead[id] = clock() }

    /** Drops messages older than [days]; returns how many were removed. 0 days = keep forever. */
    @Synchronized fun applyRetention(days: Int): Int {
        if (days <= 0) return 0
        val cutoff = clock() - days * 86_400_000L
        val before = messages.size
        messages.removeAll { it.atMillis < cutoff }
        if (messages.size != before) store.save(messages)
        return before - messages.size
    }

    @Synchronized fun delete(id: String): Int {
        val n = messages.count { it.conversation == id }
        messages.removeAll { it.conversation == id }
        lastRead.remove(id)
        if (n > 0) store.save(messages)
        return n
    }

    @Synchronized fun poke(from: String, text: String) { pokes += from to text }
    @Synchronized fun pendingPokes(): List<Pair<String, String>> = pokes.toList()
    @Synchronized fun ackPokes() = pokes.clear()

    /** Plain-text export, one line per message, oldest first. */
    @Synchronized fun export(id: String): String = conversation(id).joinToString("\n") {
        "[${java.time.Instant.ofEpochMilli(it.atMillis)}] ${it.from}: ${it.text.replace("\n", "\n    ")}"
    }
}
