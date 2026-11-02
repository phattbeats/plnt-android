package com.plnt.client.files

import org.junit.Assert.*
import org.junit.Test
import java.io.*

class FilePathsTest {
    @Test fun traversalNamesReduceToPlainName() {
        assertEquals("passwd", FilePaths.safeLocalName("../../etc/passwd"))
        assertEquals("x", FilePaths.safeLocalName("..\\..\\x"))
        assertEquals("file", FilePaths.safeLocalName(".."))
        assertEquals("file", FilePaths.safeLocalName("a/"))
        assertEquals("a_b", FilePaths.safeLocalName("a\u0000b"))
    }
    @Test fun longNamesKeepExtension() {
        val n = FilePaths.safeLocalName("a".repeat(300) + ".png")
        assertTrue(n.length <= 120); assertTrue(n.endsWith(".png"))
    }
    @Test fun uniqueNames() {
        assertEquals("a (1).txt", FilePaths.uniqueName("a.txt", setOf("a.txt")))
        assertEquals("a (2).txt", FilePaths.uniqueName("a.txt", setOf("a.txt", "a (1).txt")))
    }
    @Test fun remotePathRejectsTraversal() {
        assertNull(FilePaths.normalizeRemote("/a/../b"))
        assertNull(FilePaths.normalizeRemote("/a\\b"))
        assertNull(FilePaths.normalizeRemote("/a\u0000"))
        assertEquals("/a/b", FilePaths.normalizeRemote("a//./b/"))
        assertNull(FilePaths.child("/a", ".."))
        assertNull(FilePaths.child("/a", "x/y"))
        assertEquals("/a/x", FilePaths.child("/a", "x"))
        assertEquals("/x", FilePaths.child("/", "x"))
    }
}

class FileTransferTest {
    private class Fake(data: ByteArray, val failAfter: Int = -1) : DataConnection {
        val sent = ByteArrayOutputStream()
        override val input: InputStream = object : ByteArrayInputStream(data) {
            var served = 0
            override fun read(b: ByteArray, o: Int, l: Int): Int {
                if (failAfter in 0..served) throw IOException("reset")
                val n = super.read(b, o, minOf(l, 4)); if (n > 0) served += n; return n
            }
        }
        override val output: OutputStream = sent
        override fun close() {}
    }
    private val payload = ByteArray(100) { it.toByte() }
    private fun grant(seek: Long) = TransferGrant(1, "KEY", 30033, seek, 100)

    @Test fun grantParsing() {
        val g = TransferGrant.parse(mapOf("serverftfid" to "3", "ftkey" to "k", "port" to "30033", "seekpos" to "10"))!!
        assertEquals(10L, g.seekPos)
        assertNull(TransferGrant.parse(mapOf("serverftfid" to "3", "ftkey" to "", "port" to "30033")))
        assertNull(TransferGrant.parse(mapOf("serverftfid" to "3", "ftkey" to "k", "port" to "0")))
    }
    @Test fun downloadCompletesAndSendsKey() {
        val out = ByteArrayOutputStream(); val r = TransferRunner(); val c = Fake(payload)
        r.download(c, grant(0), 100, out)
        assertEquals(TransferState.DONE, r.state); assertArrayEquals(payload, out.toByteArray())
        assertEquals("KEY", c.sent.toString("US-ASCII"))
    }
    @Test fun cancelStopsAndReportsOffsetThenResumes() {
        val out = ByteArrayOutputStream(); val r = TransferRunner(chunk = 4)
        assertThrows(TransferCancelled::class.java) {
            r.download(Fake(payload), grant(0), 100, out) { done, _ -> if (done >= 20) r.cancel() }
        }
        assertEquals(TransferState.CANCELLED, r.state)
        val got = r.bytesDone; assertTrue(got in 20..24)
        // resume: server honours seekpos, sends the remainder
        val r2 = TransferRunner(chunk = 4)
        r2.download(Fake(payload.copyOfRange(got.toInt(), 100)), grant(got), 100, out)
        assertEquals(TransferState.DONE, r2.state); assertArrayEquals(payload, out.toByteArray())
    }
    @Test fun connectionDropFailsWithOffset() {
        val r = TransferRunner(chunk = 4)
        assertThrows(IOException::class.java) { r.download(Fake(payload, failAfter = 12), grant(0), 100, ByteArrayOutputStream()) }
        assertEquals(TransferState.FAILED, r.state); assertEquals(12L, r.bytesDone)
    }
    @Test fun earlyEofIsFailure() {
        val r = TransferRunner()
        assertThrows(EOFException::class.java) { r.download(Fake(ByteArray(10)), grant(0), 100, ByteArrayOutputStream()) }
    }
    @Test fun uploadResumesFromSeek() {
        val c = Fake(ByteArray(0)); val src = ByteArrayInputStream(payload); src.skip(40)
        TransferRunner().upload(c, grant(40), 100, src)
        assertArrayEquals("KEY".toByteArray() + payload.copyOfRange(40, 100), c.sent.toByteArray())
    }
    @Test fun permissionDeniedIsDistinguished() {
        assertTrue(FileResult.Failed(TsError.PERMISSION_DENIED, "no").permissionDenied)
        assertFalse(FileResult.Failed(TsError.FILE_NOT_FOUND, "no").permissionDenied)
    }
    @Test fun commandShapes() {
        val (n, a) = FileCommands.initUpload(1, 5, "", "/f.txt", 99, overwrite = false, resume = true)
        assertEquals("ftinitupload", n); assertEquals("1", a["resume"]); assertEquals("99", a["size"])
        assertEquals("a|b", FileCommands.delete(1, "", "a", "b").second["name"])
    }
}

