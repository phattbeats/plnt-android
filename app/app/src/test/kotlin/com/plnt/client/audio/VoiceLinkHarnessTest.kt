package com.plnt.client.audio

import org.junit.Assert.*
import org.junit.Test
import java.util.PriorityQueue
import java.util.Random

/**
 * PHA-4867: deterministic JVM harness for packet loss, jitter and simultaneous
 * talkers feeding the Android write tail ([PlaybackWriter]). Virtual time, seeded
 * RNG, no threads. The real jitter buffer/mixer live in plnt-core (Rust); this
 * models their contract (one mixed 20 ms frame per tick, silence on a miss) so
 * the Kotlin tail is exercised under realistic arrival patterns.
 *
 * Every talker emits a constant amplitude, so each delivered frame's value
 * identifies exactly which talkers were mixed into it.
 */
class VoiceLinkHarnessTest {
    private val frame = 960
    private val tickMs = 20L

    private class Packet(val talker: Int, val seq: Int, val arriveMs: Long)

    private class Net(val loss: Double, val jitterMs: Int, val baseMs: Int, seed: Long) {
        private val rnd = Random(seed)
        fun send(talker: Int, seq: Int, sentMs: Long): Packet? {
            if (rnd.nextDouble() < loss) return null
            val j = if (jitterMs > 0) rnd.nextInt(jitterMs + 1) else 0
            return Packet(talker, seq, sentMs + baseMs + j)
        }
    }

    private class Result(
        val perTickValue: List<Float>, val silentTicks: Int, val lateDrops: Int,
        val errors: List<Throwable>, val rejected: Int, val maxPending: Int,
    )

    /**
     * @param amps one entry per talker; talker i speaks for ticks in [start[i], end[i])
     * @param depthTicks jitter buffer depth; packets arriving after their playout tick are dropped
     * @param sinkStallTicks ticks during which the device accepts nothing (underrun/stall)
     */
    private fun run(
        amps: FloatArray, ticks: Int, net: Net, depthTicks: Int,
        start: IntArray = IntArray(amps.size), end: IntArray = IntArray(amps.size) { ticks },
        sinkStallTicks: IntRange = IntRange.EMPTY,
    ): Result {
        val arrivals = PriorityQueue<Packet>(compareBy { it.arriveMs })
        for (t in 0 until ticks) for (i in amps.indices) {
            if (t >= start[i] && t < end[i]) net.send(i, t, t * tickMs)?.let(arrivals::add)
        }
        val buffer = HashMap<Int, MutableSet<Int>>() // seq -> talkers present
        var lateDrops = 0
        var stalled = false
        val delivered = mutableListOf<Float>()
        val sink = object : PlaybackSink {
            override fun write(samples: FloatArray, offset: Int, count: Int): Int {
                if (stalled) return 0
                for (k in offset until offset + count) delivered += samples[k]
                return count
            }
            override fun close() {}
        }
        val errors = mutableListOf<Throwable>()
        val pending = ArrayDeque<Pair<Long, () -> Unit>>()
        val writer = PlaybackWriter({ sink }, { errors += it }, schedule = { d, task -> pending.addLast(d to task) })
        writer.reopen()

        var rejected = 0; var maxPending = 0; var silent = 0
        val values = mutableListOf<Float>()
        for (tick in 0 until ticks + depthTicks) {
            val now = tick * tickMs
            // network delivery up to this instant
            while (arrivals.isNotEmpty() && arrivals.peek().arriveMs <= now) {
                val p = arrivals.poll()
                if (p.seq + depthTicks < tick) lateDrops++ else buffer.getOrPut(p.seq) { mutableSetOf() }.add(p.talker)
            }
            // playout of seq = tick - depth
            val seq = tick - depthTicks
            if (seq >= 0) {
                val present = buffer.remove(seq).orEmpty()
                val mixed = present.sumOf { amps[it].toDouble() }.toFloat().coerceIn(-1f, 1f)
                values += mixed
                if (present.isEmpty() && (0 until amps.size).any { seq >= start[it] && seq < end[it] }) silent++
                if (!writer.offer(FloatArray(frame) { mixed })) rejected++
            }
            stalled = tick in sinkStallTicks
            // device drains: run at most one pass of queued work per tick
            repeat(pending.size) { pending.removeFirst().second() }
            maxPending = maxOf(maxPending, writer.pendingSamples)
        }
        lateDrops += arrivals.size // arrived after their final playout tick
        stalled = false
        var budget = 10_000
        while (pending.isNotEmpty() && budget-- > 0) pending.removeFirst().second()
        return Result(values, silent, lateDrops, errors, rejected, maxPending).also {
            // delivered stream must be an in-order, loss-free copy of what was accepted
            if (rejected == 0) assertEquals(values.size * frame, delivered.size)
            if (rejected == 0) for ((n, v) in values.withIndex()) assertEquals("tick $n", v, delivered[n * frame], 0f)
        }
    }

