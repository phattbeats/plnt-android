package com.plnt.client.service

import android.os.Build
import android.util.Base64
import android.util.Log
import java.io.File
import java.net.HttpURLConnection
import java.net.URL

/**
 * PHA-4176 diagnostic trace. Keeps a bounded timeline of connection events and
 * the core's `diag ...` lines so a ping-timeout drop can be read after the
 * fact without anyone attaching logcat. [report] is posted to the channel once
 * the app is back on the server, and the whole log file is uploaded to a
 * Nextcloud upload-only drop on its own (start-up, every 15 s while it
 * grows, and right after a drop), so nobody has to fetch it from the phone.
 * A watchdog ships a thread dump the moment the core's heartbeat stops.
 */
object Diag {
    private const val TAG = "plnt.diag"
    private const val MAX_LINES = 120
    private val lines = ArrayDeque<String>()
    private var file: File? = null
    private var pendingReport = false
    private val start = System.currentTimeMillis()

    // Upload-only public share: anyone with the token can drop files, nobody
    // can list or read them back.
    private const val DROP_URL = "https://nextcloud.phatt.vip/public.php/webdav/"
    private const val DROP_TOKEN = "nLw2cdSfQNTie3x"
    private const val MAX_UPLOAD_BYTES = 256 * 1024
    private val session = "${Build.MODEL.replace(Regex("[^A-Za-z0-9]"), "")}-$start"
    private var dirty = false

    fun init(dir: File) {
        if (file != null) return
        file = File(dir, "plnt-diag.log")
        // The previous run's trace (if the process died) goes up first, then
        // a background loop ships the file whenever it has grown.
        uploadAsync("start")
        Thread({
            var n = 0
            while (true) {
                Thread.sleep(5_000)
                n++
                if (watchdogDue()) {
                    add("WATCHDOG no core heartbeat for ${(System.currentTimeMillis() - lastBeat) / 1000}s\n" + threadDump())
                    upload("stall")
                } else if (n % 3 == 0 && synchronized(this) { dirty }) {
                    upload("tick")
                }
            }
        }, "plnt-diag-upload").apply { isDaemon = true }.start()
    }

    fun uploadAsync(why: String) {
        Thread({ upload(why) }, "plnt-diag-upload-now").apply { isDaemon = true }.start()
    }

    private fun upload(why: String) {
        val f = file ?: return
        runCatching {
            if (!f.exists() || f.length() == 0L) return
            val bytes = f.readBytes().let { if (it.size > MAX_UPLOAD_BYTES) it.copyOfRange(it.size - MAX_UPLOAD_BYTES, it.size) else it }
            synchronized(this) { dirty = false }
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
        lines.addLast(stamped)
        while (lines.size > MAX_LINES) lines.removeFirst()
        runCatching { file?.appendText(stamped + "\n") }
        dirty = true
        when {
            line.startsWith("diag t=") -> { lastBeat = System.currentTimeMillis(); watchdogFired = false }
            line == "Connected" -> { connected = true; lastBeat = System.currentTimeMillis(); watchdogFired = false }
            line.startsWith("Disconnected") -> connected = false
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

    /** Mark that the last session ended badly; the next Connected posts the trace. */
    @Synchronized
    fun armReport() { pendingReport = true }

    /** Non-null exactly once after [armReport], when the trace should be posted. */
    @Synchronized
    fun takeReport(): String? {
        if (!pendingReport) return null
        pendingReport = false
        // Drop the periodic heartbeat lines that report a clean loop; keep gaps,
        // errors and lifecycle so the message stays chat-sized.
        val keep = lines.filter { !it.contains("max_gap_ms=") || it.contains("audio_err=") && !it.contains("audio_err=0") }
        val tail = (if (keep.size > 30) keep.takeLast(30) else keep)
        val last = lines.lastOrNull { it.contains("max_gap_ms=") }
        return buildString {
            append("[PLNT diag PHA-4176] previous session trace:\n")
            tail.forEach { append(it).append('\n') }
            if (last != null) append("last heartbeat: ").append(last)
        }.take(1000)
    }
}
