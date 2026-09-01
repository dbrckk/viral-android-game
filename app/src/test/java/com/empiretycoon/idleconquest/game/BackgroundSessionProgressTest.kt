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
    fun repeatedPausePreservesOriginalPauseTimestamp() {
        val tracker = BackgroundSessionProgress()
        tracker.markPaused(10_000L)
        tracker.markPaused(12_000L)

        assertEquals(10_000L, tracker.saveTimestamp(13_000L))
        val result = tracker.consumeResume(15_000L, 2.0)
        assertEquals(5L, result!!.seconds)
        assertEquals(10.0, result.earnings, 0.0)
    }

    @Test
    fun saveTimestampUsesPauseTimeWhileBackgrounded() {
        val tracker = BackgroundSessionProgress()
        tracker.markPaused(10_000L)

        assertEquals(10_000L, tracker.saveTimestamp(50_000L))
    }

    @Test
    fun saveTimestampUsesCurrentTimeWhenActive() {
        val tracker = BackgroundSessionProgress()
        assertEquals(50_000L, tracker.saveTimestamp(50_000L))
    }
}
