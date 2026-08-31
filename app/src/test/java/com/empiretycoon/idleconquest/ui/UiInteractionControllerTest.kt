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
        assertTrue(result.message!!.contains("Lv.25"))
    }

    @Test
    fun lockedManagerDoesNotSaveOrShowSuccess() {
        val state = GameState()
        val result = UiInteractionController(state).manager("mia_flux")

        assertFalse(result.saveRequired)
        assertNull(result.message)
    }
}
