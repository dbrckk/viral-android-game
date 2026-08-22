package com.empiretycoon.idleconquest.game

import kotlin.math.floor
import kotlin.math.ln
import kotlin.math.pow

data class Milestone(val level: Int, val multiplier: Double)

enum class BuyMode { X1, X10, X25, MAX }

data class UpgradeQuote(val levels: Int, val cost: Double)
data class UpgradeResult(
    val upgraded: Boolean,
    val levelsBought: Int = 0,
    val totalCost: Double = 0.0,
    val reachedMilestones: List<Milestone> = emptyList()
)

data class BusinessState(
    val id: String,
    val displayName: String,
    val level: Int,
    val baseCost: Double,
    val baseIncomePerSecond: Double
) {
    val productionMultiplier: Double get() = milestoneMultiplierFor(level)
    val incomePerSecond: Double get() = baseIncomePerSecond * level.coerceAtLeast(1) * productionMultiplier
    val nextUpgradeCost: Double get() = upgradeCost(level, 1)

    fun upgradeCost(fromLevel: Int = level, count: Int): Double {
        if (count <= 0) return 0.0
        val first = baseCost * COST_GROWTH.pow(fromLevel.toDouble())
        return first * (COST_GROWTH.pow(count.toDouble()) - 1.0) / (COST_GROWTH - 1.0)
    }

    fun maxAffordableLevels(cash: Double): Int {
        if (cash < nextUpgradeCost || !cash.isFinite()) return 0
        val first = baseCost * COST_GROWTH.pow(level.toDouble())
        val inside = 1.0 + cash * (COST_GROWTH - 1.0) / first
        return floor(ln(inside) / ln(COST_GROWTH) + 1e-10).toInt().coerceAtLeast(0)
    }

    companion object {
        const val COST_GROWTH = 1.15
        private val milestones = listOf(
            Milestone(25, 2.0), Milestone(100, 4.0), Milestone(250, 8.0),
            Milestone(500, 16.0), Milestone(1_000, 32.0)
        )
        fun milestoneMultiplierFor(level: Int) = milestones.lastOrNull { level >= it.level }?.multiplier ?: 1.0
        fun nextMilestoneAfter(level: Int) = milestones.firstOrNull { it.level > level }
        fun milestonesCrossed(fromExclusive: Int, toInclusive: Int) = milestones.filter { it.level in (fromExclusive + 1)..toInclusive }
    }
}

class GameState {
    var cash: Double = 2_500.0; private set
    var gems: Int = 12; private set
    private val mutableBusinesses = mutableListOf(
        BusinessState("street_stand", "Street Stand", 1, 25.0, 2.0),
        BusinessState("corner_shop", "Corner Shop", 1, 160.0, 12.0),
        BusinessState("workshop", "Workshop", 1, 950.0, 65.0),
        BusinessState("factory", "Factory", 1, 5_500.0, 320.0)
    )
    val businesses: List<BusinessState> get() = mutableBusinesses
    val totalIncomePerSecond: Double get() = mutableBusinesses.sumOf { it.incomePerSecond }

    fun tick(deltaSeconds: Double) { if (deltaSeconds > 0.0) cash += totalIncomePerSecond * deltaSeconds }
    fun addCash(amount: Double) { if (amount > 0.0 && amount.isFinite()) cash += amount }

    fun restoreEconomy(cash: Double, gems: Int, levels: Map<String, Int>) {
        this.cash = cash.coerceAtLeast(0.0); this.gems = gems.coerceAtLeast(0)
        mutableBusinesses.replaceAll { it.copy(level = levels[it.id]?.coerceAtLeast(1) ?: it.level) }
    }

    fun quoteUpgrade(index: Int, mode: BuyMode): UpgradeQuote {
        val business = mutableBusinesses.getOrNull(index) ?: return UpgradeQuote(0, 0.0)
        val count = when (mode) {
            BuyMode.X1 -> 1
            BuyMode.X10 -> 10
            BuyMode.X25 -> 25
            BuyMode.MAX -> business.maxAffordableLevels(cash)
        }
        return UpgradeQuote(count, business.upgradeCost(count = count))
    }

    fun canUpgrade(index: Int, mode: BuyMode = BuyMode.X1): Boolean {
        val quote = quoteUpgrade(index, mode)
        return quote.levels > 0 && quote.cost <= cash
    }

    fun upgrade(index: Int, mode: BuyMode = BuyMode.X1): UpgradeResult {
        val business = mutableBusinesses.getOrNull(index) ?: return UpgradeResult(false)
        val quote = quoteUpgrade(index, mode)
        if (quote.levels <= 0 || quote.cost > cash || !quote.cost.isFinite()) return UpgradeResult(false)
        cash -= quote.cost
        val newLevel = business.level + quote.levels
        mutableBusinesses[index] = business.copy(level = newLevel)
        return UpgradeResult(true, quote.levels, quote.cost, BusinessState.milestonesCrossed(business.level, newLevel))
    }

    fun tierFor(level: Int): String = when {
        level >= 1_000 -> "master"; level >= 500 -> "lv500"; level >= 250 -> "lv250"
        level >= 100 -> "lv100"; level >= 25 -> "lv25"; else -> "base"
    }
}
