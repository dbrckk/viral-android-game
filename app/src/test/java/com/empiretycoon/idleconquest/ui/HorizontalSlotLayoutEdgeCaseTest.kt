package com.empiretycoon.idleconquest.ui

import org.junit.Assert.assertTrue
import org.junit.Test

class HorizontalSlotLayoutEdgeCaseTest {
    @Test
    fun excessiveRequestedGapsAreReducedToFitBounds() {
        val bounds = UiBounds(0f, 0f, 10f, 20f)
        val slots = HorizontalSlotLayout.calculate(bounds, 4, 8f)

        assertTrue(slots.all { it.left >= bounds.left && it.right <= bounds.right })
        assertTrue(slots.all { it.right >= it.left })
        assertTrue(slots.zipWithNext().all { (a, b) -> b.left >= a.right })
    }

    @Test
    fun singleSlotConsumesAvailableWidthWithoutGap() {
        val bounds = UiBounds(5f, 2f, 25f, 12f)
        val slot = HorizontalSlotLayout.calculate(bounds, 1, 100f).single()

        assertTrue(slot.left == bounds.left)
        assertTrue(slot.right == bounds.right)
    }
}
