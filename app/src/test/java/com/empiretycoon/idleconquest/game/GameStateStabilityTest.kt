package com.empiretycoon.idleconquest.game

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class GameStateStabilityTest {

    @Test
    fun infiniteAndNanTicksDoNotCorruptEconomy() {
        val state = GameState()
        val beforeCash = state.cash
        val beforeRunEarnings = state.runEarnings

        state.tick(Double.NaN)
        state.tick(Double.POSITIVE_INFINITY)
        state.tick(Double.NEGATIVE_INFINITY)

        assertEquals(beforeCash, state.cash, 0.0)
        assertEquals(beforeRunEarnings, state.runEarnings, 0.0)
        assertTrue(state.cash.isFinite())
        assertTrue(state.runEarnings.isFinite())
    }

    @Test
    fun restoreEconomySanitizesNonFiniteValues() {
        val state = GameState()

        state.restoreEconomy(
            cash = Double.NaN,
            gems = -4,
            levels = emptyMap(),
            prestigeCrowns = -2,
            runEarnings = Double.POSITIVE_INFINITY
        )

        assertEquals(0.0, state.cash, 0.0)
        assertEquals(0, state.gems)
        assertEquals(0, state.prestigeCrowns)
        assertEquals(0.0, state.runEarnings, 0.0)
    }

    @Test
    fun gemRewardsSaturateInsteadOfOverflowing() {
        val state = GameState()

        state.addGems(Int.MAX_VALUE)
        state.addGems(Int.MAX_VALUE)

        assertEquals(Int.MAX_VALUE, state.gems)
    }

    @Test
    fun extremeUpgradeCostsBecomeUnavailableInsteadOfInfiniteQuotes() {
        val state = GameState()
        state.restoreEconomy(
            cash = Double.MAX_VALUE,
            gems = state.gems,
            levels = mapOf("factory" to 100_000)
        )
        val factoryIndex = state.businesses.indexOfFirst { it.id == "factory" }

        val quote = state.quoteUpgrade(factoryIndex, BuyMode.X1)

        assertEquals(0, quote.levels)
        assertEquals(0.0, quote.cost, 0.0)
        assertFalse(state.canUpgrade(factoryIndex, BuyMode.X1))
        assertTrue(state.cash.isFinite())
    }
}
