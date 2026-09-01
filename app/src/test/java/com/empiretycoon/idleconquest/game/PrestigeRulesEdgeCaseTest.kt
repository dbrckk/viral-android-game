package com.empiretycoon.idleconquest.game

import org.junit.Assert.assertEquals
import org.junit.Test

class PrestigeRulesEdgeCaseTest {
    @Test
    fun invalidRunValuesDoNotGrantCrowns() {
        assertEquals(0, PrestigeRules.crownsFor(Double.NaN))
        assertEquals(0, PrestigeRules.crownsFor(Double.POSITIVE_INFINITY))
        assertEquals(0, PrestigeRules.crownsFor(-1.0))
    }

    @Test
    fun extremelyLargeFiniteRunsSaturateReward() {
        assertEquals(Int.MAX_VALUE, PrestigeRules.crownsFor(Double.MAX_VALUE))
    }
}
