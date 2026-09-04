package com.empiretycoon.idleconquest.ui

import com.empiretycoon.idleconquest.game.BuyMode
import com.empiretycoon.idleconquest.game.GameState
import com.empiretycoon.idleconquest.game.ManagerCatalog
import com.empiretycoon.idleconquest.game.MissionCatalog
import com.empiretycoon.idleconquest.game.PermanentUpgradeCatalog

data class UiAccessibilityNode(
    val virtualId: Int,
    val target: UiTouchTarget,
    val bounds: UiBounds,
    val label: String,
)

object UiAccessibilityModel {
    private const val PRESTIGE_ID = 1
    private const val MISSION_BASE_ID = 100
    private const val BUY_MODE_BASE_ID = 200
    private const val PERMANENT_BASE_ID = 300
    private const val MANAGER_BASE_ID = 400
    private const val BUSINESS_BASE_ID = 500

    fun nodes(
        map: UiTouchMap,
        gameState: GameState,
        buyMode: BuyMode,
    ): List<UiAccessibilityNode> = buildList {
        if (map.prestige.hasArea()) {
            val quote = gameState.prestigeQuote()
            val label = when {
                gameState.prestigeCrowns == Int.MAX_VALUE -> "Prestige crown cap reached"
                quote.available -> "Prestige ready. Earn ${quote.crownReward} crown${if (quote.crownReward == 1) "" else "s"}. Current multiplier ${UiNumberFormatter.multiplier(gameState.prestigeMultiplier)} times"
                else -> "Prestige. Run earnings ${UiNumberFormatter.compact(gameState.runEarnings)} of ${UiNumberFormatter.compact(quote.requirement)} required"
            }
            add(UiAccessibilityNode(PRESTIGE_ID, UiTouchTarget.Prestige, map.prestige, label))
        }

        map.missions.forEach { (id, bounds) ->
            val index = MissionCatalog.all.indexOfFirst { it.id == id }
            val mission = gameState.missions.firstOrNull { it.definition.id == id }
            if (index >= 0 && mission != null && bounds.hasArea()) {
                val status = when {
                    mission.claimed -> "claimed"
                    mission.completed -> "ready to claim"
                    else -> "progress ${UiNumberFormatter.compact(mission.progress)} of ${UiNumberFormatter.compact(mission.definition.target)}"
                }
                add(
                    UiAccessibilityNode(
                        MISSION_BASE_ID + index,
                        UiTouchTarget.Mission(id),
                        bounds,
                        "Mission ${mission.definition.title}. $status",
                    )
                )
            }
        }

        map.buyModes.forEach { (mode, bounds) ->
            if (bounds.hasArea()) {
                val selected = if (mode == buyMode) "selected" else "not selected"
                add(
                    UiAccessibilityNode(
                        BUY_MODE_BASE_ID + mode.ordinal,
                        UiTouchTarget.BuyModeTarget(mode),
                        bounds,
                        "Buy mode ${mode.accessibilityName()}. $selected",
                    )
                )
            }
        }

        map.permanentUpgrades.forEach { (id, bounds) ->
            val index = PermanentUpgradeCatalog.all.indexOfFirst { it.id == id }
            val state = gameState.permanentUpgrades.firstOrNull { it.definition.id == id }
            if (index >= 0 && state != null && bounds.hasArea()) {
                val businessLevel = gameState.businesses.firstOrNull { it.id == state.definition.businessId }?.level ?: 0
                val status = when {
                    state.purchased -> "purchased"
                    businessLevel < state.definition.unlockLevel -> "locked until level ${state.definition.unlockLevel}"
                    gameState.cash < state.definition.cost -> "cost ${UiNumberFormatter.compact(state.definition.cost)} cash"
                    else -> "available"
                }
                add(
                    UiAccessibilityNode(
                        PERMANENT_BASE_ID + index,
                        UiTouchTarget.PermanentUpgrade(id),
                        bounds,
                        "Permanent upgrade ${state.definition.name}. $status",
                    )
                )
            }
        }

        map.managers.forEach { (id, bounds) ->
            val index = ManagerCatalog.all.indexOfFirst { it.id == id }
            val state = gameState.managers.firstOrNull { it.definition.id == id }
            if (index >= 0 && state != null && bounds.hasArea()) {
                val businessLevel = gameState.businesses.firstOrNull { it.id == state.definition.businessId }?.level ?: 0
                val status = when {
                    state.hired -> "hired"
                    businessLevel < state.definition.unlockLevel -> "locked until level ${state.definition.unlockLevel}"
                    gameState.cash < state.definition.cost -> "hire cost ${UiNumberFormatter.compact(state.definition.cost)} cash"
                    else -> "available to hire"
                }
                add(
                    UiAccessibilityNode(
                        MANAGER_BASE_ID + index,
                        UiTouchTarget.Manager(id),
                        bounds,
                        "Manager ${state.definition.name}. $status",
                    )
                )
            }
        }

        map.businessUpgrades.forEachIndexed { index, bounds ->
            val business = gameState.businesses.getOrNull(index)
            if (business != null && bounds.hasArea()) {
                val quote = gameState.quoteUpgrade(index, buyMode)
                val status = when {
                    quote.levels <= 0 -> "upgrade unavailable"
                    quote.cost <= gameState.cash -> "buy ${quote.levels} level${if (quote.levels == 1) "" else "s"} for ${UiNumberFormatter.compact(quote.cost)} cash"
                    else -> "needs ${UiNumberFormatter.compact(quote.cost)} cash"
                }
                add(
                    UiAccessibilityNode(
                        BUSINESS_BASE_ID + index,
                        UiTouchTarget.BusinessUpgrade(index),
                        bounds,
                        "Upgrade ${business.displayName}, level ${business.level}. $status",
                    )
                )
            }
        }
    }

    private fun UiBounds.hasArea(): Boolean = left < right && top < bottom

    private fun BuyMode.accessibilityName(): String = when (this) {
        BuyMode.X1 -> "one"
        BuyMode.X10 -> "ten"
        BuyMode.X25 -> "twenty five"
        BuyMode.MAX -> "maximum"
    }
}
