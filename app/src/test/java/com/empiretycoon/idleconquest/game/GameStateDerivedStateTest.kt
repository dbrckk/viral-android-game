package com.empiretycoon.idleconquest.game

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class GameStateDerivedStateTest {
    @Test
    fun managerForReturnsCatalogManagerWithCurrentHireState() {
        val state = GameState()
        val before = state.managerFor("street_stand")!!
        assertEquals("mia_flux", before.definition.id)
        assertFalse(before.hired)

        state.restoreEconomy(
            cash = state.cash,
            gems = state.gems,
            levels = emptyMap(),
            hiredManagers = setOf("mia_flux"),
        )

        assertTrue(state.managerFor("street_stand")!!.hired)
    }

    @Test
    fun permanentUpgradesForReturnsOnlyRequestedBusinessAndPurchaseState() {
        val state = GameState()
        state.restoreEconomy(
            cash = state.cash,
            gems = state.gems,
            levels = emptyMap(),
            permanentUpgrades = setOf("street_solar_grill"),
        )

        val upgrades = state.permanentUpgradesFor("street_stand")

        assertEquals(2, upgrades.size)
        assertTrue(upgrades.all { it.definition.businessId == "street_stand" })
        assertTrue(upgrades.first { it.definition.id == "street_solar_grill" }.purchased)
        assertFalse(upgrades.first { it.definition.id == "street_bulk_supply" }.purchased)
    }

    @Test
    fun optimizedMultipliersMatchPurchasedCatalogEffects() {
        val state = GameState()
        state.restoreEconomy(
            cash = state.cash,
            gems = state.gems,
            levels = emptyMap(),
            hiredManagers = setOf("mia_flux"),
            permanentUpgrades = setOf("street_solar_grill", "street_bulk_supply"),
        )

        assertEquals(1.5, state.managerMultiplier("street_stand"), 0.0)
        assertEquals(2.0, state.permanentIncomeMultiplier("street_stand"), 0.0)
        assertEquals(0.90, state.permanentCostMultiplier("street_stand"), 0.0)
        assertEquals(1.0, state.managerMultiplier("missing"), 0.0)
        assertEquals(1.0, state.permanentIncomeMultiplier("missing"), 0.0)
        assertEquals(1.0, state.permanentCostMultiplier("missing"), 0.0)
    }
}
