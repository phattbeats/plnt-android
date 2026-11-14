package com.plnt.client.files

import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger

/**
 * Matches `CoreEvent.RawCommand` replies to the request that caused them.
 * Each request carries a unique `return_code`; the server answers with an
 * `error` command echoing it (id=0 is success). Data replies (`notifyfilelist`,
 * `notifystartupload`...) arrive before that `error` and are collected under
 * the same request, which is only possible because the caller has one
 * outstanding request at a time per kind — TeamSpeak does not echo
 * return_code on notify* lines.
 */
class RawReplyCorrelator {
    class Reply(val code: Int, val message: String, val data: List<Pair<String, Map<String, String>>>)

    private class Pending(val onDone: (Reply) -> Unit) { val data = mutableListOf<Pair<String, Map<String, String>>>() }

    private val next = AtomicInteger(1)
    private val pending = ConcurrentHashMap<String, Pending>()
    private val order = java.util.concurrent.ConcurrentLinkedQueue<String>()

    /** Registers a request; returns the return_code to add to the command's args. */
    fun register(onDone: (Reply) -> Unit): String {
        val rc = "plntft${next.getAndIncrement()}"
        pending[rc] = Pending(onDone); order.add(rc)
        return rc
    }

    /** Drops a request whose send failed or timed out. */
    fun forget(rc: String) { pending.remove(rc); order.remove(rc) }

    /** Feed every raw event; returns true if it belonged to a pending request. */
    fun onRaw(name: String, args: Map<String, String>): Boolean {
        if (name == "error") {
            val rc = args["return_code"] ?: return false
            val p = pending.remove(rc) ?: return false
            order.remove(rc)
            p.onDone(Reply(args["id"]?.toIntOrNull() ?: -1, args["msg"].orEmpty(), p.data))
            return true
        }
        if (!name.startsWith("notifyfile") && name != "notifystartupload" &&
            name != "notifystartdownload" && name != "notifystatusfiletransfer") return false
        // Data replies belong to the oldest outstanding request.
        val head = order.peek()?.let { pending[it] } ?: return false
        head.data += name to args
        return true
    }

    fun clear() { pending.clear(); order.clear() }
}

/** Turns a correlated reply into typed results. */
object FileReplies {
    fun <T> toResult(r: RawReplyCorrelator.Reply, ok: (RawReplyCorrelator.Reply) -> T): FileResult<T> =
        if (r.code == TsError.OK) FileResult.Ok(ok(r)) else FileResult.Failed(r.code, r.message)

    fun files(r: RawReplyCorrelator.Reply): List<RemoteFile> = r.data
        .filter { it.first == "notifyfilelist" && it.second["name"] != null }
        .map { (_, a) ->
            RemoteFile(a.getValue("name"), a["size"]?.toLongOrNull() ?: 0L, a["type"] == "0", a["datetime"]?.toLongOrNull() ?: 0L)
        }

    fun grant(r: RawReplyCorrelator.Reply): TransferGrant? =
        r.data.firstNotNullOfOrNull { (n, a) -> if (n == "notifystartupload" || n == "notifystartdownload") TransferGrant.parse(a) else null }
}
