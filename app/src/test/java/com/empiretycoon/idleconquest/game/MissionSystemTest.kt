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

    @Test fun catalogHasSixUniqueMissions() {
        assertEquals(6, MissionCatalog.all.size)
        assertEquals(6, MissionCatalog.all.map { it.id }.toSet().size)
    }
}
