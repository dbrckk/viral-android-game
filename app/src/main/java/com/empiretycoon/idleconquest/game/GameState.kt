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

data class HireResult(val hired: Boolean, val manager: ManagerDefinition? = null)

data class BusinessState(
    val id: String,
    val displayName: String,
    val level: Int,
    val baseCost: Double,
    val baseIncomePerSecond: Double
) {
    val productionMultiplier: Double get() = milestoneMultiplierFor(level)
    val rawIncomePerSecond: Double
        get() = baseIncomePerSecond * level.coerceAtLeast(1) * productionMultiplier
    val nextUpgradeCost: Double get() = upgradeCost(level, 1)

    fun upgradeCost(fromLevel: Int = level, count: Int): Double {
        if (count <= 0) return 0.0
        val first = baseCost * COST_GROWTH.pow(fromLevel.toDouble())
        return first * (COST_GROWTH.pow(count.toDouble()) - 1) / (COST_GROWTH - 1)
    }

    fun maxAffordableLevels(cash: Double, costMultiplier: Double = 1.0): Int {
        if (!cash.isFinite() || cash < 0.0 || !costMultiplier.isFinite() || costMultiplier <= 0.0) return 0
        val next = nextUpgradeCost * costMultiplier
        if (!next.isFinite() || cash < next) return 0
        val first = baseCost * COST_GROWTH.pow(level.toDouble()) * costMultiplier
        if (!first.isFinite() || first <= 0.0) return 0
        val estimate = ln(1 + cash * (COST_GROWTH - 1) / first) / ln(COST_GROWTH)
        if (!estimate.isFinite() || estimate <= 0.0) return 0

        var candidate = floor(estimate).toInt().coerceAtLeast(1)
        while (candidate > 0) {
            val cost = upgradeCost(count = candidate) * costMultiplier
            if (cost.isFinite() && cost <= cash) break
            candidate--
        }
        while (candidate < Int.MAX_VALUE) {
            val nextCandidate = candidate + 1
            val nextCost = upgradeCost(count = nextCandidate) * costMultiplier
            if (!nextCost.isFinite() || nextCost > cash) break
            candidate = nextCandidate
        }
        return candidate
    }

    companion object {
        const val COST_GROWTH = 1.15
        private val milestones = listOf(
            Milestone(25, 2.0),
            Milestone(100, 4.0),
            Milestone(250, 8.0),
            Milestone(500, 16.0),
            Milestone(1000, 32.0)
        )

        fun milestoneMultiplierFor(level: Int) =
            milestones.lastOrNull { level >= it.level }?.multiplier ?: 1.0

        fun nextMilestoneAfter(level: Int) = milestones.firstOrNull { it.level > level }

        fun milestonesCrossed(fromLevel: Int, toLevel: Int) =
            milestones.filter { it.level in (fromLevel + 1)..toLevel }
    }
}

class GameState {
    var cash = 2_500.0
        private set
    var gems = 12
        private set
    var prestigeCrowns = 0
        private set
    var runEarnings = 0.0
        private set

    private val mutableBusinesses = mutableListOf(
        BusinessState("street_stand", "Street Stand", 1, 25.0, 2.0),
        BusinessState("corner_shop", "Corner Shop", 1, 160.0, 12.0),
        BusinessState("workshop", "Workshop", 1, 950.0, 65.0),
        BusinessState("factory", "Factory", 1, 5500.0, 320.0)
    )
    private val hiredManagerIds = mutableSetOf<String>()
    private val purchasedPermanentUpgradeIds = mutableSetOf<String>()
    private val claimedMissionIds = mutableSetOf<String>()

    val businesses: List<BusinessState> get() = mutableBusinesses
    val managers: List<ManagerState>
        get() = ManagerCatalog.all.map { ManagerState(it, it.id in hiredManagerIds) }
    val permanentUpgrades: List<PermanentUpgradeState>
        get() = PermanentUpgradeCatalog.all.map {
            PermanentUpgradeState(it, it.id in purchasedPermanentUpgradeIds)
        }
    val missions: List<MissionState>
        get() = MissionCatalog.all.map { mission ->
            val progress = missionProgress(mission)
            MissionState(mission, progress, progress >= mission.target, mission.id in claimedMissionIds)
        }
    val prestigeMultiplier: Double get() = PrestigeRules.multiplier(prestigeCrowns)

    fun managerFor(businessId: String): ManagerState? {
        val definition = ManagerCatalog.all.firstOrNull { it.businessId == businessId } ?: return null
        return ManagerState(definition, definition.id in hiredManagerIds)
    }

    fun managerMultiplier(businessId: String): Double {
        val manager = ManagerCatalog.all.firstOrNull { it.businessId == businessId } ?: return 1.0
        return if (manager.id in hiredManagerIds) manager.incomeMultiplier else 1.0
    }

    fun permanentUpgradesFor(businessId: String): List<PermanentUpgradeState> =
        PermanentUpgradeCatalog.all.asSequence()
            .filter { it.businessId == businessId }
            .map { PermanentUpgradeState(it, it.id in purchasedPermanentUpgradeIds) }
            .toList()

