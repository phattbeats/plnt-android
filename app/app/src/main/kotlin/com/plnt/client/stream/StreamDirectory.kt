package com.plnt.client.stream

import com.plnt.client.core.CoreEvent

/**
 * Tracks which clients in the current channel are streaming (PHA-3289).
 *
 * The server pushes `notifystreamstarted` / `notifystreamstopped` for streams
 * in channels we are subscribed to, and answers `requeststreaminfo clid=N`
 * with a `notifystreaminfo` that carries the stream fields only when that
 * client is actually streaming (a bare `return_code`-only reply means "no
 * stream"). Pushes can be missed across a reconnect or a channel move, so the
 * owner also polls every client in its channel on roster changes and on a
 * slow timer — exactly what the desktop client does.
 *
 * Pure state: feed it [CoreEvent.RawCommand]s and tell it when a client leaves
 * the channel; read [streams] back. Threading is the caller's (the view model
 * drives it from the main thread).
 */
class StreamDirectory(private val sendRaw: (name: String, args: Map<String, String>) -> Unit) {
    private val byId = LinkedHashMap<String, StreamInfo>()
    private var nextReturnCode = 70000

    /** Streams currently known, newest last. */
    val streams: List<StreamInfo> get() = byId.values.toList()

    fun byClient(clientId: Long): StreamInfo? = byId.values.firstOrNull { it.streamerClientId == clientId }

    /** Ask the server whether each of [clientIds] is streaming. */
    fun poll(clientIds: Collection<Long>) {
        for (clid in clientIds) {
            val rc = nextReturnCode++
            runCatching {
                sendRaw("requeststreaminfo", mapOf("clid" to clid.toString(), "return_code" to rc.toString()))
            }
        }
    }

    /** Forget everything — on disconnect, or when we move to another channel. */
    fun clear(): Boolean {
        val had = byId.isNotEmpty()
        byId.clear()
        return had
    }

    /** A client left our channel (or the server); their stream is no longer watchable from here. */
    fun clientGone(clientId: Long): Boolean {
        val gone = byId.values.filter { it.streamerClientId == clientId }.map { it.id }
        gone.forEach { byId.remove(it) }
        return gone.isNotEmpty()
    }

    /** Returns true when [streams] changed. */
    fun onRaw(ev: CoreEvent.RawCommand): Boolean {
        val name = ev.name
        if (!name.startsWith("notifystream")) return false
        val a = ev.args
        val id = a["id"] ?: a["stream_id"] ?: a["streamid"] ?: return false
        val isRemoval = name == "notifystreamstopped" || name == "notifystreamended" ||
            name == "notifystreamremoved" || name == "notifystreamdeleted"
        if (isRemoval) return byId.remove(id) != null
        if (name != "notifystreamstarted" && name != "notifystreaminfo" && name != "notifystreamupdated") return false
        val clid = (a["clid"] ?: a["streamer_clid"])?.toLongOrNull() ?: return false
        val info = StreamInfo(
            id = id,
            streamerClientId = clid,
            name = a["name"].orEmpty().ifBlank { "Stream" },
            type = a["type"]?.toIntOrNull() ?: 0,
            hasAudio = a["audio"] == "1",
            viewerCount = a["viewer"]?.toIntOrNull() ?: 0,
        )
        val changed = byId[id] != info
        byId[id] = info
        return changed
    }
}
