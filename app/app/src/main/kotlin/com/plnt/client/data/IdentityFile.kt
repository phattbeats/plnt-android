package com.plnt.client.data

import java.io.ByteArrayOutputStream
import java.io.InputStream

/** Exported identities are small; bound provider reads before decoding or parsing. */
internal const val MAX_IDENTITY_FILE_BYTES = 64 * 1024

internal fun readIdentityFile(input: InputStream): String {
    val output = ByteArrayOutputStream()
    val buffer = ByteArray(4096)
    while (true) {
        val count = input.read(buffer, 0, minOf(buffer.size, MAX_IDENTITY_FILE_BYTES + 1 - output.size()))
        if (count < 0) break
        if (count == 0) throw java.io.IOException("Identity provider returned no data")
        output.write(buffer, 0, count)
        require(output.size() <= MAX_IDENTITY_FILE_BYTES) { "Identity export exceeds 64 KiB" }
    }
    return output.toByteArray().decodeToString(throwOnInvalidSequence = true)
}
