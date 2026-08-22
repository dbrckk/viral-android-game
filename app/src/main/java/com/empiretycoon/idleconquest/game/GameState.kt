package com.empiretycoon.idleconquest.game

import kotlin.math.pow

data class Milestone(
    val level: Int,
    val multiplier: Double
)

data class UpgradeResult(
    val upgraded: Boolean,
    val reachedMilestone: Milestone? = null
)

data class BusinessState(
    val id: String,
    val displayName: String,
    val level: Int,
    val baseCost: Double,
    val baseIncomePerSecond: Double
) {
    val productionMultiplier: Double
        get() = milestoneMultiplierFor(level)

    val incomePerSecond: Double
        get() = baseIncomePerSecond * level.coerceAtLeast(1) * productionMultiplier

    val nextUpgradeCost: Double
        get() = baseCost * 1.15.pow(level.toDouble())

    companion object {
        private val milestones = listOf(
            Milestone(25, 2.0),
            Milestone(100, 4.0),
            Milestone(250, 8.0),
            Milestone(500, 16.0),
            Milestone(1_000, 32.0)
        )

        fun milestoneMultiplierFor(level: Int): Double =
            milestones.lastOrNull { level >= it.level }?.multiplier ?: 1.0

        fun milestoneAt(level: Int): Milestone? = milestones.firstOrNull { it.level == level }

        fun nextMilestoneAfter(level: Int): Milestone? = milestones.firstOrNull { it.level > level }
    }
}

class GameState {
    var cash: Double = 2_500.0
        private set

    var gems: Int = 12
        private set

    private val mutableBusinesses = mutableListOf(
        BusinessState("street_stand", "Street Stand", 1, 25.0, 2.0),
        BusinessState("corner_shop", "Corner Shop", 1, 160.0, 12.0),
        BusinessState("workshop", "Workshop", 1, 950.0, 65.0),
        BusinessState("factory", "Factory", 1, 5_500.0, 320.0)
    )

    val businesses: List<BusinessState>
        get() = mutableBusinesses

    val totalIncomePerSecond: Double
        get() = mutableBusinesses.sumOf { it.incomePerSecond }

    fun tick(deltaSeconds: Double) {
        if (deltaSeconds <= 0.0) return
        cash += totalIncomePerSecond * deltaSeconds
    }

    fun addCash(amount: Double) {
        if (amount > 0.0 && amount.isFinite()) cash += amount
    }

    fun restoreEconomy(cash: Double, gems: Int, levels: Map<String, Int>) {
        this.cash = cash.coerceAtLeast(0.0)
        this.gems = gems.coerceAtLeast(0)

        mutableBusinesses.replaceAll { business ->
            val restoredLevel = levels[business.id]?.coerceAtLeast(1) ?: business.level
            business.copy(level = restoredLevel)
        }
    }

    fun canUpgrade(index: Int): Boolean {
        val business = mutableBusinesses.getOrNull(index) ?: return false
        return cash >= business.nextUpgradeCost
    }

    fun upgrade(index: Int): UpgradeResult {
        val business = mutableBusinesses.getOrNull(index) ?: return UpgradeResult(false)
        val cost = business.nextUpgradeCost
        if (cash < cost) return UpgradeResult(false)

        cash -= cost
        val newLevel = business.level + 1
        mutableBusinesses[index] = business.copy(level = newLevel)
        return UpgradeResult(
            upgraded = true,
            reachedMilestone = BusinessState.milestoneAt(newLevel)
        )
    }

    fun tierFor(level: Int): String = when {
        level >= 1_000 -> "master"
        level >= 500 -> "lv500"
        level >= 250 -> "lv250"
        level >= 100 -> "lv100"
        level >= 25 -> "lv25"
        else -> "base"
    }
}
