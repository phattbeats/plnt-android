package com.plnt.client.model

/**
 * PHA-4849: finding the channel an invite named.
 *
 * An invite names a channel either by its id (`42`) or by a path of names
 * (`Movies/Lobby`). The two need different handling: TeamSpeak's connect-time
 * channel (`client_default_channel`) only understands *names* — an id there is
 * ignored, and the client lands in the default channel with nothing to show
 * for it — while a channel *move* takes the id directly. So an id is joined
 * after the channel tree arrives, and only a path of names can be asked for
 * at connect time.
 */
object ChannelPath {
    /** The channel id the spec names, if it names one by id. */
    fun idOf(spec: String?): Long? =
        spec?.trim()?.toLongOrNull()?.takeIf { it > 0 }

    /**
     * The path of channel names the spec names, if it names one by path.
     * Null for an id, and for nothing.
     */
    fun namesOf(spec: String?): List<String>? {
        val trimmed = spec?.trim()?.trim('/') ?: return null
        if (trimmed.isEmpty()) return null
        if (idOf(trimmed) != null) return null
        val parts = trimmed.split('/').map { it.trim() }.filter { it.isNotEmpty() }
        return parts.ifEmpty { null }
    }

    /** True when the spec can be asked for at connect time — a path of names. */
    fun isConnectTime(spec: String?): Boolean = namesOf(spec) != null

    /** The channel the spec names, anywhere in the tree. */
    fun resolve(spec: String?, channels: List<ChannelNode>): ChannelNode? {
        idOf(spec)?.let { id -> return findById(id, channels) }
        val names = namesOf(spec) ?: return null
        return resolveNames(names, channels)
    }

    private fun findById(id: Long, channels: List<ChannelNode>): ChannelNode? {
        for (node in channels) {
            if (node.id == id) return node
            findById(id, node.children)?.let { return it }
        }
        return null
    }

    /** Walk the tree by name, one level per name. */
    private fun resolveNames(names: List<String>, channels: List<ChannelNode>): ChannelNode? {
        val head = names.first()
        val node = channels.firstOrNull { it.name.equals(head, ignoreCase = true) } ?: return null
        if (names.size == 1) return node
        return resolveNames(names.drop(1), node.children)
    }
}
