package com.plnt.client.audio

import java.util.ArrayDeque
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

/** All sink operations are serialized by [PlaybackWriter]. */
internal interface PlaybackSink {
    fun write(samples: FloatArray, offset: Int, count: Int): Int
    fun close()
}

/**
 * A write acknowledges samples, not frames. Only accepted prefixes are removed;
 * newer frames never overtake a tail. Overflow rejects the NEW frame explicitly.
 * One scheduled write per turn keeps callbacks fast and zero writes off a busy
 * loop. Errors get at most three reopen attempts; 200 consecutive zero writes
 * disable output. Pending PCM stays bounded and survives an explicit reopen,
 * but is intentionally abandoned on close. Android adds its own device latency.
 */
internal class PlaybackWriter(
    private val openSink: () -> PlaybackSink,
    private val onError: (Throwable) -> Unit,
    private val maxSamples: Int = 9_600, // 200 ms at 48 kHz mono
    private val schedule: (Long, () -> Unit) -> Unit,
    private val closeScheduler: () -> Unit = {},
) {
    private val queue = ArrayDeque<FloatArray>()
    private var offset = 0
    internal var pendingSamples = 0
        private set
    private var sink: PlaybackSink? = null
    private var closed = false
    private var failed = false
    private var scheduled = false
    private var generation = 0L
    private var recoveries = 0
    private var zeroWrites = 0
    private var overflowReported = false

    init { require(maxSamples > 0) }

    @Synchronized fun reopen() {
        if (closed) return
        generation++ // invalidate a task already queued for the old route
        scheduled = false
        releaseSink()
        failed = false
        recoveries = 0
        zeroWrites = 0
        if (acquire()) wake(0)
    }

    @Synchronized fun offer(samples: FloatArray): Boolean {
        if (closed || failed) return false
        if (samples.isEmpty()) return true
        if (samples.size > maxSamples - pendingSamples) {
            if (!overflowReported) {
                overflowReported = true
                report(IllegalStateException("Playback buffer full; rejected " + samples.size + " new samples; retained " + pendingSamples + " queued samples"))
            }
            return false
        }
        queue.addLast(samples.copyOf()) // producer may immediately reuse its frame
        pendingSamples += samples.size
        wake(0)
        return true
    }

    @Synchronized fun close() {
        if (closed) return
        closed = true
        generation++
        scheduled = false
        releaseSink()
        queue.clear()
        pendingSamples = 0
        offset = 0
        closeScheduler()
    }

    private fun acquire(): Boolean = try {
        sink = openSink()
        true
    } catch (e: Exception) {
        fail(IllegalStateException("Playback unavailable; retained " + pendingSamples + " queued samples", e))
        false
    }

    private fun releaseSink() {
        val old = sink
        sink = null
        if (old != null) try { old.close() } catch (e: Exception) { report(e) }
    }

    private fun wake(delayMs: Long) {
        if (closed || failed || scheduled || queue.isEmpty()) return
        scheduled = true
        val token = generation
        schedule(delayMs) { drain(token) }
    }

    @Synchronized private fun drain(token: Long) {
        if (closed || token != generation) return
        scheduled = false
        if (failed || queue.isEmpty()) return
        if (sink == null && !acquire()) return
        val frame = queue.first
        val remaining = frame.size - offset
        val n = try {
            sink!!.write(frame, offset, remaining)
        } catch (e: Exception) {
            recover(e)
            return
        }
        when {
            n in 1..remaining -> {
                offset += n
                pendingSamples -= n
                if (offset == frame.size) { queue.removeFirst(); offset = 0 }
                recoveries = 0
                zeroWrites = 0
                overflowReported = false
                wake(0)
            }
            n == 0 -> {
                zeroWrites++
                if (zeroWrites >= 200) fail(IllegalStateException("Playback stalled for 200 retries; retained " + pendingSamples + " queued samples"))
                else wake(10)
            }
            n < 0 -> recover(IllegalStateException("AudioTrack.write returned " + n + "; retained " + pendingSamples + " queued samples"))
            else -> fail(IllegalStateException("Invalid playback write count " + n + " > " + remaining))
        }
    }

    private fun recover(error: Exception) {
        releaseSink() // includes ERROR_DEAD_OBJECT (-6); never reuse a dead track
        recoveries++
        if (recoveries > 3) {
            fail(IllegalStateException("Playback recovery exhausted; retained " + pendingSamples + " queued samples", error))
        } else {
            report(error)
            wake(20) // next turn acquires a fresh sink, preserving the pending tail
        }
    }

    private fun fail(error: Exception) {
        failed = true
        releaseSink()
        report(error)
    }

    private fun report(error: Throwable) { runCatching { onError(error) } }

    companion object {
        fun threaded(openSink: () -> PlaybackSink, onError: (Throwable) -> Unit): PlaybackWriter {
            val executor = Executors.newSingleThreadScheduledExecutor { task ->
                Thread(task, "plnt-audio-playback").apply { isDaemon = true }
            }
            return PlaybackWriter(openSink, onError,
                schedule = { delay, task -> executor.schedule({ task() }, delay, TimeUnit.MILLISECONDS); Unit },
                closeScheduler = { executor.shutdownNow(); Unit })
        }
    }
}
