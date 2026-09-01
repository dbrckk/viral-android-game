package com.empiretycoon.idleconquest.ui

import java.util.Locale

object UiNumberFormatter {
    fun compact(value: Double): String = when {
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
