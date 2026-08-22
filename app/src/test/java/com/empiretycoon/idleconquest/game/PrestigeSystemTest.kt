package com.empiretycoon.idleconquest.game

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class PrestigeSystemTest {
    @Test fun rewardCurveIsStable() {
        assertEquals(0, PrestigeRules.crownsFor(999_999.0))
        assertEquals(1, PrestigeRules.crownsFor(1_000_000.0))
        assertEquals(2, PrestigeRules.crownsFor(4_000_000.0))
        assertEquals(3, PrestigeRules.crownsFor(9_000_000.0))
    }

    @Test fun crownMultiplierScalesLinearly() {
        assertEquals(1.0, PrestigeRules.multiplier(0), 0.0001)
        assertEquals(1.2, PrestigeRules.multiplier(1), 0.0001)
        assertEquals(3.0, PrestigeRules.multiplier(10), 0.0001)
    }

    @Test fun prestigeResetsRunButKeepsPermanentProgress() {
        val state=GameState()
        state.restoreEconomy(
            cash=10_000_000.0,
            gems=42,
            levels=mapOf("street_stand" to 100,"corner_shop" to 100,"workshop" to 100,"factory" to 100),
            permanentUpgrades=setOf("street_solar_grill"),
            prestigeCrowns=2,
            runEarnings=4_000_000.0
        )
        assertTrue(state.prestigeQuote().available)
        val result=state.prestige()
        assertTrue(result.prestiged)
        assertEquals(4,state.prestigeCrowns)
        assertEquals(42,state.gems)
        assertEquals(1,state.businesses.first().level)
        assertTrue("street_solar_grill" in state.purchasedPermanentUpgrades())
        assertEquals(0.0,state.runEarnings,0.0001)
        assertFalse(state.prestigeQuote().available)
    }
}
