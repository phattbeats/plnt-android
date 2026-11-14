package com.plnt.client.audio

import java.util.ArrayDeque
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit

/**
 * Sums decoded frames from several speakers into one real-time frame stream.
 * The core delivers one 20 ms frame per speaker; handing each straight to a
 * single-stream sink fills it at N x real time with N talkers. Each [tick]
 * pops at most one frame per source, sums them (clamped) and emits ONE frame.
 * Per-source backlog is bounded; the oldest frame is dropped, so a burst from
 * one speaker cannot grow latency.
 */
internal class PcmMixer(
    private val frameSamples: Int,
    private val emit: (FloatArray) -> Unit,
    private val maxFramesPerSource: Int = 5,
) {
    private val sources = HashMap<Int, ArrayDeque<FloatArray>>()

    @Synchronized fun push(sourceId: Int, samples: FloatArray) {
        if (samples.size != frameSamples) return
        val q = sources.getOrPut(sourceId) { ArrayDeque() }
        if (q.size >= maxFramesPerSource) q.removeFirst()
        q.addLast(samples.copyOf())
    }

    fun tick() {
        val out = synchronized(this) {
            if (sources.isEmpty()) return
            val mix = FloatArray(frameSamples)
            val it = sources.values.iterator()
            while (it.hasNext()) {
                val q = it.next()
                val f = q.pollFirst()
                if (f != null) for (i in 0 until frameSamples) mix[i] += f[i]
                if (q.isEmpty()) it.remove()
            }
            for (i in 0 until frameSamples) mix[i] = mix[i].coerceIn(-1f, 1f)
            mix
        }
        emit(out)
    }

    @Synchronized fun clear() = sources.clear()

    companion object {
        fun start(frameSamples: Int, periodMs: Long, emit: (FloatArray) -> Unit): Pair<PcmMixer, ScheduledExecutorService> {
            val mixer = PcmMixer(frameSamples, emit)
            val ex = Executors.newSingleThreadScheduledExecutor { Thread(it, "plnt-audio-mixer").apply { isDaemon = true } }
            ex.scheduleAtFixedRate({ runCatching { mixer.tick() } }, periodMs, periodMs, TimeUnit.MILLISECONDS)
            return mixer to ex
        }
    }
}
