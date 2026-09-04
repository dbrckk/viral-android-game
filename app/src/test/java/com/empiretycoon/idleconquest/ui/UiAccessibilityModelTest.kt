package com.empiretycoon.idleconquest.ui

import com.empiretycoon.idleconquest.game.BuyMode
import com.empiretycoon.idleconquest.game.GameState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class UiAccessibilityModelTest {
    private val box = UiBounds(0f, 0f, 20f, 20f)

    @Test
    fun exposesOnlyVisibleTouchTargetsWithUniqueVirtualIds() {
        val map = UiTouchMap(
            prestige = box,
            missions = listOf("first_25_levels" to box),
            buyModes = BuyMode.entries.map { it to box },
            managers = listOf("mia_flux" to box),
            permanentUpgrades = listOf("street_solar_grill" to box),
            businessUpgrades = listOf(box),
        )

        val nodes = UiAccessibilityModel.nodes(map, GameState(), BuyMode.X1)

        assertEquals(9, nodes.size)
        assertEquals(nodes.size, nodes.map { it.virtualId }.toSet().size)
        assertTrue(nodes.any { it.target == UiTouchTarget.Prestige })
        assertTrue(nodes.any { it.target == UiTouchTarget.Mission("first_25_levels") })
        assertTrue(nodes.any { it.target == UiTouchTarget.BusinessUpgrade(0) })
    }

    @Test
    fun emptyBoundsAreNotExposed() {
        val empty = UiBounds(0f, 0f, 0f, 20f)
        val map = UiTouchMap(prestige = empty, businessUpgrades = listOf(empty))

        assertTrue(UiAccessibilityModel.nodes(map, GameState(), BuyMode.X1).isEmpty())
    }

    @Test
    fun labelsReflectSelectionAndMissionCompletion() {
        val state = GameState()
        state.restoreEconomy(
            cash = 100_000.0,
            gems = 12,
            levels = mapOf("street_stand" to 25),
        )
        val map = UiTouchMap(
            prestige = UiBounds(0f, 0f, 0f, 0f),
            missions = listOf("first_25_levels" to box),
            buyModes = listOf(BuyMode.X10 to box),
        )

        val nodes = UiAccessibilityModel.nodes(map, state, BuyMode.X10)

        assertTrue(nodes.first { it.target is UiTouchTarget.Mission }.label.contains("ready to claim"))
        assertTrue(nodes.first { it.target is UiTouchTarget.BuyModeTarget }.label.contains("selected"))
    }

    @Test
    fun businessUpgradeLabelIncludesCurrentLevelAndQuote() {
        val state = GameState()
        val map = UiTouchMap(
            prestige = UiBounds(0f, 0f, 0f, 0f),
            businessUpgrades = listOf(box),
        )

        val label = UiAccessibilityModel.nodes(map, state, BuyMode.X1).single().label

        assertTrue(label.contains("Street Stand"))
        assertTrue(label.contains("level 1"))
        assertTrue(label.contains("buy 1 level"))
    }
}
