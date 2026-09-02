package com.empiretycoon.idleconquest.ui

import com.empiretycoon.idleconquest.game.BuyMode

sealed interface UiTouchTarget {
    data object Prestige : UiTouchTarget
    data class Mission(val id: String) : UiTouchTarget
    data class BuyModeTarget(val mode: BuyMode) : UiTouchTarget
    data class PermanentUpgrade(val id: String) : UiTouchTarget
    data class Manager(val id: String) : UiTouchTarget
    data class BusinessUpgrade(val index: Int) : UiTouchTarget
}

data class UiTouchMap(
    val prestige: UiBounds,
    val missions: List<Pair<String, UiBounds>> = emptyList(),
    val buyModes: List<Pair<BuyMode, UiBounds>> = emptyList(),
    val permanentUpgrades: List<Pair<String, UiBounds>> = emptyList(),
    val managers: List<Pair<String, UiBounds>> = emptyList(),
    val businessUpgrades: List<UiBounds> = emptyList(),
)

object UiTouchTargetResolver {
    fun resolve(x: Float, y: Float, map: UiTouchMap): UiTouchTarget? {
        if (map.prestige.contains(x, y)) return UiTouchTarget.Prestige

        map.missions.firstOrNull { (_, bounds) -> bounds.contains(x, y) }
            ?.let { (id, _) -> return UiTouchTarget.Mission(id) }

        map.buyModes.firstOrNull { (_, bounds) -> bounds.contains(x, y) }
            ?.let { (mode, _) -> return UiTouchTarget.BuyModeTarget(mode) }

        map.permanentUpgrades.firstOrNull { (_, bounds) -> bounds.contains(x, y) }
            ?.let { (id, _) -> return UiTouchTarget.PermanentUpgrade(id) }

        map.managers.firstOrNull { (_, bounds) -> bounds.contains(x, y) }
            ?.let { (id, _) -> return UiTouchTarget.Manager(id) }

        val businessIndex = map.businessUpgrades.indexOfFirst { it.contains(x, y) }
        if (businessIndex >= 0) return UiTouchTarget.BusinessUpgrade(businessIndex)

        return null
    }

    private fun UiBounds.contains(x: Float, y: Float): Boolean =
        x >= minOf(left, right) &&
            x <= maxOf(left, right) &&
            y >= minOf(top, bottom) &&
            y <= maxOf(top, bottom)
}