class ChatHistoryTest {
    private class Mem : ChatHistory.Store {
        var data = listOf<StoredMessage>()
        override fun load() = data
        override fun save(all: List<StoredMessage>) { data = all.toList() }
    }
    private var now = 10 * 86_400_000L
    private val mem = Mem()
    private val h = ChatHistory(mem) { now }
    private fun m(c: String, t: Long, self: Boolean = false) = StoredMessage(c, t, "bob", self, "hi\nthere")

    @Test fun unreadCountsOnlyOthersAfterMarkRead() {
        h.append(m("channel:1", now - 10)); h.append(m("channel:1", now - 5, self = true))
        assertEquals(1, h.unread("channel:1")); assertEquals(1, h.totalUnread())
        h.markRead("channel:1"); assertEquals(0, h.unread("channel:1"))
    }
    @Test fun persistsAndReloads() {
        h.append(m("private:a", 1)); assertEquals(1, ChatHistory(mem).conversation("private:a").size)
    }
    @Test fun retentionDropsOld() {
        h.append(m("c", 1)); h.append(m("c", now))
        assertEquals(1, h.applyRetention(7)); assertEquals(1, h.conversation("c").size)
        assertEquals(0, h.applyRetention(0))
    }
    @Test fun deleteAndExport() {
        h.append(m("c", 0)); assertTrue(h.export("c").contains("bob: hi\n    there"))
        assertEquals(1, h.delete("c")); assertEquals("", h.export("c"))
    }
    @Test fun pokesAckClears() {
        h.poke("a", "wake"); assertEquals(1, h.pendingPokes().size); h.ackPokes(); assertTrue(h.pendingPokes().isEmpty())
    }
}

class WireUpTest {
    @Test fun correlatesListAndError() {
        val c = RawReplyCorrelator(); var got: RawReplyCorrelator.Reply? = null
        val rc = c.register { got = it }
        assertTrue(c.onRaw("notifyfilelist", mapOf("name" to "a.txt", "size" to "5", "type" to "1", "datetime" to "9")))
        assertTrue(c.onRaw("notifyfilelist", mapOf("name" to "d", "type" to "0")))
        assertNull(got)
        assertFalse(c.onRaw("error", mapOf("return_code" to "other", "id" to "0")))
        assertTrue(c.onRaw("error", mapOf("return_code" to rc, "id" to "0", "msg" to "ok")))
        val files = FileReplies.files(got!!)
        assertEquals(listOf("a.txt", "d"), files.map { it.name }); assertTrue(files[1].isDir)
    }
    @Test fun errorMapsToPermissionDenied() {
        val c = RawReplyCorrelator(); var res: FileResult<List<RemoteFile>>? = null
        val rc = c.register { res = FileReplies.toResult(it, FileReplies::files) }
        c.onRaw("error", mapOf("return_code" to rc, "id" to "2568", "msg" to "insufficient"))
        assertTrue((res as FileResult.Failed).permissionDenied)
    }
    @Test fun grantFromReply() {
        val c = RawReplyCorrelator(); var g: TransferGrant? = null
        val rc = c.register { g = FileReplies.grant(it) }
        c.onRaw("notifystartdownload", mapOf("serverftfid" to "2", "ftkey" to "k", "port" to "30033", "size" to "7"))
        c.onRaw("error", mapOf("return_code" to rc, "id" to "0"))
        assertEquals(30033, g!!.port)
    }
    @Test fun historyStoreRoundTripsAwkwardText() {
        val f = File.createTempFile("hist", ".tsv"); f.delete()
        val s = FileChatHistoryStore(f)
        val m = StoredMessage("channel:1", 5L, "Bob\tX", false, "line1\nline2\\n \t end")
        s.save(listOf(m, m.copy(atMillis = 6, isSelf = true)))
        assertEquals(listOf(m, m.copy(atMillis = 6, isSelf = true)), s.load())
        assertTrue(FileChatHistoryStore(File("/nonexistent/x")).load().isEmpty())
        f.delete()
    }
}
