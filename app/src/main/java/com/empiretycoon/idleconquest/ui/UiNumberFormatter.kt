package com.empiretycoon.idleconquest.ui

import java.util.Locale

object UiNumberFormatter {
    fun compact(value: Double): String = when {
        value >= 1e36 -> String.format(Locale.US, "%.2e", value)
        value >= 1e33 -> String.format(Locale.US, "%.2fDc", value / 1e33)
        value >= 1e30 -> String.format(Locale.US, "%.2fNo", value / 1e30)
        value >= 1e27 -> String.format(Locale.US, "%.2fOc", value / 1e27)
        value >= 1e24 -> String.format(Locale.US, "%.2fSp", value / 1e24)
        value >= 1e21 -> String.format(Locale.US, "%.2fSx", value / 1e21)
        value >= 1e18 -> String.format(Locale.US, "%.2fQi", value / 1e18)
        value >= 1e15 -> String.format(Locale.US, "%.2fQa", value / 1e15)
        value >= 1e12 -> String.format(Locale.US, "%.2fT", value / 1e12)
        value >= 1e9 -> String.format(Locale.US, "%.2fB", value / 1e9)
        value >= 1e6 -> String.format(Locale.US, "%.2fM", value / 1e6)
        value >= 1e3 -> String.format(Locale.US, "%.2fK", value / 1e3)
        else -> String.format(Locale.US, "%.0f", value)
    }

    fun multiplier(value: Double): String =
        if (value % 1.0 == 0.0) String.format(Locale.US, "%.0f", value)
        else String.format(Locale.US, "%.2f", value)
}
