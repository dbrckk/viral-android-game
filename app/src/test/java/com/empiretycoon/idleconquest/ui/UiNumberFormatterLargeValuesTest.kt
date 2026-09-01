package com.empiretycoon.idleconquest.ui

import org.junit.Assert.assertEquals
import org.junit.Test

class UiNumberFormatterLargeValuesTest {
    @Test
    fun compactUsesExtendedIdleGameSuffixes() {
        assertEquals("1.00Qa", UiNumberFormatter.compact(1e15))
        assertEquals("1.00Qi", UiNumberFormatter.compact(1e18))
        assertEquals("1.00Sx", UiNumberFormatter.compact(1e21))
        assertEquals("1.00Sp", UiNumberFormatter.compact(1e24))
        assertEquals("1.00Oc", UiNumberFormatter.compact(1e27))
        assertEquals("1.00No", UiNumberFormatter.compact(1e30))
        assertEquals("1.00Dc", UiNumberFormatter.compact(1e33))
    }

    @Test
    fun compactFallsBackToScientificNotationPastNamedSuffixes() {
        assertEquals("1.00e+36", UiNumberFormatter.compact(1e36))
    }
}
