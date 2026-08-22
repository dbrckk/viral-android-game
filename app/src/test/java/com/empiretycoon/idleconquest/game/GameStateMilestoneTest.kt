package com.empiretycoon.idleconquest.game

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
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
    fun milestoneLookupOnlyTriggersOnExactThreshold() {
        assertNull(BusinessState.milestoneAt(24))
        assertNotNull(BusinessState.milestoneAt(25))
        assertNull(BusinessState.milestoneAt(26))
        assertEquals(100, BusinessState.nextMilestoneAfter(25)?.level)
        assertNull(BusinessState.nextMilestoneAfter(1_000))
    }
}