    fun permanentIncomeMultiplier(businessId: String): Double {
        var multiplier = 1.0
        for (upgrade in PermanentUpgradeCatalog.all) {
            if (
                upgrade.businessId == businessId &&
                upgrade.id in purchasedPermanentUpgradeIds &&
                upgrade.effect == PermanentUpgradeEffect.INCOME_MULTIPLIER
            ) {
                multiplier *= upgrade.value
            }
        }
        return multiplier
    }

    fun permanentCostMultiplier(businessId: String): Double {
        var multiplier = 1.0
        for (upgrade in PermanentUpgradeCatalog.all) {
            if (
                upgrade.businessId == businessId &&
                upgrade.id in purchasedPermanentUpgradeIds &&
                upgrade.effect == PermanentUpgradeEffect.COST_MULTIPLIER
            ) {
                multiplier *= upgrade.value
            }
        }
        return multiplier
    }

    fun incomeFor(business: BusinessState): Double {
        val income = business.rawIncomePerSecond *
            managerMultiplier(business.id) *
            permanentIncomeMultiplier(business.id) *
            prestigeMultiplier
        return income.takeIf { it.isFinite() && it >= 0.0 } ?: 0.0
    }

    val totalIncomePerSecond: Double
        get() {
            val total = mutableBusinesses.sumOf { incomeFor(it) }
            return total.takeIf { it.isFinite() && it >= 0.0 } ?: 0.0
        }

    fun tick(deltaSeconds: Double) {
        if (!deltaSeconds.isFinite() || deltaSeconds <= 0.0) return
        val earned = totalIncomePerSecond * deltaSeconds
        if (!earned.isFinite() || earned <= 0.0) return
        cash = safeAdd(cash, earned)
        runEarnings = safeAdd(runEarnings, earned)
    }

    fun addCash(amount: Double, countForPrestige: Boolean = false) {
        if (!amount.isFinite() || amount <= 0.0) return
        cash = safeAdd(cash, amount)
        if (countForPrestige) runEarnings = safeAdd(runEarnings, amount)
    }

    fun addGems(amount: Int) {
        if (amount <= 0) return
        gems = saturatingAddInt(gems, amount)
    }

    fun restoreEconomy(
        cash: Double,
        gems: Int,
        levels: Map<String, Int>,
        hiredManagers: Set<String> = emptySet(),
        permanentUpgrades: Set<String> = emptySet(),
        claimedMissions: Set<String> = emptySet(),
        prestigeCrowns: Int = 0,
        runEarnings: Double = 0.0
    ) {
        this.cash = cash.takeIf { it.isFinite() && it >= 0.0 } ?: 0.0
        this.gems = gems.coerceAtLeast(0)
        this.prestigeCrowns = prestigeCrowns.coerceAtLeast(0)
        this.runEarnings = runEarnings.takeIf { it.isFinite() && it >= 0.0 } ?: 0.0
        for (index in mutableBusinesses.indices) {
            val business = mutableBusinesses[index]
            mutableBusinesses[index] = business.copy(
                level = levels[business.id]?.coerceAtLeast(1) ?: business.level
            )
        }
        hiredManagerIds.clear()
        hiredManagerIds.addAll(hiredManagers.filter { id -> ManagerCatalog.all.any { it.id == id } })
        purchasedPermanentUpgradeIds.clear()
        purchasedPermanentUpgradeIds.addAll(
            permanentUpgrades.filter { id -> PermanentUpgradeCatalog.all.any { it.id == id } }
        )
        claimedMissionIds.clear()
        claimedMissionIds.addAll(claimedMissions.filter { id -> MissionCatalog.all.any { it.id == id } })
    }

    fun quoteUpgrade(index: Int, mode: BuyMode): UpgradeQuote {
        val business = mutableBusinesses.getOrNull(index) ?: return UpgradeQuote(0, 0.0)
        val remainingCapacity = Int.MAX_VALUE - business.level
        if (remainingCapacity <= 0) return UpgradeQuote(0, 0.0)
        val costMultiplier = permanentCostMultiplier(business.id)
        val requestedLevels = when (mode) {
            BuyMode.X1 -> 1
            BuyMode.X10 -> 10
            BuyMode.X25 -> 25
            BuyMode.MAX -> business.maxAffordableLevels(cash, costMultiplier)
        }
        val levels = minOf(requestedLevels, remainingCapacity)
        val cost = business.upgradeCost(count = levels) * costMultiplier
        return if (levels > 0 && cost.isFinite() && cost >= 0.0) {
            UpgradeQuote(levels, cost)
        } else {
            UpgradeQuote(0, 0.0)
        }
    }

    fun canUpgrade(index: Int, mode: BuyMode = BuyMode.X1) =
        quoteUpgrade(index, mode).let { it.levels > 0 && it.cost <= cash }

