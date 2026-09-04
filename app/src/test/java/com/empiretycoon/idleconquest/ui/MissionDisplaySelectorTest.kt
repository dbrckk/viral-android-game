package com.empiretycoon.idleconquest.ui

import com.empiretycoon.idleconquest.game.GameState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class MissionDisplaySelectorTest {
    @Test
    fun defaultWindowKeepsAtMostSixMissions() {
        val missions = GameState().missions

        val visible = MissionDisplaySelector.visible(missions)

        assertTrue(visible.size <= MissionDisplaySelector.DEFAULT_MAX_VISIBLE)
        assertEquals(missions.take(visible.size).map { it.definition.id }, visible.map { it.definition.id })
    }

    @Test
    fun claimedMissionMakesRoomForNextUnclaimedMission() {
        val state = GameState()
        state.restoreEconomy(
            cash = 2_500.0,
            gems = 12,
            levels = mapOf("street_stand" to 22),
        )
        state.claimMission("first_25_levels")

        val visible = MissionDisplaySelector.visible(state.missions, maxVisible = 3)

        assertEquals(3, visible.size)
        assertTrue(visible.none { it.definition.id == "first_25_levels" })
        assertTrue(visible.all { !it.claimed })
    }

    @Test
    fun claimedMissionsFillUnusedSlotsWhenFewUnclaimedRemain() {
        val state = GameState()
        val missions = state.missions.mapIndexed { index, mission ->
            mission.copy(claimed = index < state.missions.size - 1)
        }

        val visible = MissionDisplaySelector.visible(missions, maxVisible = 4)

        assertEquals(4, visible.size)
        assertEquals(missions.last().definition.id, visible.first().definition.id)
        assertTrue(visible.drop(1).all { it.claimed })
    }

    @Test
    fun zeroWindowReturnsNothing() {
        assertTrue(MissionDisplaySelector.visible(GameState().missions, 0).isEmpty())
    }
}
