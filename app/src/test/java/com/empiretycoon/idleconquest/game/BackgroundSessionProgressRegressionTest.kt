package com.empiretycoon.idleconquest.game

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class BackgroundSessionProgressRegressionTest {
    @Test
    fun resumeWithoutPauseDoesNothing() {
        assertNull(BackgroundSessionProgress().consumeResume(10_000L, 5.0))
    }

    @Test
    fun pauseIsConsumedExactlyOnce() {
        val tracker = BackgroundSessionProgress()
        tracker.markPaused(10_000L)

        val first = tracker.consumeResume(15_000L, 3.0)
        assertEquals(5L, first!!.seconds)
        assertEquals(15.0, first.earnings, 0.0)
        assertNull(tracker.consumeResume(20_000L, 3.0))
    }

    @Test
    fun repeatedPauseKeepsOriginalBackgroundTimestamp() {
        val tracker = BackgroundSessionProgress()
        tracker.markPaused(10_000L)
        tracker.markPaused(12_000L)

        assertEquals(10_000L, tracker.saveTimestamp(13_000L))
        val result = tracker.consumeResume(15_000L, 2.0)
        assertEquals(5L, result!!.seconds)
        assertEquals(10.0, result.earnings, 0.0)
    }

    @Test
    fun activeSaveUsesCurrentTime() {
        assertEquals(50_000L, BackgroundSessionProgress().saveTimestamp(50_000L))
    }

    @Test
    fun backgroundEarningsCountTowardPrestige() {
        val state = GameState()
        val tracker = BackgroundSessionProgress()
        val cashBefore = state.cash
        tracker.markPaused(0L)

        val progress = tracker.consumeResume(
            nowEpochMillis = 3_600_000L,
            incomePerSecond = state.totalIncomePerSecond,
        )!!
        state.addCash(progress.earnings, countForPrestige = true)

        assertEquals(3_600L, progress.seconds)
        assertTrue(state.cash > cashBefore)
        assertEquals(progress.earnings, state.runEarnings, 0.0001)
        assertTrue(state.prestigeQuote().available)
    }
}
