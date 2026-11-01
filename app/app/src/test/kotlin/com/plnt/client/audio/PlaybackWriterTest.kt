package com.plnt.client.audio

import org.junit.Assert.*
import org.junit.Test
import java.util.ArrayDeque

class PlaybackWriterTest {
    private class Clock {
        val tasks = ArrayDeque<Pair<Long, () -> Unit>>()
        val delays = mutableListOf<Long>()
        fun schedule(delay: Long, task: () -> Unit) { tasks.addLast(delay to task) }
        fun next() { val (delay, task) = tasks.removeFirst(); delays += delay; task() }
        fun drain() { var budget = 1_000; while (tasks.isNotEmpty()) { check(budget-- > 0); next() } }
    }

    private class Sink(
        val delivered: MutableList<Float> = mutableListOf(),
        vararg results: Int,
    ) : PlaybackSink {
        val results = ArrayDeque<Int>().apply { results.forEach(::addLast) }
        val requests = mutableListOf<Pair<Int, Int>>()
        var closes = 0
        var throwWrite = false
        var throwClose = false
        override fun write(samples: FloatArray, offset: Int, count: Int): Int {
            check(closes == 0) { "write after release" }
            requests += offset to count
            if (throwWrite) throw IllegalStateException("write failed")
            val n = if (results.isEmpty()) count else results.removeFirst()
            if (n in 1..count) delivered += samples.slice(offset until offset + n)
            return n
        }
        override fun close() { closes++; if (throwClose) throw IllegalStateException("close failed") }
    }

    private class Rig(val sinks: List<Sink> = listOf(Sink()), max: Int = 16) {
        val clock = Clock()
        val errors = mutableListOf<Throwable>()
        var opens = 0
        var schedulerCloses = 0
        var failOpen = false
        val writer = PlaybackWriter(
            openSink = { if (failOpen) throw IllegalStateException("open failed"); sinks[opens++] },
            onError = { errors += it }, maxSamples = max,
            schedule = clock::schedule, closeScheduler = { schedulerCloses++ },
        )
        init { writer.reopen() }
        fun offer(vararg samples: Float) = writer.offer(samples)
    }

    @Test fun fullWriteDrainsAndStopsScheduling() {
        val r = Rig(); assertTrue(r.offer(1f, 2f)); r.clock.drain()
        assertEquals(listOf(1f, 2f), r.sinks[0].delivered)
        assertEquals(0, r.writer.pendingSamples); assertTrue(r.errors.isEmpty())
    }

    @Test fun partialTailsPrecedeNewFrames() {
        val sink = Sink(mutableListOf(), 1, 1, 1)
        val r = Rig(listOf(sink)); r.offer(1f, 2f, 3f); r.clock.next(); r.offer(4f, 5f)
        r.clock.drain()
        assertEquals(listOf(1f, 2f, 3f, 4f, 5f), sink.delivered)
        assertEquals(listOf(0 to 3, 1 to 2, 2 to 1, 0 to 2), sink.requests)
    }

    @Test fun zeroRetainsTailAndSchedulesDelayedRetry() {
        val sink = Sink(mutableListOf(), 1, 0)
        val r = Rig(listOf(sink)); r.offer(1f, 2f); r.clock.next(); r.clock.next()
        assertEquals(1, r.writer.pendingSamples)
        assertEquals(10L, r.clock.tasks.first.first)
        assertEquals(2, sink.requests.size)
        r.clock.drain(); assertEquals(listOf(1f, 2f), sink.delivered)
    }

    @Test fun producerCannotMutateQueuedFrame() {
        val r = Rig(); val frame = floatArrayOf(1f, 2f)
        r.writer.offer(frame); frame.fill(9f); r.clock.drain()
        assertEquals(listOf(1f, 2f), r.sinks[0].delivered)
    }

    @Test fun overflowRejectsNewestWithoutDroppingTail() {
        val sink = Sink(mutableListOf(), 1, 0)
        val r = Rig(listOf(sink), max = 3)
        r.offer(1f, 2f, 3f); r.clock.next()
        assertTrue(r.offer(4f)); assertFalse(r.offer(5f)); assertFalse(r.offer(6f))
        assertEquals(3, r.writer.pendingSamples); assertEquals(1, r.errors.size)
        r.clock.drain(); assertEquals(listOf(1f, 2f, 3f, 4f), sink.delivered)
    }