    fun upgrade(index: Int, mode: BuyMode = BuyMode.X1): UpgradeResult {
        val business = mutableBusinesses.getOrNull(index) ?: return UpgradeResult(false)
        val quote = quoteUpgrade(index, mode)
        if (quote.levels <= 0 || quote.cost > cash || !quote.cost.isFinite()) return UpgradeResult(false)
        cash -= quote.cost
        val newLevel = saturatingAddInt(business.level, quote.levels)
        mutableBusinesses[index] = business.copy(level = newLevel)
        return UpgradeResult(
            true,
            newLevel - business.level,
            quote.cost,
            BusinessState.milestonesCrossed(business.level, newLevel)
        )
    }

    fun canHire(managerId: String): Boolean {
        val manager = ManagerCatalog.all.firstOrNull { it.id == managerId } ?: return false
        val level = businesses.firstOrNull { it.id == manager.businessId }?.level ?: 0
        return managerId !in hiredManagerIds && level >= manager.unlockLevel && cash >= manager.cost
    }

    fun hire(managerId: String): HireResult {
        val manager = ManagerCatalog.all.firstOrNull { it.id == managerId } ?: return HireResult(false)
        if (!canHire(managerId)) return HireResult(false)
        cash -= manager.cost
        hiredManagerIds += manager.id
        return HireResult(true, manager)
    }

    fun canBuyPermanentUpgrade(id: String): Boolean {
        val upgrade = PermanentUpgradeCatalog.all.firstOrNull { it.id == id } ?: return false
        val level = businesses.firstOrNull { it.id == upgrade.businessId }?.level ?: 0
        return id !in purchasedPermanentUpgradeIds && level >= upgrade.unlockLevel && cash >= upgrade.cost
    }

    fun buyPermanentUpgrade(id: String): PermanentUpgradePurchaseResult {
        val upgrade = PermanentUpgradeCatalog.all.firstOrNull { it.id == id }
            ?: return PermanentUpgradePurchaseResult(false)
        if (!canBuyPermanentUpgrade(id)) return PermanentUpgradePurchaseResult(false)
        cash -= upgrade.cost
        purchasedPermanentUpgradeIds += id
        return PermanentUpgradePurchaseResult(true, upgrade)
    }

    fun missionProgress(mission: MissionDefinition): Double = when (mission.metric) {
        MissionMetric.TOTAL_LEVELS -> businesses.sumOf { it.level.toLong() }.toDouble()
        MissionMetric.BUSINESS_LEVEL ->
            businesses.firstOrNull { it.id == mission.businessId }?.level?.toDouble() ?: 0.0
        MissionMetric.INCOME_PER_SECOND -> totalIncomePerSecond
        MissionMetric.MANAGERS_HIRED -> hiredManagerIds.size.toDouble()
        MissionMetric.UPGRADES_PURCHASED -> purchasedPermanentUpgradeIds.size.toDouble()
    }

    fun claimMission(id: String): MissionClaimResult {
        val mission = MissionCatalog.all.firstOrNull { it.id == id } ?: return MissionClaimResult(false)
        if (id in claimedMissionIds || missionProgress(mission) < mission.target) {
            return MissionClaimResult(false)
        }
        when (mission.reward.type) {
            MissionRewardType.CASH -> addCash(mission.reward.amount)
            MissionRewardType.GEMS -> addGems(mission.reward.amount.toInt())
        }
        claimedMissionIds += id
        return MissionClaimResult(true, mission)
    }

    fun prestigeQuote(): PrestigeQuote {
        val reward = PrestigeRules.crownsFor(runEarnings)
        val nextCrowns = saturatingAddInt(prestigeCrowns, reward)
        return PrestigeQuote(
            reward > 0,
            reward,
            prestigeCrowns,
            PrestigeRules.multiplier(nextCrowns),
            PrestigeRules.BASE_REQUIREMENT
        )
    }

    fun prestige(): PrestigeResult {
        val quote = prestigeQuote()
        if (!quote.available) return PrestigeResult(false)
        prestigeCrowns = saturatingAddInt(prestigeCrowns, quote.crownReward)
        cash = 2_500.0
        runEarnings = 0.0
        for (index in mutableBusinesses.indices) {
            val business = mutableBusinesses[index]
            mutableBusinesses[index] = business.copy(level = 1)
        }
        hiredManagerIds.clear()
        return PrestigeResult(true, quote.crownReward, prestigeCrowns, prestigeMultiplier)
    }

    fun hiredManagers(): Set<String> = hiredManagerIds.toSet()
    fun purchasedPermanentUpgrades(): Set<String> = purchasedPermanentUpgradeIds.toSet()
    fun claimedMissions(): Set<String> = claimedMissionIds.toSet()

    fun tierFor(level: Int) = when {
        level >= 1000 -> "master"
        level >= 500 -> "lv500"
        level >= 250 -> "lv250"
        level >= 100 -> "lv100"
        level >= 25 -> "lv25"
        else -> "base"
    }

    private fun safeAdd(current: Double, amount: Double): Double {
        val result = current + amount
        return if (result.isFinite()) result else Double.MAX_VALUE
    }

    private fun saturatingAddInt(current: Int, amount: Int): Int =
        (current.toLong() + amount.toLong()).coerceIn(0L, Int.MAX_VALUE.toLong()).toInt()
}
