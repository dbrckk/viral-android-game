package com.empiretycoon.idleconquest.ui

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class GameLoopTimingTest {
    @Test
    fun redrawCadenceSlowsOnlyInPowerSaveMode() {
        assertEquals(GameLoopTiming.REDRAW_DELAY_MILLIS, GameLoopTiming.redrawDelayMillis(false))
        assertEquals(GameLoopTiming.POWER_SAVE_REDRAW_DELAY_MILLIS, GameLoopTiming.redrawDelayMillis(true))
        assertTrue(GameLoopTiming.POWER_SAVE_REDRAW_DELAY_MILLIS > GameLoopTiming.REDRAW_DELAY_MILLIS)
    }

    @Test
    fun firstFrameAndNonMonotonicTimeProduceZeroDelta() {
        assertEquals(0.0, GameLoopTiming.frameDeltaSeconds(1_000L, 0L), 0.0)
        assertEquals(0.0, GameLoopTiming.frameDeltaSeconds(1_000L, 1_000L), 0.0)
        assertEquals(0.0, GameLoopTiming.frameDeltaSeconds(999L, 1_000L), 0.0)
    }

    @Test
    fun normalFrameDeltaIsPreserved() {
        assertEquals(
            0.05,
            GameLoopTiming.frameDeltaSeconds(1_050_000_000L, 1_000_000_000L),
            0.0000001,
        )
    }

    @Test
    fun longForegroundFrameDeltaIsPreserved() {
        assertEquals(
            9.0,
            GameLoopTiming.frameDeltaSeconds(10_000_000_000L, 1_000_000_000L),
            0.0,
        )
    }

    @Test
    fun elapsedTimeOverflowStaysFiniteAndPositive() {
        val delta = GameLoopTiming.frameDeltaSeconds(Long.MAX_VALUE, Long.MIN_VALUE + 1)
        assertTrue(delta.isFinite())
        assertTrue(delta > 0.0)
    }

    @Test
    fun autosaveRunsImmediatelyThenAtConfiguredInterval() {
        assertTrue(GameLoopTiming.shouldAutosave(1L, 0L))
        assertFalse(GameLoopTiming.shouldAutosave(10_999_999_999L, 1_000_000_000L))
        assertTrue(GameLoopTiming.shouldAutosave(11_000_000_000L, 1_000_000_000L))
    }

    @Test
    fun backwardsClockDoesNotTriggerAutosave() {
        assertFalse(GameLoopTiming.shouldAutosave(999L, 1_000L))
    }

    @Test
    fun autosaveOverflowCountsAsElapsedInterval() {
        assertTrue(GameLoopTiming.shouldAutosave(Long.MAX_VALUE, Long.MIN_VALUE + 1))
    }
}