    @Test fun oversizedFrameRejectedWithoutScheduling() {
        val r = Rig(max = 1); assertFalse(r.offer(1f, 2f))
        assertEquals(0, r.writer.pendingSamples); assertTrue(r.clock.tasks.isEmpty())
        assertEquals(1, r.errors.size)
    }

    @Test fun emptyFrameDoesNotSchedule() {
        val r = Rig(); assertTrue(r.offer()); assertTrue(r.clock.tasks.isEmpty())
    }

    @Test fun multipleOffersCoalesceWakeup() {
        val r = Rig(); repeat(10) { r.offer(it.toFloat()) }
        assertEquals(1, r.clock.tasks.size); r.clock.drain()
        assertEquals((0..9).map(Int::toFloat), r.sinks[0].delivered)
    }

    @Test fun deadObjectRecoversWithoutReplayingAcceptedPrefix() {
        val delivered = mutableListOf<Float>()
        val a = Sink(delivered, 1, -6); val b = Sink(delivered)
        val r = Rig(listOf(a, b)); r.offer(1f, 2f, 3f); r.clock.next(); r.clock.next()
        assertEquals(1, a.closes); assertEquals(20L, r.clock.tasks.first.first)
        r.offer(4f); r.clock.drain()
        assertEquals(listOf(1f, 2f, 3f, 4f), delivered)
        assertEquals(1 to 2, b.requests.first()); assertEquals(2, r.opens)
    }

    @Test fun everyNegativeErrorUsesBoundedRecovery() {
        for (code in listOf(-1, -2, -3, -6, -99)) {
            val a = Sink(mutableListOf(), code); val b = Sink()
            val r = Rig(listOf(a, b)); r.offer(7f); r.clock.drain()
            assertEquals(listOf(7f), b.delivered); assertEquals(1, a.closes)
            assertEquals(1, r.errors.size)
        }
    }

    @Test fun writeExceptionAlsoRecovers() {
        val a = Sink().apply { throwWrite = true }; val b = Sink()
        val r = Rig(listOf(a, b)); r.offer(1f); r.clock.drain()
        assertEquals(listOf(1f), b.delivered); assertEquals(1, a.closes)
    }

    @Test fun repeatedErrorsStopAtThreeReopensAndRetainPcm() {
        val sinks = List(4) { Sink(mutableListOf(), -6) }
        val r = Rig(sinks); r.offer(1f, 2f); r.clock.drain()
        assertEquals(4, r.opens); assertEquals(2, r.writer.pendingSamples)
        assertTrue(sinks.all { it.closes == 1 }); assertFalse(r.offer(3f))
        assertTrue(r.errors.last().message!!.contains("exhausted"))
    }

    @Test fun sustainedZeroStopsWithoutSpinOrLoss() {
        val sink = Sink(mutableListOf(), *IntArray(200))
        val r = Rig(listOf(sink)); r.offer(1f); r.clock.drain()
        assertEquals(200, sink.requests.size)
        assertEquals(199, r.clock.delays.count { it == 10L })
        assertEquals(1, r.writer.pendingSamples); assertEquals(1, sink.closes)
        assertTrue(r.errors.single().message!!.contains("stalled")); assertFalse(r.offer(2f))
    }

    @Test fun routeReopenInvalidatesOldTaskButPreservesTail() {
        val delivered = mutableListOf<Float>(); val a = Sink(delivered, 1); val b = Sink(delivered)
        val r = Rig(listOf(a, b)); r.offer(1f, 2f); r.clock.next(); r.writer.reopen()
        r.clock.drain(); assertEquals(listOf(1f, 2f), delivered)
        assertEquals(1, a.requests.size); assertEquals(1, a.closes); assertEquals(1, b.requests.size)
    }

    @Test fun explicitReopenResumesFailedOutput() {
        val a = Sink(mutableListOf(), *IntArray(200)); val b = Sink()
        val r = Rig(listOf(a, b)); r.offer(1f); r.clock.drain()
        r.writer.reopen(); r.offer(2f); r.clock.drain()
        assertEquals(listOf(1f, 2f), b.delivered)
    }

