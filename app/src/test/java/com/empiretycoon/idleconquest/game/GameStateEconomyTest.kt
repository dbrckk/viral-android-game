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
    fun maxPurchaseWithInsufficientCashDoesNothing() {
        val state = GameState()
        val factoryIndex = state.businesses.indexOfFirst { it.id == "factory" }
        val beforeCash = state.cash
        val beforeLevel = state.businesses[factoryIndex].level

        val quote = state.quoteUpgrade(factoryIndex, BuyMode.MAX)
        assertEquals(0, quote.levels)
        assertEquals(0.0, quote.cost, 0.0)

        val result = state.upgrade(factoryIndex, BuyMode.MAX)
        assertFalse(result.upgraded)
        assertEquals(beforeCash, state.cash, 0.0)
        assertEquals(beforeLevel, state.businesses[factoryIndex].level)
    }

    @Test
    fun invalidBusinessIndexCannotSpendCash() {
        val state = GameState()
        val beforeCash = state.cash

        assertFalse(state.canUpgrade(-1, BuyMode.X1))
        assertFalse(state.canUpgrade(99, BuyMode.MAX))
        assertFalse(state.upgrade(-1, BuyMode.X1).upgraded)
        assertFalse(state.upgrade(99, BuyMode.MAX).upgraded)
        assertEquals(beforeCash, state.cash, 0.0)
    }

    @Test
    fun invalidCashGrantsAreIgnored() {
        val state = GameState()
        val beforeCash = state.cash
        val beforeRunEarnings = state.runEarnings

        state.addCash(Double.NaN, true)
        state.addCash(Double.POSITIVE_INFINITY, true)
        state.addCash(-100.0, true)
        state.addCash(0.0, true)

        assertEquals(beforeCash, state.cash, 0.0)
        assertEquals(beforeRunEarnings, state.runEarnings, 0.0)
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
