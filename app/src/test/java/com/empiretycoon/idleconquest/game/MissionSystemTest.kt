package com.empiretycoon.idleconquest.game

import org.junit.Assert.*
import org.junit.Test

class MissionSystemTest {
    @Test fun initialMissionProgressIsComputed() {
        val state = GameState()
        val first = state.missions.first { it.definition.id == "first_25_levels" }
        assertEquals(4.0, first.progress, 0.001)
        assertFalse(first.completed)
    }

    @Test fun completedMissionCanBeClaimedOnce() {
        val state = GameState()
        state.restoreEconomy(1000.0, 0, mapOf(
            "street_stand" to 25,
            "corner_shop" to 1,
            "workshop" to 1,
            "factory" to 1
        ))
        val before = state.cash
        val result = state.claimMission("first_25_levels")
        assertTrue(result.claimed)
        assertTrue(state.cash > before)
        assertFalse(state.claimMission("first_25_levels").claimed)
    }

    @Test fun catalogHasTwelveUniqueMissions() {
        assertEquals(12, MissionCatalog.all.size)
        assertEquals(12, MissionCatalog.all.map { it.id }.toSet().size)
    }

    @Test fun expandedMissionMetricsReachExpectedTargets() {
        val state = GameState()
        state.restoreEconomy(
            cash = 1_000_000_000.0,
            gems = 0,
            levels = mapOf(
                "street_stand" to 100,
                "corner_shop" to 250,
                "workshop" to 250,
                "factory" to 500,
            ),
            hiredManagers = ManagerCatalog.all.map { it.id }.toSet(),
            permanentUpgrades = PermanentUpgradeCatalog.all.map { it.id }.toSet(),
        )

        assertTrue(state.missions.first { it.definition.id == "corner_lv250" }.completed)
        assertTrue(state.missions.first { it.definition.id == "workshop_lv250" }.completed)
        assertTrue(state.missions.first { it.definition.id == "hire_four" }.completed)
        assertTrue(state.missions.first { it.definition.id == "buy_eight_upgrades" }.completed)
        assertTrue(state.missions.first { it.definition.id == "factory_lv500" }.completed)
    }
}
