package com.empiretycoon.idleconquest.ui

object HorizontalSlotLayout {
    fun calculate(bounds: UiBounds, count: Int, gap: Float): List<UiBounds> {
        val safeCount = count.coerceAtLeast(0)
        if (safeCount == 0) return emptyList()

        val width = (bounds.right - bounds.left).coerceAtLeast(0f)
        val requestedGap = gap.coerceAtLeast(0f)
        val safeGap = if (safeCount > 1) {
            minOf(requestedGap, width / (safeCount - 1))
        } else {
            0f
        }
        val totalGap = safeGap * (safeCount - 1)
        val slotWidth = ((width - totalGap) / safeCount).coerceAtLeast(0f)

        return List(safeCount) { index ->
            val left = bounds.left + index * (slotWidth + safeGap)
            UiBounds(left, bounds.top, minOf(bounds.right, left + slotWidth), bounds.bottom)
        }
    }
}
