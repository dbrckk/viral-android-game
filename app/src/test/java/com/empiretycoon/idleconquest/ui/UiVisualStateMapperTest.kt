package com.empiretycoon.idleconquest.ui

import org.junit.Assert.assertEquals
import org.junit.Test

class UiVisualStateMapperTest {
    @Test
    fun businessCardPrioritizesMilestoneThenUpgrade() {
        assertEquals("milestone", UiVisualStateMapper.businessCard(active = true, canUpgrade = true))
        assertEquals("upgrade_ready", UiVisualStateMapper.businessCard(active = false, canUpgrade = true))
        assertEquals("normal", UiVisualStateMapper.businessCard(active = false, canUpgrade = false))
    }

    @Test
    fun missionMapsLifecycleStates() {
        assertEquals("claimed", UiVisualStateMapper.mission(claimed = true, completed = true, progress = 1.0))
        assertEquals("complete", UiVisualStateMapper.mission(claimed = false, completed = true, progress = 1.0))
        assertEquals("active", UiVisualStateMapper.mission(claimed = false, completed = false, progress = 0.5))
        assertEquals("locked", UiVisualStateMapper.mission(claimed = false, completed = false, progress = 0.0))
    }

    @Test
    fun managerAndPermanentStatesRespectUnlocks() {
        assertEquals("hired", UiVisualStateMapper.manager(true, 1, 100))
        assertEquals("available", UiVisualStateMapper.manager(false, 100, 100))
        assertEquals("locked", UiVisualStateMapper.manager(false, 99, 100))

        assertEquals("purchased", UiVisualStateMapper.permanentUpgrade(true, 1, 100))
        assertEquals("available", UiVisualStateMapper.permanentUpgrade(false, 100, 100))
        assertEquals("locked", UiVisualStateMapper.permanentUpgrade(false, 99, 100))
    }

    @Test
    fun prestigePrioritizesReadyThenOwned() {
        assertEquals("ready", UiVisualStateMapper.prestige(available = true, crowns = 3))
        assertEquals("owned", UiVisualStateMapper.prestige(available = false, crowns = 3))
        assertEquals("locked", UiVisualStateMapper.prestige(available = false, crowns = 0))
    }
}
