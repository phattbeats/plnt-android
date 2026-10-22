package com.plnt.client.account

import java.io.ByteArrayOutputStream
import java.nio.ByteBuffer
import java.nio.charset.CodingErrorAction

/** Small bounded proto3 wire codec for the recovered read-only schema subset. */
internal class Proto(bytes: ByteArray) {
    private class Field(val number: Int, val wire: Int, val value: Long, val bytes: ByteArray)
    private val fields = mutableListOf<Field>()
    init {
        require(bytes.size <= 8 * 1024 * 1024) { "Account response is too large." }
        var p = 0
        fun varint(): Long {
            var result = 0L
            for (shift in 0..63 step 7) {
                require(p < bytes.size) { "Truncated account response." }
                val b = bytes[p++].toInt() and 255
                require(shift != 63 || b <= 1) { "Invalid account response integer." }
                result = result or ((b and 127).toLong() shl shift)
                if (b < 128) return result
            }
            error("Invalid account response integer.")
        }
        while (p < bytes.size) {
            require(fields.size < 20000) { "Too many account response fields." }
            val tag = varint()
            require(tag ushr 3 in 1..536870911) { "Invalid account field." }
            val wire = (tag and 7).toInt()
            var v = 0L
            val n = when (wire) {
                0 -> { v = varint(); 0 }
                1 -> 8
                2 -> { val length = varint(); require(length in 0..bytes.size.toLong()); length.toInt() }
                5 -> 4
                else -> error("Unsupported account wire type.")
            }
            require(n <= bytes.size - p) { "Truncated account response field." }
            fields += Field((tag ushr 3).toInt(), wire, v, bytes.copyOfRange(p, p + n))
            p += n
        }
    }
    private fun one(n: Int, wire: Int): Field? {
        val matches = fields.filter { it.number == n }
        require(matches.size <= 1 && matches.all { it.wire == wire }) { "Invalid account field type or duplicate." }
        return matches.singleOrNull()
    }
    fun number(n: Int): Long = one(n, 0)?.value ?: 0
    fun bytes(n: Int): ByteArray = one(n, 2)?.bytes ?: byteArrayOf()
    fun text(n: Int): String = Charsets.UTF_8.newDecoder().onMalformedInput(CodingErrorAction.REPORT)
        .decode(ByteBuffer.wrap(bytes(n))).toString()
    fun messages(n: Int): List<Proto> = fields.filter { it.number == n }.map {
        require(it.wire == 2); Proto(it.bytes)
    }
    fun has(n: Int) = fields.any { it.number == n }
    fun clear() { fields.forEach { it.bytes.fill(0) }; fields.clear() }

    class Writer {
        private val out = ByteArrayOutputStream()
        private fun varint(value: Long) {
            var v = value
            while (v and -128L != 0L) { out.write((v.toInt() and 127) or 128); v = v ushr 7 }
            out.write(v.toInt())
        }
        fun number(n: Int, v: Long): Writer = apply { varint((n * 8).toLong()); varint(v) }
        fun bytes(n: Int, b: ByteArray): Writer = apply { varint((n * 8 + 2).toLong()); varint(b.size.toLong()); out.write(b) }
        fun text(n: Int, s: String) = bytes(n, s.toByteArray(Charsets.UTF_8))
        fun build(): ByteArray = out.toByteArray()
    }
}
