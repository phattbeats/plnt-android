package com.plnt.client.files

import java.io.InputStream
import java.io.OutputStream

/** TeamSpeak error ids relevant to files. */
object TsError {
    const val OK = 0
    const val FILE_NOT_FOUND = 2051
    const val FILE_ALREADY_EXISTS = 2050
    const val PERMISSION_DENIED = 2568
    const val FILE_TRANSFER_LIMIT = 2563
}

sealed class FileResult<out T> {
    data class Ok<T>(val value: T) : FileResult<T>()
    /** Server said no. [permissionDenied] drives a "you don't have access" UI instead of a generic failure. */
    data class Failed(val code: Int, val message: String) : FileResult<Nothing>() {
        val permissionDenied get() = code == TsError.PERMISSION_DENIED
    }
}

data class RemoteFile(val name: String, val size: Long, val isDir: Boolean, val modified: Long)

/** Builds the raw commands for core `sendRawCommand` (values are escaped by the core). */
object FileCommands {
    fun list(cid: Long, cpw: String, path: String) = cmd("ftgetfilelist", "cid" to cid, "cpw" to cpw, "path" to path)
    fun mkdir(cid: Long, cpw: String, dir: String) = cmd("ftcreatedir", "cid" to cid, "cpw" to cpw, "dirname" to dir)
    fun delete(cid: Long, cpw: String, vararg names: String) =
        cmd("ftdeletefile", "cid" to cid, "cpw" to cpw, "name" to names.joinToString("|"))
    fun rename(cid: Long, cpw: String, old: String, new: String) =
        cmd("ftrenamefile", "cid" to cid, "cpw" to cpw, "oldname" to old, "newname" to new)
    fun initUpload(ftId: Int, cid: Long, cpw: String, path: String, size: Long, overwrite: Boolean, resume: Boolean) =
        cmd("ftinitupload", "clientftfid" to ftId, "cid" to cid, "cpw" to cpw, "name" to path,
            "size" to size, "overwrite" to if (overwrite) 1 else 0, "resume" to if (resume) 1 else 0)
    fun initDownload(ftId: Int, cid: Long, cpw: String, path: String, seekPos: Long) =
        cmd("ftinitdownload", "clientftfid" to ftId, "cid" to cid, "cpw" to cpw, "name" to path, "seekpos" to seekPos)
    fun stop(serverFtId: Int, delete: Boolean) = cmd("ftstop", "serverftfid" to serverFtId, "delete" to if (delete) 1 else 0)

    private fun cmd(name: String, vararg a: Pair<String, Any>) = name to a.associate { it.first to it.second.toString() }
}

/** Reply to ftinit*: where to connect and what to resume from. */
data class TransferGrant(val serverFtId: Int, val key: String, val port: Int, val seekPos: Long, val size: Long) {
    companion object {
        /** Parses `notifystartupload` / `notifystartdownload` args. Null if malformed. */
        fun parse(args: Map<String, String>): TransferGrant? = runCatching {
            TransferGrant(
                args.getValue("serverftfid").toInt(), args.getValue("ftkey"),
                args.getValue("port").toInt(), args["seekpos"]?.toLong() ?: 0L, args["size"]?.toLong() ?: 0L,
            ).takeIf { it.key.isNotEmpty() && it.port in 1..65535 && it.seekPos >= 0 && it.size >= 0 }
        }.getOrNull()
    }
}

/** Where the data connection goes; production is a TCP socket, tests use a fake. */
interface DataConnection : AutoCloseable {
    val input: InputStream
    val output: OutputStream
}

enum class TransferState { PENDING, RUNNING, DONE, CANCELLED, FAILED }

/** Progress callback: bytes done (absolute, including resumed prefix) and total. */
fun interface Progress { fun onBytes(done: Long, total: Long) }

class TransferCancelled : Exception("transfer cancelled")

/**
 * Runs one data-channel transfer. Cancel is cooperative and checked between
 * chunks. A cancelled/failed transfer reports how many bytes landed so the
 * caller can re-issue ftinit* with that offset to resume.
 */
class TransferRunner(private val chunk: Int = 16 * 1024) {
    @Volatile private var cancelled = false
    @Volatile var state = TransferState.PENDING; private set
    @Volatile var bytesDone = 0L; private set

    fun cancel() { cancelled = true }

    /** Sends [src] (already positioned at grant.seekPos) to the server. */
    fun upload(conn: DataConnection, grant: TransferGrant, total: Long, src: InputStream, p: Progress? = null) =
        run(conn, grant, total, p) { buf ->
            val n = src.read(buf)
            if (n > 0) conn.output.write(buf, 0, n)
            n
        }

    /** Receives into [dst]; [total] is the full file size, grant.seekPos is already on disk. */
    fun download(conn: DataConnection, grant: TransferGrant, total: Long, dst: OutputStream, p: Progress? = null) =
        run(conn, grant, total, p) { buf ->
            val n = conn.input.read(buf)
            if (n > 0) dst.write(buf, 0, n)
            n
        }

    private inline fun run(conn: DataConnection, grant: TransferGrant, total: Long, p: Progress?, step: (ByteArray) -> Int) {
        state = TransferState.RUNNING
        bytesDone = grant.seekPos
        try {
            conn.output.write(grant.key.toByteArray(Charsets.US_ASCII)); conn.output.flush()
            val buf = ByteArray(chunk)
            while (bytesDone < total) {
                if (cancelled) throw TransferCancelled()
                val n = step(buf)
                if (n < 0) throw java.io.EOFException("connection closed at $bytesDone/$total")
                bytesDone += n
                p?.onBytes(bytesDone, total)
            }
            conn.output.flush()
            state = TransferState.DONE
        } catch (e: TransferCancelled) {
            state = TransferState.CANCELLED; throw e
        } catch (e: Exception) {
            state = TransferState.FAILED; throw e
        } finally {
            runCatching { conn.close() }
        }
    }
}
