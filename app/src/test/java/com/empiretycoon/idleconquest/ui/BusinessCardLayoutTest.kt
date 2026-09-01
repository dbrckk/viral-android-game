package com.empiretycoon.idleconquest.ui

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class BusinessCardLayoutTest {
    private val card = UiBounds(40f, 400f, 960f, 760f)

    @Test
    fun calculateKeepsPrimaryRegionsInsideCard() {
        val layout = BusinessCardLayout.calculate(card, permanentUpgradeCount = 2)

        listOf(layout.art, layout.manager, layout.upgradeButton).forEach { bounds ->
            assertTrue(bounds.left >= card.left)
            assertTrue(bounds.top >= card.top)
            assertTrue(bounds.right <= card.right)
            assertTrue(bounds.bottom <= card.bottom)
            assertTrue(bounds.right > bounds.left)
            assertTrue(bounds.bottom > bounds.top)
        }
    }

    @Test
    fun permanentUpgradeSlotsAreEvenAndSeparated() {
        val layout = BusinessCardLayout.calculate(card, permanentUpgradeCount = 2)
        assertEquals(2, layout.permanentUpgradeSlots.size)
        val first = layout.permanentUpgradeSlots[0]
        val second = layout.permanentUpgradeSlots[1]
        assertEquals(first.bottom - first.top, second.bottom - second.top, 0.001f)
        assertEquals(5f, second.top - first.bottom, 0.001f)
    }

    @Test
    fun densePermanentUpgradeLayoutsStayInsideCard() {
        val layout = BusinessCardLayout.calculate(card, permanentUpgradeCount = 100)

        assertEquals(100, layout.permanentUpgradeSlots.size)
        layout.permanentUpgradeSlots.forEach { bounds ->
            assertTrue(bounds.top >= card.top)
            assertTrue(bounds.bottom <= card.bottom)
            assertTrue(bounds.bottom >= bounds.top)
        }
        layout.permanentUpgradeSlots.zipWithNext().forEach { (first, second) ->
            assertTrue(second.top >= first.bottom)
        }
    }

    @Test
    fun textAnchorsMatchCurrentCardProportions() {
        val layout = BusinessCardLayout.calculate(card)
        val height = card.bottom - card.top
        assertEquals(card.top + height * .18f, layout.titleY, 0.001f)
        assertEquals(card.top + height * .31f, layout.levelY, 0.001f)
        assertEquals(card.top + height * .44f, layout.incomeY, 0.001f)
        assertEquals(card.top + height * .57f, layout.managerY, 0.001f)
        assertEquals(card.top + height * .68f, layout.permanentY, 0.001f)
    }

    @Test
    fun zeroPermanentUpgradesProducesNoSlots() {
        val layout = BusinessCardLayout.calculate(card, permanentUpgradeCount = 0)
        assertTrue(layout.permanentUpgradeSlots.isEmpty())
    }
}
