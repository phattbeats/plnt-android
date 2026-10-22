package com.plnt.client.account

import org.junit.Assert.*
import org.junit.Test

class AccountClientTest {
    private fun fetch(sync: String = AccountFixtures.syncReply, validIdentity: Boolean = true): AccountPreview {
        val methods = mutableListOf<ReadRpc>()
        val client = AccountClient(AccountTransport { rpc, request ->
            methods += rpc
            val message = Proto(request)
            when (rpc) {
                ReadRpc.LOGIN -> {
                    assertEquals(AccountFixtures.loginPassword, String(message.bytes(2)))
                    assertEquals("own-device", message.text(7))
                    assertEquals(0L, message.number(3))
                    assertFalse(message.has(8))
                    AccountFixtures.loginReply.hex()
                }
                ReadRpc.ITEMS -> {
                    assertEquals("synthetic-session", message.text(1))
                    assertEquals(listOf(0L, 1L), message.messages(2).map { it.number(1) })
                    assertTrue(message.messages(2).all { !it.has(3) && !it.has(4) })
                    sync.hex()
                }
            }
        }, { validIdentity })
        val result = client.fetch("Person@Example.invalid", "SyntheticPassword!", "", "own-device")
        assertEquals(listOf(ReadRpc.LOGIN, ReadRpc.ITEMS), methods)
        return result
    }
    @Test fun syntheticLoginToVerifiedPreviewOnlyCallsReadRpc() {
        val entry = fetch().entries.single()
        assertEquals("voice.example.invalid", entry.bookmark.address)
        assertEquals("synthetic-private-key", entry.identity)
        assertEquals("synthetic-server-password", entry.password)
        assertNull(entry.bookmark.serverPassword) // Password only goes to encrypted storage.
        assertFalse(entry.toString().contains("synthetic-private-key"))
        assertEquals(entry.bookmark.id, fetch().entries.single().bookmark.id)
    }
    @Test fun missingOrInvalidIdentityNeverFallsBack() {
        assertThrows(AccountFailure::class.java) { fetch(AccountFixtures.missingIdentityReply) }
        assertThrows(AccountFailure::class.java) { fetch(validIdentity = false) }
    }
    @Test fun otpAndLoginErrorsDoNotRequestSync() {
        for (status in listOf(0L, 202L, 208L, 209L, 211L, 212L, 213L)) {
            var calls = 0
            val client = AccountClient(AccountTransport { rpc, request ->
                calls++
                assertEquals(ReadRpc.LOGIN, rpc)
                assertEquals("123456", Proto(request).text(6))
                Proto.Writer().number(5, status).build()
            })
            val error = assertThrows(AccountFailure::class.java) { client.fetch("a@b.invalid", "pass", "123456", "device") }
            assertEquals(status in listOf(208L, 209L), error.needsOtp)
            assertEquals(1, calls)
        }
    }
    @Test fun secretBearingTransportExceptionIsRedacted() {
        val client = AccountClient(AccountTransport { _, _ -> throw java.io.IOException("secret-password") })
        val error = assertThrows(AccountFailure::class.java) { client.fetch("a@b.invalid", "pass", "", "device") }
        assertFalse(error.message!!.contains("secret-password"))
        assertNull(error.cause)
    }
}
