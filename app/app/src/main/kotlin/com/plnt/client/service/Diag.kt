package com.plnt.client.service

import android.os.Build
import android.util.Base64
import android.util.Log
import com.plnt.client.BuildConfig
import java.io.File
import java.io.RandomAccessFile
import java.net.HttpURLConnection
import java.net.URL

/**
 * PHA-4176 diagnostic trace. Keeps a bounded log file of connection events and
 * the core's `diag ...` lines so a ping-timeout drop can be read after the
 * fact without anyone attaching logcat. The tail of that file is uploaded to a
 * Nextcloud upload-only drop on its own (start-up, right after a drop or
 * stall, every 15 s for the first 3 minutes after a connect/drop, then at most
 * every 5 minutes and only while more than heartbeats is being added), so
 * nobody has to fetch it from the phone. A watchdog ships a thread dump the
 * moment the core's heartbeat stops.
 *
 * Nothing here is ever shown to the user or posted to a channel.
 */
object Diag {
    /**
     * The one switch for this whole machinery: uploads, the stall watchdog and
     * the diagnostic auto-join on open. Debug builds (what the tester runs)
     * have it on; a release build has all of it off.
     */
    @JvmField val ENABLED: Boolean = BuildConfig.DEBUG

    private const val TAG = "plnt.diag"
    private var file: File? = null
    private val start = System.currentTimeMillis()

    // Upload-only public share: anyone with the token can drop files, nobody
    // can list or read them back.
    private const val DROP_URL = "https://nextcloud.phatt.vip/public.php/webdav/"
    private const val DROP_TOKEN = "nLw2cdSfQNTie3x"
    // On-disk cap: past MAX_FILE_BYTES the file is cut back to its last
    // KEEP_FILE_BYTES. Only the last MAX_UPLOAD_BYTES ever leave the phone.
    private const val MAX_FILE_BYTES = 256L * 1024
    private const val KEEP_FILE_BYTES = 128 * 1024
    private const val MAX_UPLOAD_BYTES = 64 * 1024
    // Tick cadence: fast right after a connect/drop/stall, slow afterwards.
    private const val BURST_WINDOW_MS = 3 * 60_000L
    private const val BURST_TICK_MS = 15_000L
    private const val SLOW_TICK_MS = 5 * 60_000L
    private val session = "${Build.MODEL.replace(Regex("[^A-Za-z0-9]"), "")}-$start"

    /** Anything was added since the last upload, heartbeats included. */
    private var dirty = false
    /** Something other than a `diag t=` heartbeat was added since the last upload. */
    private var notable = false
    private var burstUntil = 0L
    private var lastUpload = 0L

    fun init(dir: File) {
        if (!ENABLED || file != null) return
        file = File(dir, "plnt-diag.log")
        // The previous run's trace (if the process died) goes up first, then
        // a background loop ships the file on the cadence described above.
        uploadAsync("start")
        Thread({
            while (true) {
                Thread.sleep(5_000)
                if (watchdogDue()) {
                    add("WATCHDOG no core heartbeat for ${(System.currentTimeMillis() - lastBeat) / 1000}s\n" + threadDump())
                    openBurst()
                    upload("stall")
                } else if (tickDue()) {
                    upload("tick")
                }
            }
        }, "plnt-diag-upload").apply { isDaemon = true }.start()
    }

    @Synchronized
    private fun tickDue(): Boolean {
        val now = System.currentTimeMillis()
        return if (now < burstUntil) {
            dirty && now - lastUpload >= BURST_TICK_MS
        } else {
            notable && now - lastUpload >= SLOW_TICK_MS
        }
    }

    @Synchronized
    private fun openBurst() {
        burstUntil = System.currentTimeMillis() + BURST_WINDOW_MS
    }

    fun uploadAsync(why: String) {
        if (!ENABLED) return
        Thread({ upload(why) }, "plnt-diag-upload-now").apply { isDaemon = true }.start()
    }

    /** The last [MAX_UPLOAD_BYTES] of the log, read under the lock [add] trims under. */
    @Synchronized
    private fun tail(f: File): ByteArray? {
        if (!f.exists() || f.length() == 0L) return null
        val buf = RandomAccessFile(f, "r").use { raf ->
            val len = raf.length()
            val n = minOf(len, MAX_UPLOAD_BYTES.toLong()).toInt()
            ByteArray(n).also {
                raf.seek(len - n)
                raf.readFully(it)
            }
        }
        dirty = false
        notable = false
        lastUpload = System.currentTimeMillis()
        return buf
    }