    @Test fun initialAcquisitionFailureReportedAndRecoverable() {
        val clock = Clock(); val errors = mutableListOf<Throwable>(); val sink = Sink(); var fail = true
        val writer = PlaybackWriter({ if (fail) error("unavailable") else sink }, { errors += it }, schedule = clock::schedule)
        writer.reopen(); assertFalse(writer.offer(floatArrayOf(1f))); assertEquals(1, errors.size)
        fail = false; writer.reopen(); assertTrue(writer.offer(floatArrayOf(2f))); clock.drain()
        assertEquals(listOf(2f), sink.delivered)
    }

    @Test fun failedRecoveryAcquisitionRetainsTail() {
        val a = Sink(mutableListOf(), 1, -6); val r = Rig(listOf(a))
        r.offer(1f, 2f); r.clock.next(); r.clock.next(); r.failOpen = true; r.clock.drain()
        assertEquals(1, r.writer.pendingSamples); assertFalse(r.offer(3f))
        assertTrue(r.errors.last().message!!.contains("unavailable"))
    }

    @Test fun closeIsIdempotentAndStaleTasksCannotWriteOrReopen() {
        val r = Rig(); r.offer(1f); r.writer.close(); r.writer.close(); r.writer.reopen(); r.clock.drain()
        assertEquals(1, r.sinks[0].closes); assertEquals(1, r.schedulerCloses)
        assertEquals(1, r.opens); assertTrue(r.sinks[0].requests.isEmpty())
        assertEquals(0, r.writer.pendingSamples); assertFalse(r.offer(2f))
    }

    @Test fun closeDuringPendingRecoveryNeverAcquiresAnotherSink() {
        val a = Sink(mutableListOf(), -6); val r = Rig(listOf(a))
        r.offer(1f); r.clock.next(); r.writer.close(); r.clock.drain()
        assertEquals(1, a.closes); assertEquals(1, r.opens)
    }

    @Test fun cleanupExceptionStillClosesSchedulerAndReports() {
        val r = Rig(listOf(Sink().apply { throwClose = true }))
        r.writer.close(); assertEquals(1, r.schedulerCloses); assertEquals(1, r.errors.size)
    }

    @Test fun invalidPositiveCountFailsWithoutRemovingSamples() {
        val r = Rig(listOf(Sink(mutableListOf(), 3))); r.offer(1f, 2f); r.clock.drain()
        assertEquals(2, r.writer.pendingSamples); assertEquals(1, r.sinks[0].closes)
        assertTrue(r.errors.single().message!!.contains("Invalid"))
    }

    @Test fun progressResetsRecoveryBudget() {
        val delivered = mutableListOf<Float>()
        val sinks = List(5) { Sink(delivered, 1, -6) }
        val r = Rig(sinks); r.offer(1f, 2f, 3f, 4f, 5f); r.clock.drain()
        assertEquals(listOf(1f, 2f, 3f, 4f, 5f), delivered)
        assertEquals(5, r.opens); assertEquals(0, r.writer.pendingSamples)
        assertFalse(r.errors.any { it.message!!.contains("exhausted") })
    }

    @Test fun closeCancelsDelayedZeroRetry() {
        val a = Sink(mutableListOf(), 0); val r = Rig(listOf(a))
        r.offer(1f); r.clock.next(); r.writer.close(); r.clock.drain()
        assertEquals(1, a.requests.size); assertEquals(1, a.closes)
        assertEquals(0, r.writer.pendingSamples)
    }

    @Test fun failingErrorObserverCannotKillWorkerOrCleanup() {
        val clock = Clock(); val a = Sink(mutableListOf(), -6); val b = Sink(); var opens = 0
        val writer = PlaybackWriter({ listOf(a, b)[opens++] }, { error("observer failed") }, schedule = clock::schedule)
        writer.reopen(); writer.offer(floatArrayOf(1f)); clock.drain(); writer.close()
        assertEquals(listOf(1f), b.delivered); assertEquals(1, a.closes); assertEquals(1, b.closes)
    }
}
