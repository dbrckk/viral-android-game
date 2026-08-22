package com.empiretycoon.idleconquest.game

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class PermanentUpgradeSystemTest {
    @Test fun incomeUpgradeMultipliesBusinessIncome() {
        val state = GameState()
        state.restoreEconomy(
            cash = 1_000_000.0,
            gems = 0,
            levels = mapOf("street_stand" to 25),
            permanentUpgrades = setOf("street_solar_grill")
        )
        val business = state.businesses.first { it.id == "street_stand" }
        assertEquals(2.0, state.permanentIncomeMultiplier("street_stand"), 0.0001)
        assertEquals(business.rawIncomePerSecond * 2.0, state.incomeFor(business), 0.0001)
    }

    @Test fun costUpgradeReducesBulkQuote() {
        val baseline = GameState().apply { restoreEconomy(1_000_000.0, 0, mapOf("street_stand" to 75)) }
        val upgraded = GameState().apply { restoreEconomy(1_000_000.0, 0, mapOf("street_stand" to 75), permanentUpgrades = setOf("street_bulk_supply")) }
        val baseCost = baseline.quoteUpgrade(0, BuyMode.X10).cost
        val reducedCost = upgraded.quoteUpgrade(0, BuyMode.X10).cost
        assertEquals(baseCost * 0.90, reducedCost, 0.01)
        assertTrue(reducedCost < baseCost)
    }

    @Test fun catalogContainsTwoUpgradesPerBusiness() {
        val grouped = PermanentUpgradeCatalog.all.groupBy { it.businessId }
        listOf("street_stand","corner_shop","workshop","factory").forEach { id ->
            assertEquals(2, grouped[id]?.size)
        }
    }
}
