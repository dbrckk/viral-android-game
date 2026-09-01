package com.empiretycoon.idleconquest.ui

import com.empiretycoon.idleconquest.game.BuyMode
import com.empiretycoon.idleconquest.game.GameState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class UiInteractionControllerTest {
    @Test
    fun unavailablePrestigeReturnsProgressMessageWithoutSaving() {
        val state = GameState()
        val result = UiInteractionController(state).prestige()

        assertFalse(result.saveRequired)
        assertFalse(result.clearHighlight)
        assertEquals(UiInteractionController.DURATION_PROGRESS, result.durationNanos)
        assertTrue(result.message!!.startsWith("PRESTIGE AT "))
    }

    @Test
    fun availablePrestigeResetsRunAndRequestsSave() {
        val state = GameState()
        state.restoreEconomy(
            cash = 50_000.0,
            gems = 12,
            levels = mapOf("street_stand" to 100),
            runEarnings = 1_000_000.0,
        )

        val result = UiInteractionController(state).prestige()

        assertTrue(result.saveRequired)
        assertTrue(result.clearHighlight)
        assertEquals(UiInteractionController.DURATION_PRESTIGE, result.durationNanos)
        assertEquals(1, state.prestigeCrowns)
        assertEquals(1, state.businesses.first().level)
        assertEquals(0.0, state.runEarnings, 0.0)
    }

    @Test
    fun normalBusinessUpgradeRequestsSaveWithoutHighlight() {
        val state = GameState()
        val result = UiInteractionController(state).businessUpgrade(0, BuyMode.X1)

        assertTrue(result.saveRequired)
        assertNull(result.highlightBusinessId)
        assertEquals(2, state.businesses[0].level)
    }

    @Test
    fun blockedBusinessUpgradeExplainsMissingCash() {
        val state = GameState()
        state.restoreEconomy(cash = 0.0, gems = 12, levels = emptyMap())

        val result = UiInteractionController(state).businessUpgrade(0, BuyMode.X1)

        assertFalse(result.saveRequired)
        assertEquals(UiInteractionController.DURATION_INFO, result.durationNanos)
        assertTrue(result.message!!.contains("NEED $"))
        assertTrue(result.message!!.contains("NOW $0"))
        assertEquals(1, state.businesses[0].level)
    }

    @Test
    fun milestoneUpgradeReturnsHighlightAndMessage() {
        val state = GameState()
        state.restoreEconomy(
            cash = 1_000_000.0,
            gems = 12,
            levels = mapOf("street_stand" to 24),
        )

        val result = UiInteractionController(state).businessUpgrade(0, BuyMode.X1)

        assertTrue(result.saveRequired)
        assertEquals("street_stand", result.highlightBusinessId)
        assertEquals(UiInteractionController.DURATION_SUCCESS, result.durationNanos)
        assertTrue(result.message!!.contains("Lv.25"))
    }

    @Test
    fun lockedManagerExplainsUnlockRequirement() {
        val state = GameState()
        val result = UiInteractionController(state).manager("mia_flux")

        assertFalse(result.saveRequired)
        assertEquals(UiInteractionController.DURATION_INFO, result.durationNanos)
        assertEquals("Mia Flux • UNLOCK Lv.15", result.message)
    }

    @Test
    fun unlockedManagerWithoutCashExplainsCost() {
        val state = GameState()
        state.restoreEconomy(
            cash = 100.0,
            gems = 12,
            levels = mapOf("street_stand" to 15),
        )

        val result = UiInteractionController(state).manager("mia_flux")

        assertFalse(result.saveRequired)
        assertTrue(result.message!!.contains("NEED $750"))
        assertTrue(result.message!!.contains("NOW $100"))
    }

    @Test
    fun availableManagerHireRequestsSaveAndMutatesState() {
        val state = GameState()
        state.restoreEconomy(
            cash = 10_000.0,
            gems = 12,
            levels = mapOf("street_stand" to 25),
        )

        val result = UiInteractionController(state).manager("mia_flux")

        assertTrue(result.saveRequired)
        assertTrue(result.message!!.startsWith("MANAGER HIRED!"))
        assertTrue(state.managerFor("street_stand")!!.hired)
    }

    @Test
    fun alreadyHiredManagerExplainsStateWithoutSaving() {
        val state = GameState()
        state.restoreEconomy(
            cash = 10_000.0,
            gems = 12,
            levels = mapOf("street_stand" to 25),
            hiredManagers = setOf("mia_flux"),
        )

        val result = UiInteractionController(state).manager("mia_flux")

        assertFalse(result.saveRequired)
        assertEquals("Mia Flux ALREADY HIRED", result.message)
    }

    @Test
    fun availablePermanentUpgradeRequestsSaveAndMutatesState() {
        val state = GameState()
        state.restoreEconomy(
            cash = 10_000.0,
            gems = 12,
            levels = mapOf("street_stand" to 25),
        )

        val result = UiInteractionController(state).permanentUpgrade("street_solar_grill")

        assertTrue(result.saveRequired)
        assertTrue(result.message!!.startsWith("PERMANENT UPGRADE!"))
        assertTrue("street_solar_grill" in state.purchasedPermanentUpgrades())
    }

    @Test
    fun completedMissionClaimRequestsSaveAndAppliesReward() {
        val state = GameState()
        state.restoreEconomy(
            cash = 2_500.0,
            gems = 12,
            levels = mapOf("street_stand" to 22),
        )

        val result = UiInteractionController(state).mission("first_25_levels")

        assertTrue(result.saveRequired)
        assertTrue(result.message!!.startsWith("MISSION COMPLETE!"))
        assertEquals(5_000.0, state.cash, 0.0)
        assertTrue("first_25_levels" in state.claimedMissions())
    }

    @Test
    fun invalidTargetsAreNoOps() {
        val state = GameState()
        val controller = UiInteractionController(state)
        val cashBefore = state.cash

        val results = listOf(
            controller.manager("missing_manager"),
            controller.permanentUpgrade("missing_upgrade"),
            controller.mission("missing_mission"),
            controller.businessUpgrade(-1, BuyMode.X1),
            controller.businessUpgrade(Int.MAX_VALUE, BuyMode.MAX),
        )

        results.forEach { result ->
            assertFalse(result.saveRequired)
            assertFalse(result.clearHighlight)
            assertNull(result.highlightBusinessId)
            assertNull(result.message)
            assertEquals(0L, result.durationNanos)
        }
        assertEquals(cashBefore, state.cash, 0.0)
    }
}
