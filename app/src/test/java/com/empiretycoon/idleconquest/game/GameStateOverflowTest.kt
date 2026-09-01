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
        assertTrue(quote.nextMultiplier > 1.0)

        val result = state.prestige()
        assertTrue(result.prestiged)
        assertEquals(Int.MAX_VALUE, state.prestigeCrowns)
        assertEquals(Int.MAX_VALUE, result.totalCrowns)
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
}
