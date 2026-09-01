package com.empiretycoon.idleconquest.game

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class BackgroundSessionProgressTest {
    @Test
    fun resumeWithoutPauseDoesNothing() {
        assertNull(BackgroundSessionProgress().consumeResume(10_000L, 5.0))
    }

    @Test
    fun resumeConsumesPauseExactlyOnce() {
        val tracker = BackgroundSessionProgress()
        tracker.markPaused(10_000L)

        val first = tracker.consumeResume(15_000L, 3.0)
        assertEquals(5L, first!!.seconds)
        assertEquals(15.0, first.earnings, 0.0)
        assertNull(tracker.consumeResume(20_000L, 3.0))
    }

    @Test
    fun laterPauseReplacesEarlierPauseTimestamp() {
        val tracker = BackgroundSessionProgress()
        tracker.markPaused(10_000L)
        tracker.markPaused(12_000L)

        val result = tracker.consumeResume(15_000L, 2.0)
        assertEquals(3L, result!!.seconds)
        assertEquals(6.0, result.earnings, 0.0)
    }
}
