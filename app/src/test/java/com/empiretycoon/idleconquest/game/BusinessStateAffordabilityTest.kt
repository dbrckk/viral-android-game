package com.empiretycoon.idleconquest.game

import org.junit.Assert.assertEquals
import org.junit.Test

class BusinessStateAffordabilityTest {
    private val business = BusinessState(
        id = "test",
        displayName = "Test",
        level = 1,
        baseCost = 25.0,
        baseIncomePerSecond = 1.0,
    )

    @Test
    fun exactGeometricCostsBuyExactlyExpectedLevels() {
        for (count in 1..100) {
            val cash = business.upgradeCost(count = count)
            assertEquals("count=$count", count, business.maxAffordableLevels(cash))
        }
    }

    @Test
    fun justBelowNextCostNeverOverQuotes() {
        for (count in 1..100) {
            val nextCost = business.upgradeCost(count = count + 1)
            val cash = Math.nextDown(nextCost)
            val quoted = business.maxAffordableLevels(cash)
            assertEquals("count=$count", count, quoted)
        }
    }

    @Test
    fun costMultiplierIsIncludedInBoundaryCalculation() {
        val multiplier = 0.86
        for (count in listOf(1, 10, 25, 50, 100)) {
            val cash = business.upgradeCost(count = count) * multiplier
            assertEquals(count, business.maxAffordableLevels(cash, multiplier))
        }
    }

    @Test
    fun invalidInputsNeverQuoteLevels() {
        assertEquals(0, business.maxAffordableLevels(Double.NaN))
        assertEquals(0, business.maxAffordableLevels(Double.POSITIVE_INFINITY))
        assertEquals(0, business.maxAffordableLevels(-1.0))
        assertEquals(0, business.maxAffordableLevels(1_000.0, 0.0))
        assertEquals(0, business.maxAffordableLevels(1_000.0, Double.NaN))
    }
}
