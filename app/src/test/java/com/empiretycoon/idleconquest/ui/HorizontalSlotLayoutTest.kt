package com.empiretycoon.idleconquest.ui

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class HorizontalSlotLayoutTest {
    @Test
    fun calculatesEvenSlotsWithGaps() {
        val slots = HorizontalSlotLayout.calculate(UiBounds(0f, 10f, 100f, 30f), 4, 4f)

        assertEquals(4, slots.size)
        assertEquals(22f, slots[0].right - slots[0].left, 0.001f)
        assertEquals(4f, slots[1].left - slots[0].right, 0.001f)
        assertEquals(100f, slots.last().right, 0.001f)
        assertTrue(slots.all { it.top == 10f && it.bottom == 30f })
    }

    @Test
    fun zeroCountReturnsNoSlots() {
        assertTrue(HorizontalSlotLayout.calculate(UiBounds(0f, 0f, 100f, 20f), 0, 5f).isEmpty())
    }

    @Test
    fun negativeGapIsClampedToZero() {
        val slots = HorizontalSlotLayout.calculate(UiBounds(0f, 0f, 90f, 20f), 3, -5f)
        assertEquals(30f, slots[0].right - slots[0].left, 0.001f)
        assertEquals(slots[0].right, slots[1].left, 0.001f)
    }

    @Test
    fun impossibleWidthNeverProducesNegativeSlotWidth() {
        val slots = HorizontalSlotLayout.calculate(UiBounds(0f, 0f, 10f, 20f), 4, 8f)
        assertTrue(slots.all { it.right >= it.left })
    }
}
