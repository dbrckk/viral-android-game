package com.empiretycoon.idleconquest.game

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class GameStateMilestoneTest {
    @Test
    fun milestoneMultipliersMatchProgressionContract() {
        assertEquals(1.0, BusinessState.milestoneMultiplierFor(1), 0.0)
        assertEquals(2.0, BusinessState.milestoneMultiplierFor(25), 0.0)
        assertEquals(4.0, BusinessState.milestoneMultiplierFor(100), 0.0)
        assertEquals(8.0, BusinessState.milestoneMultiplierFor(250), 0.0)
        assertEquals(16.0, BusinessState.milestoneMultiplierFor(500), 0.0)
        assertEquals(32.0, BusinessState.milestoneMultiplierFor(1_000), 0.0)
    }

    @Test
    fun milestoneCrossingOnlyTriggersWhenThresholdIsReached() {
        assertTrue(BusinessState.milestonesCrossed(23, 24).isEmpty())
        assertEquals(listOf(Milestone(25, 2.0)), BusinessState.milestonesCrossed(24, 25))
        assertTrue(BusinessState.milestonesCrossed(25, 26).isEmpty())
        assertEquals(100, BusinessState.nextMilestoneAfter(25)?.level)
        assertNull(BusinessState.nextMilestoneAfter(1_000))
    }
}
