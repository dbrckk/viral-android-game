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
            return UiInteractionResult(
                message = "PRESTIGE AT $${UiNumberFormatter.compact(quote.requirement)} RUN EARNINGS • NOW ${UiNumberFormatter.compact(gameState.runEarnings)}",
                durationNanos = 4_000_000_000L,
            )
        }

        val result = gameState.prestige()
        if (!result.prestiged) return UiInteractionResult()
        return UiInteractionResult(
            message = "PRESTIGE! +${result.crownsEarned} CROWN • EMPIRE ×${UiNumberFormatter.multiplier(result.multiplier)}",
            durationNanos = 6_000_000_000L,
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
                    durationNanos = 5_000_000_000L,
                    saveRequired = true,
                )
            }
        }

        return UiInteractionResult(
            message = "${state.definition.title} ${UiNumberFormatter.compact(state.progress)}/${UiNumberFormatter.compact(state.definition.target)}",
            durationNanos = 3_000_000_000L,
        )
    }

    fun permanentUpgrade(id: String): UiInteractionResult {
        val result = gameState.buyPermanentUpgrade(id)
        if (result.purchased) {
            return UiInteractionResult(
                message = "PERMANENT UPGRADE! ${result.upgrade!!.name}",
                durationNanos = 5_000_000_000L,
                saveRequired = true,
            )
        }
        val upgrade = PermanentUpgradeCatalog.all.firstOrNull { it.id == id } ?: return UiInteractionResult()
        return UiInteractionResult(
            message = "${upgrade.name} • Lv.${upgrade.unlockLevel} • $${UiNumberFormatter.compact(upgrade.cost)}",
            durationNanos = 3_000_000_000L,
        )
    }

    fun manager(id: String): UiInteractionResult {
        val result = gameState.hire(id)
        if (!result.hired) return UiInteractionResult()
        val manager = result.manager ?: return UiInteractionResult()
        return UiInteractionResult(
            message = "MANAGER HIRED! ${manager.name} ×${UiNumberFormatter.multiplier(manager.incomeMultiplier)}",
            durationNanos = 5_000_000_000L,
            saveRequired = true,
        )
    }

    fun businessUpgrade(index: Int, mode: BuyMode): UiInteractionResult {
        val before = gameState.businesses.getOrNull(index) ?: return UiInteractionResult()
        val result = gameState.upgrade(index, mode)
        if (!result.upgraded) return UiInteractionResult()
        val milestone = result.reachedMilestones.lastOrNull()
        return if (milestone != null) {
            UiInteractionResult(
                message = "MILESTONE! ${before.displayName} Lv.${milestone.level} ×${UiNumberFormatter.multiplier(milestone.multiplier)}",
                durationNanos = 5_000_000_000L,
                saveRequired = true,
                highlightBusinessId = before.id,
            )
        } else {
            UiInteractionResult(saveRequired = true)
        }
    }
}
