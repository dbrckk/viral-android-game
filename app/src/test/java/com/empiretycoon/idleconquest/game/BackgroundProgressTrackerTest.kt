package com.empiretycoon.idleconquest.game

import org.junit.Assert.assertEquals
import org.junit.Test

class BackgroundProgressTrackerTest {
    @Test
    fun resumeCalculatesProgressFromPauseTime() {
        val tracker = BackgroundProgressTracker()
        tracker.pause(1_000L)

        val progress = tracker.resume(6_000L, 10.0)

        assertEquals(5L, progress.seconds)
        assertEquals(50.0, progress.earnings, 0.0)
    }

    @Test
    fun repeatedPauseKeepsOriginalTimestamp() {
        val tracker = BackgroundProgressTracker()
        tracker.pause(1_000L)
        tracker.pause(4_000L)

        assertEquals(1_000L, tracker.saveTimestamp(9_000L))
        val progress = tracker.resume(6_000L, 2.0)
        assertEquals(5L, progress.seconds)
    }

    @Test
    fun resumeConsumesPauseOnlyOnce() {
        val tracker = BackgroundProgressTracker()
        tracker.pause(1_000L)

        tracker.resume(6_000L, 10.0)
        val second = tracker.resume(10_000L, 10.0)

        assertEquals(0L, second.seconds)
        assertEquals(0.0, second.earnings, 0.0)
    }

    @Test
    fun activeSaveUsesCurrentTimestamp() {
        val tracker = BackgroundProgressTracker()
        assertEquals(5_000L, tracker.saveTimestamp(5_000L))
    }
}
