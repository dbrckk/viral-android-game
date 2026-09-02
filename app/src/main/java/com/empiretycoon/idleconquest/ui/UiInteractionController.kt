package com.empiretycoon.idleconquest.ui

import com.empiretycoon.idleconquest.game.BuyMode
import com.empiretycoon.idleconquest.game.GameState
import com.empiretycoon.idleconquest.game.MissionRewardType
import com.empiretycoon.idleconquest.game.PermanentUpgradeCatalog

data class UiInteractionResult(
    val message: String? = null,
    val durationNanos: Long = 0L,
    val saveRequired: Boolean = false,
    val highlightBusinessId: String? = null,
    val clearHighlight: Boolean = false,
)

class UiInteractionController(private val gameState: GameState) {
    fun prestige(): UiInteractionResult {
        val quote = gameState.prestigeQuote()
        if (!quote.available) {
            if (quote.currentCrowns == Int.MAX_VALUE) {
                return UiInteractionResult(
                    message = "PRESTIGE CROWN CAP REACHED",
                    durationNanos = DURATION_INFO,
                )
            }
            return UiInteractionResult(
                message = "PRESTIGE AT $${UiNumberFormatter.compact(quote.requirement)} RUN EARNINGS • NOW ${UiNumberFormatter.compact(gameState.runEarnings)}",
                durationNanos = DURATION_PROGRESS,
            )
        }

        val result = gameState.prestige()
        if (!result.prestiged) return UiInteractionResult()
        return UiInteractionResult(
            message = "PRESTIGE! +${result.crownsEarned} CROWN • EMPIRE ×${UiNumberFormatter.multiplier(result.multiplier)}",
            durationNanos = DURATION_PRESTIGE,
            saveRequired = true,
            clearHighlight = true,
        )
    }

    fun mission(id: String): UiInteractionResult {
        val state = gameState.missions.firstOrNull { it.definition.id == id } ?: return UiInteractionResult()
        if (state.completed && !state.claimed) {
            val result = gameState.claimMission(id)
            if (result.claimed) {
                val mission = result.mission ?: return UiInteractionResult()
                val reward = mission.reward
                val rewardText = if (reward.type == MissionRewardType.CASH) {
                    "$ ${UiNumberFormatter.compact(reward.amount)}"
                } else {
                    "◆ ${reward.amount.toInt()}"
                }
                return UiInteractionResult(
                    message = "MISSION COMPLETE! ${mission.title} +$rewardText",
                    durationNanos = DURATION_SUCCESS,
                    saveRequired = true,
                )
            }
        }

        return UiInteractionResult(
            message = "${state.definition.title} ${UiNumberFormatter.compact(state.progress)}/${UiNumberFormatter.compact(state.definition.target)}",
            durationNanos = DURATION_INFO,
        )
    }

    fun permanentUpgrade(id: String): UiInteractionResult {
        val upgradeState = gameState.permanentUpgrades.firstOrNull { it.definition.id == id } ?: return UiInteractionResult()
        val upgrade = upgradeState.definition
        val businessLevel = gameState.businesses.firstOrNull { it.id == upgrade.businessId }?.level ?: 0
        val result = gameState.buyPermanentUpgrade(id)
        if (result.purchased) {
            val purchasedUpgrade = result.upgrade ?: return UiInteractionResult()
            return UiInteractionResult(
                message = "PERMANENT UPGRADE! ${purchasedUpgrade.name}",
                durationNanos = DURATION_SUCCESS,
                saveRequired = true,
            )
        }

        val message = when {
            upgradeState.purchased -> "${upgrade.name} ALREADY PURCHASED"
            businessLevel < upgrade.unlockLevel -> "${upgrade.name} • UNLOCK Lv.${upgrade.unlockLevel}"
            gameState.cash < upgrade.cost -> "${upgrade.name} • NEED $${UiNumberFormatter.compact(upgrade.cost)} • NOW $${UiNumberFormatter.compact(gameState.cash)}"
            else -> "${upgrade.name} UNAVAILABLE"
        }
        return UiInteractionResult(message = message, durationNanos = DURATION_INFO)
    }

    fun manager(id: String): UiInteractionResult {
        val managerState = gameState.managers.firstOrNull { it.definition.id == id } ?: return UiInteractionResult()
        val manager = managerState.definition
        val businessLevel = gameState.businesses.firstOrNull { it.id == manager.businessId }?.level ?: 0
        val result = gameState.hire(id)
        if (result.hired) {
            val hiredManager = result.manager ?: return UiInteractionResult()
            return UiInteractionResult(
                message = "MANAGER HIRED! ${hiredManager.name} ×${UiNumberFormatter.multiplier(hiredManager.incomeMultiplier)}",
                durationNanos = DURATION_SUCCESS,
                saveRequired = true,
            )
        }

        val message = when {
            managerState.hired -> "${manager.name} ALREADY HIRED"
            businessLevel < manager.unlockLevel -> "${manager.name} • UNLOCK Lv.${manager.unlockLevel}"
            gameState.cash < manager.cost -> "HIRE ${manager.name} • NEED $${UiNumberFormatter.compact(manager.cost)} • NOW $${UiNumberFormatter.compact(gameState.cash)}"
            else -> "HIRE ${manager.name} UNAVAILABLE"
        }
        return UiInteractionResult(message = message, durationNanos = DURATION_INFO)
    }

    fun businessUpgrade(index: Int, mode: BuyMode): UiInteractionResult {
        val before = gameState.businesses.getOrNull(index) ?: return UiInteractionResult()
        val requestedQuote = gameState.quoteUpgrade(index, mode)
        val result = gameState.upgrade(index, mode)
        if (!result.upgraded) {
            val fallbackQuote = if (requestedQuote.levels > 0) requestedQuote else gameState.quoteUpgrade(index, BuyMode.X1)
            val message = if (fallbackQuote.levels > 0 && fallbackQuote.cost > 0.0) {
                "UPGRADE ${before.displayName} • NEED $${UiNumberFormatter.compact(fallbackQuote.cost)} • NOW $${UiNumberFormatter.compact(gameState.cash)}"
            } else {
                "UPGRADE ${before.displayName} UNAVAILABLE"
            }
            return UiInteractionResult(message = message, durationNanos = DURATION_INFO)
        }
        val milestone = result.reachedMilestones.lastOrNull()
        return if (milestone != null) {
            UiInteractionResult(
                message = "MILESTONE! ${before.displayName} Lv.${milestone.level} ×${UiNumberFormatter.multiplier(milestone.multiplier)}",
                durationNanos = DURATION_SUCCESS,
                saveRequired = true,
                highlightBusinessId = before.id,
            )
        } else {
            UiInteractionResult(saveRequired = true)
        }
    }

    companion object {
        const val DURATION_INFO = 3_000_000_000L
        const val DURATION_PROGRESS = 4_000_000_000L
        const val DURATION_SUCCESS = 5_000_000_000L
        const val DURATION_PRESTIGE = 6_000_000_000L
    }
}
