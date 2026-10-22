package com.plnt.client.account

import org.junit.Assert.*
import org.junit.Test

internal fun String.hex(): ByteArray = chunked(2).map { it.toInt(16).toByte() }.toByteArray()
class AccountCryptoTest {
    @Test fun asciiLoginMatchesIndependentPythonKdf() {
        AccountCrypto.passwords("Person@Example.invalid", "SyntheticPassword!").use {
            assertEquals(AccountFixtures.loginPassword, String(it.login))
            assertArrayEquals(AccountFixtures.encryptionPassword.hex(), it.encryption)
        }
    }
    @Test fun nativeCtrCarryVectors() {
        val vectors = listOf(
            "000102030405060708090a0b0c0d0e0f" to "5a6f06540cfe7791f8275f360ecea89d1adeb9e64463f941ed0883488cb8ec0fae1b129e3c286df2d3f8a49bf7732273bdec3961afb0a540820fff3cd5c1277c30",
            "ff000000000000000000000000000000" to "d9a27132cdc7dc939e1e3dfcfb3f25d915c84b3ffbd2955cee8e6c7a5475bf4e5a96fd1ccbbeb6e0781bb8cb0c6a6f88e284302cab39f6af84bee3bd5eab4228ba",
            "ffffffffffffffffffffffffffffffff" to "e998e61e48a276dd5b8e1b70518259e1843ce0ec51b9c8450bfc3551d60a863021a22bd203cc003320617e883db9b37b996b188c96765a8b906535bbf4d4e0b263",
        )
        for ((iv, ciphertext) in vectors) {
            assertArrayEquals(ciphertext.hex(), AccountCrypto.ctr(ByteArray(32) { it.toByte() }, iv.hex(), ByteArray(65) { it.toByte() }))
        }
    }
    @Test fun bothNativeKeyWrapVersionsAndBadTag() {
        val keys = AccountCrypto.PasswordKeys("Synthetic-login-hash".toByteArray(), ByteArray(32) { (it + 32).toByte() })
        val vectors = listOf(
            "024b896d9d4358a8737731d10b60954cdb000102030405060708090a0b5c535fa44e30edebb4f0c3cd2a7e50330361fb777e04b0063c14c9a1a79cd587",
            "014bb017de708b43776390f13a1b6e0d7d000102030405060708090a0bc8f28f86827bd84a77720985937f8451a0d9e0177f8278426c6b9bff9cb458c0",
        )
        for (hex in vectors) {
            val blob = hex.hex()
            assertArrayEquals(ByteArray(32) { it.toByte() }, AccountCrypto.unwrap(blob, "synthetic-account-uuid", keys))
            blob[1] = (blob[1].toInt() xor 1).toByte()
            assertThrows(javax.crypto.AEADBadTagException::class.java) { AccountCrypto.unwrap(blob, "synthetic-account-uuid", keys) }
        }
        assertThrows(AccountFailure::class.java) { AccountCrypto.unwrap(ByteArray(61) { 3 }, "uuid", keys) }
    }
    @Test fun itemDigestFailureReturnsNoPlaintext() {
        val plain = "synthetic plaintext".toByteArray()
        val master = ByteArray(32)
        val iv = ByteArray(16)
        val blob = iv + AccountCrypto.ctr(master, iv, plain) + AccountCrypto.sha512(plain)
        assertArrayEquals(plain, AccountCrypto.decryptItem(master, blob))
        blob[blob.lastIndex] = (blob.last().toInt() xor 1).toByte()
        assertThrows(IllegalArgumentException::class.java) { AccountCrypto.decryptItem(master, blob) }
    }
    @Test fun malformedProtobufRejectedAndUnknownFieldsIgnored() {
        for (hex in listOf("00", "0aff", "08ffffffffffffffffff02", "0b", "0a0378", "08010802")) {
            assertThrows(IllegalArgumentException::class.java) { Proto(hex.hex()).number(1) }
        }
        val p = Proto(Proto.Writer().number(20, 7).text(2, "ok").build())
        assertEquals("ok", p.text(2))
        assertEquals(0L, p.number(1))
    }
}
