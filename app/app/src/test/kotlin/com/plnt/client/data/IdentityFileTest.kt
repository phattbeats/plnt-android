package com.plnt.client.data

import java.io.ByteArrayInputStream
import java.io.IOException
import java.io.InputStream
import java.nio.charset.CharacterCodingException
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test

class IdentityFileTest {
    @Test fun acceptsExactLimit() {
        val bytes = ByteArray(MAX_IDENTITY_FILE_BYTES) { 'x'.code.toByte() }
        assertEquals(MAX_IDENTITY_FILE_BYTES, readIdentityFile(ByteArrayInputStream(bytes)).length)
    }

    @Test fun stopsAtOneBytePastLimitWithoutReadingRemainder() {
        var consumed = 0
        val endless = object : InputStream() {
            override fun read(): Int { consumed++; return 'x'.code }
        }
        assertThrows(IllegalArgumentException::class.java) { readIdentityFile(endless) }
        assertEquals(MAX_IDENTITY_FILE_BYTES + 1, consumed)
    }

    @Test fun rejectsInvalidUtf8InsteadOfReplacingBytes() {
        assertThrows(CharacterCodingException::class.java) {
            readIdentityFile(ByteArrayInputStream(byteArrayOf(0xc3.toByte(), 0x28)))
        }
    }

    @Test fun surfacesProviderFailure() {
        val broken = object : InputStream() { override fun read(): Int = throw IOException("offline") }
        assertThrows(IOException::class.java) { readIdentityFile(broken) }
    }
}
