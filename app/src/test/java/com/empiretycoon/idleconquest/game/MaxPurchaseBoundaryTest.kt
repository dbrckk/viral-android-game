package com.empiretycoon.idleconquest.game

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class MaxPurchaseBoundaryTest {
    private val business = BusinessState(
        id = "test",
        displayName = "Test",
        level = 1,
        baseCost = 25.0,
        baseIncomePerSecond = 1.0,
    )

    @Test
    fun exactGeometricCostsBuyExactlyThatManyLevels() {
        listOf(1, 2, 10, 25, 100).forEach { count ->
            val cost = business.upgradeCost(count = count)
            assertEquals(count, business.maxAffordableLevels(cost))
        }
    }

    @Test
    fun oneRepresentableStepBelowBoundaryCannotBuyExtraLevel() {
        listOf(2, 10, 25, 100).forEach { count ->
            val boundary = business.upgradeCost(count = count)
            val below = Math.nextDown(boundary)
            val affordable = business.maxAffordableLevels(below)

            assertTrue("count=$count affordable=$affordable", affordable <= count - 1)
            assertTrue(business.upgradeCost(count = affordable) <= below)
        }
    }

    @Test
    fun oneRepresentableStepAboveBoundaryStillNeverOverspends() {
        listOf(1, 10, 25, 100).forEach { count ->
            val cash = Math.nextUp(business.upgradeCost(count = count))
            val affordable = business.maxAffordableLevels(cash)

            assertTrue(affordable >= count)
            assertTrue(business.upgradeCost(count = affordable) <= cash)
            assertTrue(business.upgradeCost(count = affordable + 1) > cash)
        }
    }

    @Test
    fun costMultiplierRespectsSameBoundaryRules() {
        val multiplier = 0.84
        val count = 40
        val cash = business.upgradeCost(count = count) * multiplier

        assertEquals(count, business.maxAffordableLevels(cash, multiplier))
        assertTrue(business.upgradeCost(count = count + 1) * multiplier > cash)
    }
}
