package com.plnt.client.files

import java.io.InputStream
import java.io.OutputStream

/**
 * Drives the channel file browser over raw commands (PHA-4864). Every request
 * gets a `return_code` from [RawReplyCorrelator]; [send] is the core's
 * `sendRawCommand`, [open] makes the TCP data connection for a granted transfer.
 * Callbacks may fire on the core's event thread, so callers marshal as needed.
 */
class FileBrowser(
    private val correlator: RawReplyCorrelator,
    private val send: (String, Map<String, String>) -> Unit,
    private val open: (TransferGrant) -> DataConnection,
) {
    private var ftId = 1

    private fun request(cmd: Pair<String, Map<String, String>>, done: (RawReplyCorrelator.Reply) -> Unit) {
        val rc = correlator.register(done)
        try { send(cmd.first, cmd.second + ("return_code" to rc)) }
        catch (e: Exception) { correlator.forget(rc); done(RawReplyCorrelator.Reply(-1, e.message ?: "send failed", emptyList())) }
    }

    fun list(cid: Long, cpw: String, dir: String, cb: (FileResult<List<RemoteFile>>) -> Unit) {
        val path = FilePaths.normalizeRemote(dir) ?: return cb(FileResult.Failed(-1, "Invalid path"))
        request(FileCommands.list(cid, cpw, path)) { r ->
            // An empty directory is reported as error 1281 (database empty result), not a failure.
            cb(if (r.code == 1281) FileResult.Ok(emptyList()) else FileReplies.toResult(r, FileReplies::files))
        }
    }

    fun mkdir(cid: Long, cpw: String, dir: String, name: String, cb: (FileResult<Unit>) -> Unit) {
        val path = FilePaths.child(dir, name) ?: return cb(FileResult.Failed(-1, "Invalid name"))
        request(FileCommands.mkdir(cid, cpw, path)) { cb(FileReplies.toResult(it) {}) }
    }

    fun delete(cid: Long, cpw: String, dir: String, name: String, cb: (FileResult<Unit>) -> Unit) {
        val path = FilePaths.child(dir, name) ?: return cb(FileResult.Failed(-1, "Invalid name"))
        request(FileCommands.delete(cid, cpw, path)) { cb(FileReplies.toResult(it) {}) }
    }

    fun rename(cid: Long, cpw: String, dir: String, old: String, new: String, cb: (FileResult<Unit>) -> Unit) {
        val from = FilePaths.child(dir, old)
        val to = FilePaths.child(dir, new)
        if (from == null || to == null) return cb(FileResult.Failed(-1, "Invalid name"))
        request(FileCommands.rename(cid, cpw, from, to)) { cb(FileReplies.toResult(it) {}) }
    }

    /** Asks for a download grant, then runs the transfer on the calling thread (call from IO). */
    fun download(
        cid: Long, cpw: String, dir: String, file: RemoteFile, dst: (seek: Long) -> OutputStream,
        runner: TransferRunner, progress: Progress?, cb: (FileResult<Unit>) -> Unit,
    ) {
        val path = FilePaths.child(dir, file.name) ?: return cb(FileResult.Failed(-1, "Invalid name"))
        grant(FileCommands.initDownload(ftId++, cid, cpw, path, 0)) { res ->
            when (res) {
                is FileResult.Failed -> cb(res)
                is FileResult.Ok -> cb(transfer { runner.download(open(res.value), res.value, file.size, dst(res.value.seekPos), progress) })
            }
        }
    }

    fun upload(
        cid: Long, cpw: String, dir: String, name: String, size: Long, src: InputStream,
        runner: TransferRunner, progress: Progress?, cb: (FileResult<Unit>) -> Unit,
    ) {
        val path = FilePaths.child(dir, name) ?: return cb(FileResult.Failed(-1, "Invalid name"))
        grant(FileCommands.initUpload(ftId++, cid, cpw, path, size, overwrite = false, resume = false)) { res ->
            when (res) {
                is FileResult.Failed -> cb(res)
                is FileResult.Ok -> cb(transfer { runner.upload(open(res.value), res.value, size, src, progress) })
            }
        }
    }

    private fun grant(cmd: Pair<String, Map<String, String>>, cb: (FileResult<TransferGrant>) -> Unit) =
        request(cmd) { r ->
            if (r.code != TsError.OK) cb(FileResult.Failed(r.code, r.message))
            else cb(FileReplies.grant(r)?.let { FileResult.Ok(it) } ?: FileResult.Failed(-1, "Server sent no transfer grant"))
        }

    private inline fun transfer(block: () -> Unit): FileResult<Unit> = try { block(); FileResult.Ok(Unit) }
    catch (e: TransferCancelled) { FileResult.Failed(-2, "Cancelled") }
    catch (e: Exception) { FileResult.Failed(-3, e.message ?: "Transfer failed") }
}
