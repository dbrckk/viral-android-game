package com.empiretycoon.idleconquest.game

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class GameStateEconomyTest {

    @Test
    fun maxPurchaseNeverOverspendsAndBuysAffordableLevels() {
        val state = GameState()
        state.addCash(100_000.0)
        val beforeCash = state.cash
        val beforeLevel = state.businesses[0].level
        val quote = state.quoteUpgrade(0, BuyMode.MAX)

        assertTrue(quote.levels > 0)
        assertTrue(quote.cost <= beforeCash)

        val result = state.upgrade(0, BuyMode.MAX)
        assertTrue(result.upgraded)
        assertEquals(quote.levels, result.levelsBought)
        assertEquals(beforeLevel + quote.levels, state.businesses[0].level)
        assertTrue(state.cash >= 0.0)
    }

    @Test
    fun managerRequiresUnlockThenAppliesIncomeMultiplier() {
        val state = GameState()
        val manager = ManagerCatalog.all.first { it.id == "mia_flux" }

        state.addCash(1_000_000.0)
        assertFalse(state.canHire(manager.id))

        val upgrade = state.upgrade(0, BuyMode.X25)
        assertTrue(upgrade.upgraded)
        assertTrue(state.businesses[0].level >= manager.unlockLevel)

        val incomeBefore = state.incomeFor(state.businesses[0])
        val hire = state.hire(manager.id)
        assertTrue(hire.hired)
        assertTrue(manager.id in state.hiredManagers())

        val incomeAfter = state.incomeFor(state.businesses[0])
        assertEquals(incomeBefore * manager.incomeMultiplier, incomeAfter, 1e-9)
    }
}
