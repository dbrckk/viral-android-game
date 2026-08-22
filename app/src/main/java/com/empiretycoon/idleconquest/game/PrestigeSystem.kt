package com.empiretycoon.idleconquest.game

import kotlin.math.floor
import kotlin.math.sqrt

data class PrestigeQuote(
    val available: Boolean,
    val crownReward: Int,
    val currentCrowns: Int,
    val nextMultiplier: Double,
    val requirement: Double
)

data class PrestigeResult(
    val prestiged: Boolean,
    val crownsEarned: Int = 0,
    val totalCrowns: Int = 0,
    val multiplier: Double = 1.0
)

object PrestigeRules {
    const val BASE_REQUIREMENT = 1_000_000.0
    const val CROWN_POWER = 0.50
    const val BONUS_PER_CROWN = 0.20

    fun crownsFor(runValue: Double): Int {
        if (!runValue.isFinite() || runValue < BASE_REQUIREMENT) return 0
        return floor(sqrt(runValue / BASE_REQUIREMENT)).toInt().coerceAtLeast(1)
    }

    fun multiplier(crowns: Int): Double = 1.0 + crowns.coerceAtLeast(0) * BONUS_PER_CROWN
}
