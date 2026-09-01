package com.empiretycoon.idleconquest.ui

import org.junit.Assert.assertTrue
import org.junit.Test

class BusinessCardLayoutEdgeCaseTest {
    @Test
    fun tinyCardNeverProducesInvertedPrimaryBounds() {
        val layout = BusinessCardLayout.calculate(UiBounds(0f, 0f, 20f, 12f), 2)
        val bounds = listOf(layout.art, layout.manager, layout.upgradeButton) + layout.permanentUpgradeSlots
        assertTrue(bounds.all { it.right >= it.left && it.bottom >= it.top })
    }

    @Test
    fun invertedInputIsHandledWithoutNegativeGeometry() {
        val layout = BusinessCardLayout.calculate(UiBounds(100f, 100f, 50f, 40f), 3)
        val bounds = listOf(layout.art, layout.manager, layout.upgradeButton) + layout.permanentUpgradeSlots
        assertTrue(bounds.all { it.right >= it.left && it.bottom >= it.top })
        assertTrue(layout.statIconSize >= 0f)
    }

    @Test
    fun negativePermanentUpgradeCountProducesNoSlots() {
        val layout = BusinessCardLayout.calculate(UiBounds(0f, 0f, 400f, 200f), -5)
        assertTrue(layout.permanentUpgradeSlots.isEmpty())
    }
}
