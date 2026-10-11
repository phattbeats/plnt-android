package com.plnt.client.service

import android.util.Log
import java.io.File

/**
 * PHA-4176 diagnostic trace. Keeps a bounded timeline of connection events and
 * the core's `diag ...` lines so a ping-timeout drop can be read after the
 * fact without anyone attaching logcat. [report] is posted to the channel once
 * the app is back on the server.
 */
object Diag {
    private const val TAG = "plnt.diag"
    private const val MAX_LINES = 120
    private val lines = ArrayDeque<String>()
    private var file: File? = null
    private var pendingReport = false
    private val start = System.currentTimeMillis()

    fun init(dir: File) {
        if (file == null) file = File(dir, "plnt-diag.log")
    }

    @Synchronized
    fun add(line: String) {
        val stamped = "+${(System.currentTimeMillis() - start) / 1000}s $line"
        Log.w(TAG, stamped)
        lines.addLast(stamped)
        while (lines.size > MAX_LINES) lines.removeFirst()
        runCatching { file?.appendText(stamped + "\n") }
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
