package com.plnt.client.admin

import java.security.MessageDigest
import java.util.Base64

/**
 * PHA-4854 — administration: move / kick / ban, channel create / edit /
 * reorder / delete, and readable denied-operation errors.
 *
 * Pure on purpose: an [AdminOp] becomes the command for core `sendRawCommand`
 * (the core escapes the values), and the server's `error` reply becomes an
 * [AdminResult] a person can act on. The server is the authority on
 * permissions — the app never guesses, it sends the command and reports the
 * answer.
 */
sealed class AdminOp {
    /** Move any client (not just ourselves) into a channel. */
    data class MoveClient(val clientId: Long, val channelId: Long, val password: String? = null) : AdminOp()
    /** Kick from the channel (back to the default channel) or from the server. */
    data class Kick(val clientId: Long, val fromServer: Boolean, val reason: String = "") : AdminOp()
    /** Ban by client id. [seconds] == 0 is permanent. */
    data class Ban(val clientId: Long, val seconds: Long = 0, val reason: String = "") : AdminOp()
    data class CreateChannel(
        val name: String,
        val parentId: Long? = null,
        val password: String? = null,
        val topic: String? = null,
        val maxClients: Int? = null,
        val permanent: Boolean = true,
    ) : AdminOp()
    /** Only the non-null fields are sent. An empty [password] removes the channel's password. */
    data class EditChannel(
        val channelId: Long,
        val name: String? = null,
        val topic: String? = null,
        val password: String? = null,
        val maxClients: Int? = null,
    ) : AdminOp()
    /** Re-parent a channel and/or place it after [orderAfterId] (0 = first). */
    data class MoveChannel(val channelId: Long, val parentId: Long, val orderAfterId: Long = 0) : AdminOp()
    data class DeleteChannel(val channelId: Long, val force: Boolean = false) : AdminOp()

    /** Short label for confirmations and error text. */
    val label: String
        get() = when (this) {
            is MoveClient -> "move user"
            is Kick -> if (fromServer) "kick from server" else "kick from channel"
            is Ban -> "ban user"
            is CreateChannel -> "create channel"
            is EditChannel -> "edit channel"
            is MoveChannel -> "reorder channel"
            is DeleteChannel -> "delete channel"
        }

    /** Destructive operations the UI must confirm before sending. */
    val needsConfirmation: Boolean
        get() = this is Kick || this is Ban || this is DeleteChannel
}

/** A command ready for core `sendRawCommand`; [returnCode] correlates the reply. */
data class AdminCommand(val name: String, val args: Map<String, String>, val returnCode: String)

class AdminInvalid(message: String) : IllegalArgumentException(message)

/** TeamSpeak error ids an admin operation can hit. */
object AdminError {
    const val OK = 0
    const val INVALID_CLIENT = 512
    const val INVALID_CHANNEL = 768
    const val CHANNEL_NAME_IN_USE = 771
    const val CHANNEL_NOT_EMPTY = 772
    const val CANT_DELETE_DEFAULT = 773
    const val INVALID_CHANNEL_PASSWORD = 781
    const val INSUFFICIENT_PERMISSIONS = 2568
}

/** Outcome of an admin command, decoded from the server's `error` reply. */
data class AdminResult(
    val ok: Boolean,
    val errorId: Int,
    /** True when the server refused for lack of permission. */
    val denied: Boolean,
    /** Text to show the user. */
    val message: String,
)

object AdminCommands {
    /** `return_code` prefix, so a reply can be matched to the request that caused it. */
    const val RETURN_CODE_PREFIX = "adm"

    private const val MAX_NAME = 40
    private const val MAX_TOPIC = 255
    private const val MAX_REASON = 80

    fun returnCode(requestId: Int) = "$RETURN_CODE_PREFIX$requestId"

    /** The request id inside a `return_code`, or null if it is not ours. */
    fun requestIdOf(returnCode: String?): Int? =
        returnCode?.takeIf { it.startsWith(RETURN_CODE_PREFIX) }
            ?.removePrefix(RETURN_CODE_PREFIX)?.toIntOrNull()

    /** TeamSpeak's own encoding of a channel password on the wire: base64(sha1(password)). */
    fun hashPassword(password: String): String =
        Base64.getEncoder().encodeToString(MessageDigest.getInstance("SHA-1").digest(password.toByteArray(Charsets.UTF_8)))

