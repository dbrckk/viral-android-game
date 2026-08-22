package com.empiretycoon.idleconquest.game

import kotlin.math.pow

data class BusinessState(
    val id: String,
    val displayName: String,
    val level: Int,
    val baseCost: Double,
    val baseIncomePerSecond: Double
) {
    val incomePerSecond: Double
        get() = baseIncomePerSecond * level.coerceAtLeast(1)

    val nextUpgradeCost: Double
        get() = baseCost * 1.15.pow(level.toDouble())
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

    fun canUpgrade(index: Int): Boolean {
        val business = mutableBusinesses.getOrNull(index) ?: return false
        return cash >= business.nextUpgradeCost
    }

    fun upgrade(index: Int): Boolean {
        val business = mutableBusinesses.getOrNull(index) ?: return false
        val cost = business.nextUpgradeCost
        if (cash < cost) return false

        cash -= cost
        mutableBusinesses[index] = business.copy(level = business.level + 1)
        return true
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