    @Test fun cleanLinkDeliversEveryFrameInOrder() {
        val r = run(floatArrayOf(0.5f), 200, Net(0.0, 0, 30, 1), depthTicks = 3)
        assertEquals(0, r.silentTicks); assertEquals(0, r.lateDrops)
        assertTrue(r.errors.isEmpty()); assertTrue(r.perTickValue.all { it == 0.5f })
    }

    @Test fun uniformLossProducesProportionalSilenceAndNoWriterErrors() {
        val ticks = 2_000
        val r = run(floatArrayOf(0.4f), ticks, Net(0.10, 0, 30, 7), depthTicks = 3)
        val rate = r.silentTicks.toDouble() / ticks
        assertTrue("loss rate $rate", rate in 0.07..0.13)
        assertTrue(r.errors.isEmpty()); assertEquals(0, r.rejected)
    }

    @Test fun jitterWithinBufferDepthLosesNothing() {
        // up to 40 ms jitter, 3-tick (60 ms) buffer
        val r = run(floatArrayOf(0.3f), 500, Net(0.0, 40, 20, 3), depthTicks = 3)
        assertEquals(0, r.lateDrops); assertEquals(0, r.silentTicks)
    }

    @Test fun jitterBeyondBufferDepthDropsLatePacketsButStaysOrdered() {
        val r = run(floatArrayOf(0.3f), 1_000, Net(0.0, 120, 20, 3), depthTicks = 1)
        assertTrue("late drops ${r.lateDrops}", r.lateDrops > 100)
        assertEquals(r.lateDrops, r.silentTicks)
        assertTrue(r.errors.isEmpty())
    }

    @Test fun simultaneousTalkersMixAndClip() {
        val amps = floatArrayOf(0.2f, 0.3f, 0.4f, 0.5f)
        val r = run(amps, 100, Net(0.0, 0, 20, 1), depthTicks = 2)
        assertTrue(r.perTickValue.all { it == 1f }) // 1.4 clipped to full scale
        val two = run(floatArrayOf(0.2f, 0.3f), 50, Net(0.0, 0, 20, 1), depthTicks = 2)
        assertTrue(two.perTickValue.all { Math.abs(it - 0.5f) < 1e-6f })
    }

    @Test fun talkersJoiningAndLeavingMidStreamChangeMixPerTick() {
        val r = run(
            floatArrayOf(0.1f, 0.2f), 90, Net(0.0, 0, 20, 1), depthTicks = 2,
            start = intArrayOf(0, 30), end = intArrayOf(60, 90),
        )
        assertEquals(0.1f, r.perTickValue[10], 1e-6f)
        assertEquals(0.3f, r.perTickValue[45], 1e-6f)
        assertEquals(0.2f, r.perTickValue[75], 1e-6f)
        assertEquals(0, r.silentTicks)
    }

    @Test fun manyTalkersWithLossAndJitterStayWithinBufferBounds() {
        val amps = FloatArray(8) { 0.05f }
        val r = run(amps, 1_500, Net(0.05, 60, 25, 11), depthTicks = 4)
        assertTrue(r.errors.isEmpty()); assertEquals(0, r.rejected)
        assertTrue("pending ${r.maxPending}", r.maxPending <= 9_600)
    }

    @Test fun deviceStallWithinBudgetRecoversWithoutLossOrReorder() {
        // 120 ms stall = 6 frames = 5760 samples < 9600 cap: everything must still arrive in order
        val r = run(floatArrayOf(0.6f), 200, Net(0.0, 0, 20, 1), depthTicks = 2, sinkStallTicks = 50..55)
        assertEquals(0, r.rejected); assertTrue(r.errors.isEmpty())
        assertTrue(r.maxPending >= 5 * frame)
    }

    @Test fun deviceStallBeyondCapRejectsNewestAndReportsOnce() {
        val r = run(floatArrayOf(0.6f), 200, Net(0.0, 0, 20, 1), depthTicks = 2, sinkStallTicks = 50..80)
        assertTrue(r.rejected > 0); assertEquals(1, r.errors.size)
        assertTrue(r.maxPending <= 9_600)
    }

    @Test fun sameSeedIsReproducible() {
        val a = run(floatArrayOf(0.1f, 0.2f), 300, Net(0.1, 50, 20, 99), depthTicks = 2)
        val b = run(floatArrayOf(0.1f, 0.2f), 300, Net(0.1, 50, 20, 99), depthTicks = 2)
        assertEquals(a.perTickValue, b.perTickValue)
    }
}
