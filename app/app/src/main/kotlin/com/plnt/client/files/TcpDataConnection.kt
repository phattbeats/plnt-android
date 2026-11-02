package com.plnt.client.files

import java.io.InputStream
import java.io.OutputStream
import java.net.InetSocketAddress
import java.net.Socket

/** Production [DataConnection]: a plain TCP socket to the server's file-transfer port. */
class TcpDataConnection private constructor(private val socket: Socket) : DataConnection {
    override val input: InputStream get() = socket.getInputStream()
    override val output: OutputStream get() = socket.getOutputStream()
    override fun close() = socket.close()

    companion object {
        /** Connects to [host]:[grant.port]; [readTimeoutMs] keeps a stalled transfer from hanging forever. */
        fun open(host: String, grant: TransferGrant, connectTimeoutMs: Int = 10_000, readTimeoutMs: Int = 30_000): TcpDataConnection {
            val s = Socket()
            try {
                s.soTimeout = readTimeoutMs
                s.connect(InetSocketAddress(host, grant.port), connectTimeoutMs)
            } catch (e: Exception) {
                runCatching { s.close() }; throw e
            }
            return TcpDataConnection(s)
        }
    }
}
