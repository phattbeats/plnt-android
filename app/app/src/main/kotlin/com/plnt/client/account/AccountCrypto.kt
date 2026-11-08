package com.plnt.client.account

import java.security.MessageDigest
import java.util.Base64
import javax.crypto.Cipher
import javax.crypto.Mac
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.SecretKeySpec

/** Byte-oriented KDF: never let a provider reinterpret the password as UTF-16. */
internal object AccountCrypto {
    fun sha512(bytes: ByteArray): ByteArray = MessageDigest.getInstance("SHA-512").digest(bytes)

    fun pbkdf2(password: ByteArray, salt: ByteArray, length: Int): ByteArray {
        val mac = Mac.getInstance("HmacSHA512")
        mac.init(SecretKeySpec(if (password.isEmpty()) byteArrayOf(0) else password, "HmacSHA512"))
        val out = ByteArray(length)
        var offset = 0
        var block = 1
        while (offset < length) {
            var u = mac.doFinal(salt + byteArrayOf(0, 0, 0, block.toByte()))
            val t = u.copyOf()
            repeat(9999) {
                val next = mac.doFinal(u)
                u.fill(0)
                u = next
                for (i in t.indices) t[i] = (t[i].toInt() xor u[i].toInt()).toByte()
            }
            val count = minOf(t.size, length - offset)
            t.copyInto(out, offset, 0, count)
            t.fill(0); u.fill(0)
            offset += count
            block++
        }
        return out
    }

    class PasswordKeys(val login: ByteArray, val encryption: ByteArray) : AutoCloseable {
        override fun close() { login.fill(0); encryption.fill(0) }
    }

    fun passwords(email: String, password: String): PasswordKeys {
        // Non-ASCII email case-folding has not been established by native research.
        require(email.all { it.code in 0x21..0x7e }) { "Use an ASCII email address for this test build." }
        val p = password.toByteArray(Charsets.UTF_8)
        val m = email.map { if (it in 'A'..'Z') it + 32 else it }.joinToString("").toByteArray()
        val ls = m + "ts3Login".toByteArray() + p
        val es = m + "ts3Encryption".toByteArray() + p
        val raw = pbkdf2(p, ls, 48)
        return try {
            PasswordKeys(Base64.getEncoder().encode(raw), pbkdf2(p, es, 32))
        } finally { p.fill(0); raw.fill(0); ls.fill(0); es.fill(0) }
    }

    fun unwrap(blob: ByteArray, uuid: String, keys: PasswordKeys): ByteArray {
        if (blob.size != 61) throw AccountFailure("Unsupported account key format. Sync recovery may be required in the official client.")
        val key = when (blob[0].toInt()) {
            1 -> {
                val salt = sha512(uuid.toByteArray() + "ts3PassPhrase".toByteArray() + keys.login)
                try { pbkdf2(keys.login, salt, 32) } finally { salt.fill(0) }
            }
            2 -> keys.encryption.copyOf()
            else -> throw AccountFailure("Unsupported account key version.")
        }
        return try {
            Cipher.getInstance("AES/GCM/NoPadding").run {
                init(Cipher.DECRYPT_MODE, SecretKeySpec(key, "AES"), GCMParameterSpec(128, blob.copyOfRange(17, 29)))
                doFinal(blob.copyOfRange(29, blob.size) + blob.copyOfRange(1, 17))
            }
        } finally { key.fill(0) }
    }

    /** Native CTR has a combined BE+1 / LE+1 / BE-1 counter update per block. */
    fun ctr(key: ByteArray, iv: ByteArray, input: ByteArray): ByteArray {
        require(key.size == 32 && iv.size == 16)
        val aes = Cipher.getInstance("AES/ECB/NoPadding")
        aes.init(Cipher.ENCRYPT_MODE, SecretKeySpec(key, "AES"))
        val counter = iv.copyOf()
        val out = ByteArray(input.size)
        for (offset in input.indices step 16) {
            val block = aes.doFinal(counter)
            for (i in 0 until minOf(16, input.size - offset)) out[offset + i] = (input[offset + i].toInt() xor block[i].toInt()).toByte()
            block.fill(0)
            adjust(counter, false, 1); adjust(counter, true, 1); adjust(counter, false, -1)
        }
        counter.fill(0)
        return out
    }

    private fun adjust(counter: ByteArray, little: Boolean, delta: Int) {
        for (i in if (little) 0..15 else 15 downTo 0) {
            val next = (counter[i].toInt() and 255) + delta
            counter[i] = next.toByte()
            if (next in 0..255) break
        }
    }

    fun decryptItem(master: ByteArray, blob: ByteArray): ByteArray {
        require(blob.size in 80..(1024 * 1024)) { "Invalid encrypted item size." }
        val plain = ctr(master, blob.copyOfRange(0, 16), blob.copyOfRange(16, blob.size - 64))
        if (!MessageDigest.isEqual(sha512(plain), blob.copyOfRange(blob.size - 64, blob.size))) {
            plain.fill(0)
            throw IllegalArgumentException("A synced item failed its integrity check. Nothing was imported.")
        }
        return plain
    }
}
