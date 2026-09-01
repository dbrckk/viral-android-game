package com.empiretycoon.idleconquest.ui

import org.junit.Assert.assertEquals
import org.junit.Test

class UiNumberFormatterTest {
    @Test
    fun compactFormatsBaseAndThresholdValues() {
        assertEquals("0", UiNumberFormatter.compact(0.0))
        assertEquals("999", UiNumberFormatter.compact(999.0))
        assertEquals("1.00K", UiNumberFormatter.compact(1_000.0))
        assertEquals("1.50K", UiNumberFormatter.compact(1_500.0))
        assertEquals("1.00M", UiNumberFormatter.compact(1_000_000.0))
        assertEquals("1.00B", UiNumberFormatter.compact(1_000_000_000.0))
        assertEquals("1.00T", UiNumberFormatter.compact(1_000_000_000_000.0))
    }

    @Test
    fun compactRoundsUsingUsDecimalSeparator() {
        assertEquals("1.23K", UiNumberFormatter.compact(1_234.0))
        assertEquals("12.35M", UiNumberFormatter.compact(12_345_678.0))
    }

    @Test
    fun multiplierDropsDecimalsForWholeNumbers() {
        assertEquals("1", UiNumberFormatter.multiplier(1.0))
        assertEquals("32", UiNumberFormatter.multiplier(32.0))
    }

    @Test
    fun multiplierKeepsTwoDecimalsForFractionalValues() {
        assertEquals("1.50", UiNumberFormatter.multiplier(1.5))
        assertEquals("1.75", UiNumberFormatter.multiplier(1.75))
    }

    @Test
    fun multiplierDoesNotClampLargeWholeNumbersToIntRange() {
        assertEquals("3000000000", UiNumberFormatter.multiplier(3_000_000_000.0))
        assertEquals("1000000000000", UiNumberFormatter.multiplier(1_000_000_000_000.0))
    }
}