    private fun upload(why: String) {
        if (!ENABLED) return
        val f = file ?: return
        runCatching {
            val bytes = tail(f) ?: return
            val name = "plnt-diag-$session-${System.currentTimeMillis()}-$why.log"
            val conn = URL(DROP_URL + name).openConnection() as HttpURLConnection
            conn.requestMethod = "PUT"
            conn.connectTimeout = 15_000
            conn.readTimeout = 15_000
            conn.doOutput = true
            conn.setRequestProperty("Authorization", "Basic " + Base64.encodeToString("$DROP_TOKEN:".toByteArray(), Base64.NO_WRAP))
            conn.setRequestProperty("X-Requested-With", "XMLHttpRequest")
            conn.setRequestProperty("Content-Type", "text/plain")
            conn.setFixedLengthStreamingMode(bytes.size)
            conn.outputStream.use { it.write(bytes) }
            val code = conn.responseCode
            conn.disconnect()
            Log.w(TAG, "upload $why -> HTTP $code (${bytes.size} bytes)")
        }.onFailure { Log.w(TAG, "upload $why failed: $it") }
    }

    @Synchronized
    fun add(line: String) {
        val stamped = "+${(System.currentTimeMillis() - start) / 1000}s $line"
        Log.w(TAG, stamped)
        file?.let { f ->
            runCatching {
                f.appendText(stamped + "\n")
                if (f.length() > MAX_FILE_BYTES) trim(f)
            }
        }
        dirty = true
        val heartbeat = line.startsWith("diag t=")
        if (!heartbeat) notable = true
        when {
            heartbeat -> { lastBeat = System.currentTimeMillis(); watchdogFired = false }
            line == "Connected" -> {
                connected = true
                lastBeat = System.currentTimeMillis()
                watchdogFired = false
                burstUntil = System.currentTimeMillis() + BURST_WINDOW_MS
            }
            line.startsWith("Disconnected") -> connected = false
        }
    }

    /** Cuts the file back to its last [KEEP_FILE_BYTES], from a line start. Caller holds the lock. */
    private fun trim(f: File) {
        val bytes = f.readBytes()
        var from = (bytes.size - KEEP_FILE_BYTES).coerceAtLeast(0)
        while (from < bytes.size && bytes[from] != '\n'.code.toByte()) from++
        f.writeBytes(bytes.copyOfRange((from + 1).coerceAtMost(bytes.size), bytes.size))
    }

    /**
     * The connection is gone — an unrequested drop, a reconnect tearing the
     * old client down, the radio going away. Stops the watchdog reading the
     * silence that follows as a stall; [unrequested] drops are shipped at once.
     */
    fun disconnected(detail: String, unrequested: Boolean) {
        add("Disconnected $detail")
        if (unrequested) {
            openBurst()
            uploadAsync("drop")
        }
    }

    // Watchdog: the core posts `diag t=` every 5 s while its loop runs. Silence
    // while connected means the loop (or the main thread relaying it) is stuck,
    // so dump every thread's stack and ship it at once, while it is still stuck.
    private var connected = false
    private var lastBeat = 0L
    private var watchdogFired = false

    @Synchronized
    private fun watchdogDue(): Boolean {
        if (!connected || watchdogFired || System.currentTimeMillis() - lastBeat < 8_000) return false
        watchdogFired = true
        return true
    }

    private fun threadDump(): String = buildString {
        for ((t, st) in Thread.getAllStackTraces()) {
            if (st.isEmpty()) continue
            append("  [").append(t.name).append(' ').append(t.state).append("]\n")
            st.take(8).forEach { append("    at ").append(it).append('\n') }
            if (length > 16_000) break
        }
    }

    private var callbackThrows = 0

    /** A core callback threw; before PHA-4176 that panicked the core's connection task. */
    fun callbackThrew(event: String, t: Throwable) {
        if (synchronized(this) { ++callbackThrows } > 5) return
        add("CALLBACK THREW on $event: $t\n" + t.stackTrace.take(10).joinToString("\n") { "    at $it" })
        uploadAsync("throw")
    }
}
