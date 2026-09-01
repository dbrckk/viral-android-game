package com.empiretycoon.idleconquest.game

import org.junit.Assert.assertEquals
import org.junit.Test

class OfflineProgressCalculatorTest {
    @Test
    fun calculatesElapsedSecondsAndEarnings() {
        val result = OfflineProgressCalculator.calculate(
            nowEpochMillis = 20_000L,
            savedAtEpochMillis = 10_000L,
            incomePerSecond = 2.5,
        )

        assertEquals(10L, result.seconds)
        assertEquals(25.0, result.earnings, 0.0)
    }

    @Test
    fun futureSaveProducesNoOfflineProgress() {
        val result = OfflineProgressCalculator.calculate(10_000L, 20_000L, 100.0)
        assertEquals(0L, result.seconds)
        assertEquals(0.0, result.earnings, 0.0)
    }

    @Test
    fun elapsedTimeIsCappedAtEightHours() {
        val result = OfflineProgressCalculator.calculate(
            nowEpochMillis = 48L * 60L * 60L * 1_000L,
            savedAtEpochMillis = 0L,
            incomePerSecond = 1.0,
        )
        assertEquals(OfflineProgressCalculator.MAX_OFFLINE_SECONDS, result.seconds)
    }

    @Test
    fun nonFiniteOrNegativeIncomeProducesNoEarnings() {
        assertEquals(0.0, OfflineProgressCalculator.calculate(10_000L, 0L, Double.NaN).earnings, 0.0)
        assertEquals(0.0, OfflineProgressCalculator.calculate(10_000L, 0L, Double.POSITIVE_INFINITY).earnings, 0.0)
        assertEquals(0.0, OfflineProgressCalculator.calculate(10_000L, 0L, -1.0).earnings, 0.0)
    }

    @Test
    fun timestampSubtractionOverflowStillCapsSafely() {
        val result = OfflineProgressCalculator.calculate(Long.MAX_VALUE, Long.MIN_VALUE, 1.0)
        assertEquals(OfflineProgressCalculator.MAX_OFFLINE_SECONDS, result.seconds)
        assertEquals(OfflineProgressCalculator.MAX_OFFLINE_SECONDS.toDouble(), result.earnings, 0.0)
    }
}
