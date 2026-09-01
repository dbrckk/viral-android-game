package com.empiretycoon.idleconquest.ui

object HorizontalSlotLayout {
    fun calculate(bounds: UiBounds, count: Int, gap: Float): List<UiBounds> {
        val safeCount = count.coerceAtLeast(0)
        if (safeCount == 0) return emptyList()

        val safeGap = gap.coerceAtLeast(0f)
        val width = (bounds.right - bounds.left).coerceAtLeast(0f)
        val totalGap = safeGap * (safeCount - 1)
        val slotWidth = ((width - totalGap) / safeCount).coerceAtLeast(0f)

        return List(safeCount) { index ->
            val left = bounds.left + index * (slotWidth + safeGap)
            UiBounds(left, bounds.top, left + slotWidth, bounds.bottom)
        }
    }
}
