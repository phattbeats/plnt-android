package com.plnt.client.files

import org.junit.Assert.*
import org.junit.Test
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream

class FileBrowserTest {
    private val sent = mutableListOf<Pair<String, Map<String, String>>>()
    private val corr = RawReplyCorrelator()
    private fun browser(open: (TransferGrant) -> DataConnection = { error("no data conn") }) =
        FileBrowser(corr, { n, a -> sent += n to a }, open)
    private fun answer(vararg data: Pair<String, Map<String, String>>, id: String = "0") {
        val rc = sent.last().second.getValue("return_code")
        data.forEach { corr.onRaw(it.first, it.second) }
        corr.onRaw("error", mapOf("id" to id, "msg" to "ok", "return_code" to rc))
    }

    @Test fun listParsesRepliesAndSendsReturnCode() {
        var out: FileResult<List<RemoteFile>>? = null
        browser().list(5, "", "/", { out = it })
        assertEquals("ftgetfilelist", sent.last().first)
        answer("notifyfilelist" to mapOf("name" to "a.txt", "size" to "3", "type" to "1", "datetime" to "9"),
            "notifyfilelist" to mapOf("name" to "d", "size" to "0", "type" to "0", "datetime" to "9"))
        val files = (out as FileResult.Ok).value
        assertEquals(listOf("a.txt", "d"), files.map { it.name }); assertTrue(files[1].isDir)
    }

    @Test fun emptyDirectoryIsNotAnError() {
        var out: FileResult<List<RemoteFile>>? = null
        browser().list(5, "", "/", { out = it }); answer(id = "1281")
        assertEquals(emptyList<RemoteFile>(), (out as FileResult.Ok).value)
    }

    @Test fun permissionDeniedSurfaces() {
        var out: FileResult<Unit>? = null
        browser().mkdir(5, "", "/", "x") { out = it }; answer(id = "2568")
        assertTrue((out as FileResult.Failed).permissionDenied)
    }

    @Test fun traversalNeverReachesTheWire() {
        var out: FileResult<Unit>? = null
        browser().delete(5, "", "/", "../etc") { out = it }
        assertTrue(out is FileResult.Failed); assertTrue(sent.isEmpty())
    }

    @Test fun downloadFollowsGrant() {
        val payload = ByteArray(10) { it.toByte() }
        val dst = ByteArrayOutputStream()
        var out: FileResult<Unit>? = null
        val conn = object : DataConnection {
            val o = ByteArrayOutputStream()
            override val input = ByteArrayInputStream(payload); override val output = o
            override fun close() {}
        }
        val b = browser { conn }
        b.download(5, "", "/", RemoteFile("f", 10, false, 0), { dst }, TransferRunner(), null) { out = it }
        answer("notifystartdownload" to mapOf("serverftfid" to "1", "ftkey" to "K", "port" to "30033", "size" to "10"))
        assertTrue(out is FileResult.Ok); assertArrayEquals(payload, dst.toByteArray())
    }
}