    /** Throws [AdminInvalid] for input the server would reject anyway. Passwords never go out in plain text. */
    fun build(op: AdminOp, requestId: Int): AdminCommand {
        val a = linkedMapOf<String, String>()
        val name: String
        when (op) {
            is AdminOp.MoveClient -> {
                name = "clientmove"
                a["clid"] = op.clientId.toString()
                a["cid"] = op.channelId.toString()
                op.password?.takeIf { it.isNotEmpty() }?.let { a["cpw"] = hashPassword(it) }
            }
            is AdminOp.Kick -> {
                limit("kick reason", op.reason, MAX_REASON)
                name = "clientkick"
                a["clid"] = op.clientId.toString()
                // 4 = kicked from channel, 5 = kicked from server.
                a["reasonid"] = if (op.fromServer) "5" else "4"
                if (op.reason.isNotEmpty()) a["reasonmsg"] = op.reason
            }
            is AdminOp.Ban -> {
                limit("ban reason", op.reason, MAX_REASON)
                if (op.seconds < 0) throw AdminInvalid("ban length cannot be negative")
                name = "banclient"
                a["clid"] = op.clientId.toString()
                if (op.seconds > 0) a["time"] = op.seconds.toString()
                if (op.reason.isNotEmpty()) a["banreason"] = op.reason
            }
            is AdminOp.CreateChannel -> {
                val n = channelName(op.name)
                name = "channelcreate"
                a["channel_name"] = n
                op.parentId?.takeIf { it != 0L }?.let { a["cpid"] = it.toString() }
                op.password?.takeIf { it.isNotEmpty() }?.let { a["channel_password"] = hashPassword(it) }
                op.topic?.takeIf { it.isNotEmpty() }?.let { limit("topic", it, MAX_TOPIC); a["channel_topic"] = it }
                op.maxClients?.let { maxClients(a, it) }
                a["channel_flag_permanent"] = if (op.permanent) "1" else "0"
            }
            is AdminOp.EditChannel -> {
                name = "channeledit"
                a["cid"] = op.channelId.toString()
                var changed = false
                op.name?.let { a["channel_name"] = channelName(it); changed = true }
                op.topic?.let { limit("topic", it, MAX_TOPIC); a["channel_topic"] = it; changed = true }
                op.password?.let {
                    if (it.isEmpty()) {
                        a["channel_flag_password"] = "0"
                    } else {
                        a["channel_password"] = hashPassword(it)
                        a["channel_flag_password"] = "1"
                    }
                    changed = true
                }
                op.maxClients?.let { maxClients(a, it); changed = true }
                if (!changed) throw AdminInvalid("nothing to change")
            }
            is AdminOp.MoveChannel -> {
                if (op.channelId == op.parentId) throw AdminInvalid("a channel cannot be its own parent")
                name = "channelmove"
                a["cid"] = op.channelId.toString()
                a["cpid"] = op.parentId.toString()
                a["order"] = op.orderAfterId.toString()
            }
            is AdminOp.DeleteChannel -> {
                name = "channeldelete"
                a["cid"] = op.channelId.toString()
                a["force"] = if (op.force) "1" else "0"
            }
        }
        val rc = returnCode(requestId)
        a["return_code"] = rc
        return AdminCommand(name, a, rc)
    }

    /**
     * Decode the `error` reply (the `args` of a [com.plnt.client.core.CoreEvent.RawCommand])
     * for [op]. A refusal names what was refused instead of passing the
     * server's bare "insufficient client permissions" through.
     */
    fun describe(op: AdminOp, args: Map<String, String>): AdminResult {
        val id = args["id"]?.toIntOrNull() ?: 0
        val what = op.label
        if (id == AdminError.OK) return AdminResult(true, id, false, "Done: $what.")
        val serverMsg = args["msg"].orEmpty()
        return when (id) {
            AdminError.INSUFFICIENT_PERMISSIONS -> {
                val perm = args["failed_permid"]?.let { " (permission $it)" }.orEmpty()
                AdminResult(false, id, true, "You don't have permission to $what$perm. Ask a server admin.")
            }
            AdminError.INVALID_CLIENT -> fail(id, "Can't $what: that user is no longer on the server.")
            AdminError.INVALID_CHANNEL -> fail(id, "Can't $what: that channel no longer exists.")
            AdminError.CHANNEL_NAME_IN_USE -> fail(id, "A channel with that name already exists there.")
            AdminError.CHANNEL_NOT_EMPTY ->
                fail(id, "That channel isn't empty. Move the people out, or delete it with force.")
            AdminError.CANT_DELETE_DEFAULT -> fail(id, "The default channel can't be deleted.")
            AdminError.INVALID_CHANNEL_PASSWORD -> fail(id, "Wrong channel password.")
            else -> fail(
                id,
                if (serverMsg.isEmpty()) "Can't $what: server error $id." else "Can't $what: $serverMsg (error $id).",
            )
        }
    }

    private fun fail(id: Int, message: String) = AdminResult(false, id, false, message)

    private fun channelName(raw: String): String {
        val n = raw.trim()
        if (n.isEmpty()) throw AdminInvalid("channel name is empty")
        limit("channel name", n, MAX_NAME)
        return n
    }

    private fun maxClients(a: MutableMap<String, String>, n: Int) {
        if (n < 1) throw AdminInvalid("max clients must be at least 1")
        a["channel_maxclients"] = n.toString()
        a["channel_flag_maxclients_unlimited"] = "0"
    }

    private fun limit(what: String, s: String, max: Int) {
        if (s.codePointCount(0, s.length) > max) throw AdminInvalid("$what is longer than $max characters")
    }
}
