package com.empiretycoon.idleconquest.ui

import java.util.Locale

object BusinessCardTextFormatter {
    fun level(level: Int, tier: String, productionMultiplier: Double): String =
        "Lv.$level • ${tier.uppercase(Locale.US)} • ×${UiNumberFormatter.multiplier(productionMultiplier)}"

    fun income(incomePerSecond: Double): String =
        "+${UiNumberFormatter.compact(incomePerSecond)}/s"

    fun manager(
        name: String?,
        hired: Boolean,
        businessLevel: Int,
        unlockLevel: Int,
        cost: Double,
        incomeMultiplier: Double,
    ): String {
        if (name == null) return "MANAGER UNAVAILABLE"
        return when {
            hired -> "$name ×${UiNumberFormatter.multiplier(incomeMultiplier)}"
            businessLevel >= unlockLevel -> "HIRE $name $${UiNumberFormatter.compact(cost)}"
            else -> "MANAGER Lv.$unlockLevel"
        }
    }

    fun permanent(incomeMultiplier: Double, costMultiplier: Double): String =
        "PERM income ×${UiNumberFormatter.multiplier(incomeMultiplier)} • cost ×${UiNumberFormatter.multiplier(costMultiplier)}"
}
