package com.empiretycoon.idleconquest.ui

data class BusinessCardGeometry(
    val art: UiBounds,
    val manager: UiBounds,
    val permanentUpgradeSlots: List<UiBounds>,
    val upgradeButton: UiBounds,
    val contentLeft: Float,
    val textLeft: Float,
    val statIconSize: Float,
    val titleY: Float,
    val levelY: Float,
    val incomeY: Float,
    val managerY: Float,
    val permanentY: Float,
)

object BusinessCardLayout {
    fun calculate(card: UiBounds, permanentUpgradeCount: Int = 2): BusinessCardGeometry {
        val width = card.right - card.left
        val height = card.bottom - card.top
        val inset = 10f
        val contentLeft = card.left + width * .28f
        val rightColumnLeft = card.right - width * .19f
        val statIconSize = height * .085f

        val art = UiBounds(
            card.left + inset,
            card.top + inset,
            card.left + width * .24f,
            card.bottom - inset,
        )
        val manager = UiBounds(
            rightColumnLeft,
            card.top + inset,
            card.right - inset,
            card.top + height * .45f,
        )

        val safeUpgradeCount = permanentUpgradeCount.coerceAtLeast(0)
        val upgradeGap = 5f
        val upgradesTop = card.top + height * .49f
        val availableHeight = card.bottom - inset - upgradesTop
        val totalGaps = upgradeGap * (safeUpgradeCount - 1).coerceAtLeast(0)
        val slotHeight = if (safeUpgradeCount > 0) {
            (availableHeight - totalGaps) / safeUpgradeCount
        } else {
            0f
        }
        val permanentUpgradeSlots = List(safeUpgradeCount) { index ->
            val top = upgradesTop + index * (slotHeight + upgradeGap)
            UiBounds(rightColumnLeft, top, card.right - inset, top + slotHeight)
        }

        val upgradeButton = UiBounds(
            contentLeft,
            card.bottom - height * .22f,
            rightColumnLeft - 8f,
            card.bottom - inset,
        )

        return BusinessCardGeometry(
            art = art,
            manager = manager,
            permanentUpgradeSlots = permanentUpgradeSlots,
            upgradeButton = upgradeButton,
            contentLeft = contentLeft,
            textLeft = contentLeft + statIconSize + 5f,
            statIconSize = statIconSize,
            titleY = card.top + height * .18f,
            levelY = card.top + height * .31f,
            incomeY = card.top + height * .44f,
            managerY = card.top + height * .57f,
            permanentY = card.top + height * .68f,
        )
    }
}
