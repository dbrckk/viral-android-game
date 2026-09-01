package com.empiretycoon.idleconquest.game

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class GameSaveRestorerTest {
    @Test
    fun rejectsUnsupportedSchemas() {
        assertNull(GameSaveRestorer.restore(snapshot(schemaVersion = 0), 0L))
        assertNull(GameSaveRestorer.restore(snapshot(schemaVersion = GameSaveRestorer.CURRENT_SCHEMA_VERSION + 1), 0L))
    }

    @Test
    fun restoresEconomyAndKnownCatalogIdsWhileFilteringUnknownIds() {
        val result = GameSaveRestorer.restore(
            snapshot(
                cash = 9_000.0,
                gems = 42,
                levels = mapOf("street_stand" to 25, "unknown" to 999),
                hiredManagers = setOf("mia_flux", "unknown_manager"),
                permanentUpgrades = setOf("street_solar_grill", "unknown_upgrade"),
                claimedMissions = setOf("first_25_levels", "unknown_mission"),
                prestigeCrowns = 3,
                runEarnings = 123.0,
            ),
            nowEpochMillis = 0L,
        )!!

        assertEquals(9_000.0, result.state.cash, 0.0)
        assertEquals(42, result.state.gems)
        assertEquals(25, result.state.businesses.first { it.id == "street_stand" }.level)
        assertTrue(result.state.businesses.none { it.id == "unknown" })
        assertEquals(3, result.state.prestigeCrowns)
        assertEquals(123.0, result.state.runEarnings, 0.0)
        assertTrue("mia_flux" in result.state.hiredManagers())
        assertFalse("unknown_manager" in result.state.hiredManagers())
        assertTrue("street_solar_grill" in result.state.purchasedPermanentUpgrades())
        assertFalse("unknown_upgrade" in result.state.purchasedPermanentUpgrades())
        assertTrue("first_25_levels" in result.state.claimedMissions())
        assertFalse("unknown_mission" in result.state.claimedMissions())
    }

    @Test
    fun invalidNumericValuesFallBackSafely() {
        val result = GameSaveRestorer.restore(
            snapshot(cash = Double.NaN, gems = -10, prestigeCrowns = -4, runEarnings = Double.POSITIVE_INFINITY),
            nowEpochMillis = 0L,
        )!!

        assertEquals(2_500.0, result.state.cash, 0.0)
        assertEquals(0, result.state.gems)
        assertEquals(0, result.state.prestigeCrowns)
        assertEquals(0.0, result.state.runEarnings, 0.0)
    }

    @Test
    fun invalidBusinessLevelsAreClampedToPlayableMinimum() {
        val result = GameSaveRestorer.restore(
            snapshot(levels = mapOf("street_stand" to Int.MIN_VALUE, "corner_shop" to 0)),
            nowEpochMillis = 0L,
        )!!

        assertEquals(1, result.state.businesses.first { it.id == "street_stand" }.level)
        assertEquals(1, result.state.businesses.first { it.id == "corner_shop" }.level)
    }

    @Test
    fun appliesOfflineProgressAndCountsItForPrestige() {
        val result = GameSaveRestorer.restore(
            snapshot(savedAtEpochMillis = 0L),
            nowEpochMillis = 10_000L,
        )!!

        assertEquals(10L, result.offlineSeconds)
        assertEquals(result.offlineEarnings, result.state.runEarnings, 0.0001)
        assertTrue(result.offlineEarnings > 0.0)
    }

    private fun snapshot(
        schemaVersion: Int = GameSaveRestorer.CURRENT_SCHEMA_VERSION,
        cash: Double = 2_500.0,
        gems: Int = 12,
        levels: Map<String, Int> = emptyMap(),
        hiredManagers: Set<String> = emptySet(),
        permanentUpgrades: Set<String> = emptySet(),
        claimedMissions: Set<String> = emptySet(),
        prestigeCrowns: Int = 0,
        runEarnings: Double = 0.0,
        savedAtEpochMillis: Long = 0L,
    ) = GameSaveSnapshot(
        schemaVersion = schemaVersion,
        cash = cash,
        gems = gems,
        levels = levels,
        hiredManagers = hiredManagers,
        permanentUpgrades = permanentUpgrades,
        claimedMissions = claimedMissions,
        prestigeCrowns = prestigeCrowns,
        runEarnings = runEarnings,
        savedAtEpochMillis = savedAtEpochMillis,
    )
}
