package com.empiretycoon.idleconquest.ui

import org.junit.Assert.assertEquals
import org.junit.Test

class EconomyTextFormatterTest {
    @Test
    fun compactFormatsThresholds() {
        assertEquals("999", EconomyTextFormatter.compact(999.0))
        assertEquals("1.00K", EconomyTextFormatter.compact(1_000.0))
        assertEquals("1.50M", EconomyTextFormatter.compact(1_500_000.0))
        assertEquals("2.00B", EconomyTextFormatter.compact(2_000_000_000.0))
        assertEquals("3.25T", EconomyTextFormatter.compact(3_250_000_000_000.0))
    }

    @Test
    fun multiplierKeepsIntegerValuesCompact() {
        assertEquals("1", EconomyTextFormatter.multiplier(1.0))
        assertEquals("2", EconomyTextFormatter.multiplier(2.0))
        assertEquals("1.50", EconomyTextFormatter.multiplier(1.5))
        assertEquals("2.25", EconomyTextFormatter.multiplier(2.25))
    }
}
