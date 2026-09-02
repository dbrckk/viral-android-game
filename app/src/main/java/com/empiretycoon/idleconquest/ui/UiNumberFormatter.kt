package com.empiretycoon.idleconquest.ui

import java.util.Locale
import kotlin.math.abs
import kotlin.math.round

object UiNumberFormatter {
    private data class CompactUnit(val threshold: Double, val suffix: String)

    private val compactUnits = listOf(
        CompactUnit(1e3, "K"),
        CompactUnit(1e6, "M"),
        CompactUnit(1e9, "B"),
        CompactUnit(1e12, "T"),
        CompactUnit(1e15, "Qa"),
        CompactUnit(1e18, "Qi"),
        CompactUnit(1e21, "Sx"),
        CompactUnit(1e24, "Sp"),
        CompactUnit(1e27, "Oc"),
        CompactUnit(1e30, "No"),
        CompactUnit(1e33, "Dc"),
    )

    fun compact(value: Double): String {
        if (value.isNaN()) return "0"
        if (value == Double.POSITIVE_INFINITY) return "∞"
        if (value == Double.NEGATIVE_INFINITY) return "-∞"

        val magnitude = abs(value)
        if (magnitude < 1e3) return String.format(Locale.US, "%.0f", value)
        if (magnitude >= 1e36) return String.format(Locale.US, "%.2e", value)

        var unitIndex = compactUnits.indexOfLast { magnitude >= it.threshold }
        if (unitIndex < 0) return String.format(Locale.US, "%.0f", value)

        while (unitIndex < compactUnits.lastIndex) {
            val unit = compactUnits[unitIndex]
            val roundedScaled = round(abs(value / unit.threshold) * 100.0) / 100.0
            if (roundedScaled < 1_000.0) break
            unitIndex++
        }

        val unit = compactUnits[unitIndex]
        val scaled = value / unit.threshold
        val roundedScaled = round(abs(scaled) * 100.0) / 100.0
        if (unitIndex == compactUnits.lastIndex && roundedScaled >= 1_000.0) {
            return String.format(Locale.US, "%.2e", value)
        }
        return String.format(Locale.US, "%.2f%s", scaled, unit.suffix)
    }

    fun multiplier(value: Double): String {
        if (value.isNaN()) return "0"
        if (value == Double.POSITIVE_INFINITY) return "∞"
        if (value == Double.NEGATIVE_INFINITY) return "-∞"
        return if (value % 1.0 == 0.0) String.format(Locale.US, "%.0f", value)
        else String.format(Locale.US, "%.2f", value)
    }
}
