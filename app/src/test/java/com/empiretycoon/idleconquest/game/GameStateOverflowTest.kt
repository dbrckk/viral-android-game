package com.empiretycoon.idleconquest.game

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class GameStateOverflowTest {
    @Test
    fun gemsSaturateAtIntMaxValue() {
        val state = GameState()
        state.restoreEconomy(
            cash = state.cash,
            gems = Int.MAX_VALUE - 1,
            levels = emptyMap(),
        )

        state.addGems(100)
        assertEquals(Int.MAX_VALUE, state.gems)
    }

    @Test
    fun prestigeCrownsSaturateInsteadOfOverflowing() {
        val state = GameState()
        state.restoreEconomy(
            cash = state.cash,
            gems = state.gems,
            levels = emptyMap(),
            prestigeCrowns = Int.MAX_VALUE - 1,
            runEarnings = PrestigeRules.BASE_REQUIREMENT * 100.0,
        )

        val quote = state.prestigeQuote()
        assertTrue(quote.available)
        assertEquals(1, quote.crownReward)
        assertTrue(quote.nextMultiplier > 1.0)

        val result = state.prestige()
        assertTrue(result.prestiged)
        assertEquals(1, result.crownsEarned)
        assertEquals(Int.MAX_VALUE, state.prestigeCrowns)
        assertEquals(Int.MAX_VALUE, result.totalCrowns)
    }

    @Test
    fun prestigeAtCrownCapIsUnavailableAndDoesNotResetRun() {
        val state = GameState()
        val first = state.businesses.first()
        val cashBefore = 9_999_999.0
        val runEarnings = PrestigeRules.BASE_REQUIREMENT * 100.0
        state.restoreEconomy(
            cash = cashBefore,
            gems = state.gems,
            levels = mapOf(first.id to 100),
            prestigeCrowns = Int.MAX_VALUE,
            runEarnings = runEarnings,
        )

        val quote = state.prestigeQuote()
        assertFalse(quote.available)
        assertEquals(0, quote.crownReward)

        val result = state.prestige()
        assertFalse(result.prestiged)
        assertEquals(Int.MAX_VALUE, state.prestigeCrowns)
        assertEquals(100, state.businesses.first().level)
        assertEquals(cashBefore, state.cash, 0.0)
        assertEquals(runEarnings, state.runEarnings, 0.0)
    }

    @Test
    fun totalLevelMissionProgressDoesNotOverflowInt() {
        val state = GameState()
        val levels = state.businesses.associate { it.id to Int.MAX_VALUE }
        state.restoreEconomy(
            cash = state.cash,
            gems = state.gems,
            levels = levels,
        )
        val mission = MissionDefinition(
            id = "overflow_probe",
            title = "Overflow Probe",
            description = "test",
            metric = MissionMetric.TOTAL_LEVELS,
            target = 1.0,
            reward = MissionReward(MissionRewardType.CASH, 1.0),
            icon = "test",
        )

        val progress = state.missionProgress(mission)
        assertEquals(Int.MAX_VALUE.toLong() * state.businesses.size.toLong(), progress.toLong())
    }

    @Test
    fun businessAtIntMaxCannotQuoteAnotherUpgrade() {
        val state = GameState()
        val first = state.businesses.first()
        state.restoreEconomy(
            cash = Double.MAX_VALUE,
            gems = state.gems,
            levels = mapOf(first.id to Int.MAX_VALUE),
        )

        assertFalse(state.canUpgrade(0, BuyMode.X1))
        assertEquals(0, state.quoteUpgrade(0, BuyMode.MAX).levels)
    }

    @Test
    fun overflowingBusinessIncomeSaturatesInsteadOfDroppingToZero() {
        val state = GameState()
        val overflowing = BusinessState(
            id = "overflow_business",
            displayName = "Overflow Business",
            level = Int.MAX_VALUE,
            baseCost = 1.0,
            baseIncomePerSecond = Double.MAX_VALUE,
        )

        assertEquals(Double.MAX_VALUE, state.incomeFor(overflowing), 0.0)
    }

    @Test
    fun overflowingTickSaturatesCashAndRunEarnings() {
        val state = GameState()

        state.tick(Double.MAX_VALUE)

        assertEquals(Double.MAX_VALUE, state.cash, 0.0)
        assertEquals(Double.MAX_VALUE, state.runEarnings, 0.0)
    }
}
