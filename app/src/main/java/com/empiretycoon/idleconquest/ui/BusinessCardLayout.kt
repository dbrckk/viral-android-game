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
        val width = (card.right - card.left).coerceAtLeast(0f)
        val height = (card.bottom - card.top).coerceAtLeast(0f)
        val horizontalInset = minOf(10f, width / 2f)
        val verticalInset = minOf(10f, height / 2f)
        val contentLeft = card.left + width * .28f
        val rightColumnLeft = card.left + width * .81f
        val statIconSize = height * .085f

        val artLeft = card.left + horizontalInset
        val art = UiBounds(
            artLeft,
            card.top + verticalInset,
            maxOf(artLeft, card.left + width * .24f),
            maxOf(card.top + verticalInset, card.top + height - verticalInset),
        )
        val managerTop = card.top + verticalInset
        val manager = UiBounds(
            rightColumnLeft,
            managerTop,
            maxOf(rightColumnLeft, card.left + width - horizontalInset),
            maxOf(managerTop, card.top + height * .45f),
        )

        val safeUpgradeCount = permanentUpgradeCount.coerceAtLeast(0)
        val preferredUpgradeGap = 5f
        val upgradesTop = card.top + height * .49f
        val upgradesBottom = (card.top + height - verticalInset).coerceAtLeast(upgradesTop)
        val availableHeight = upgradesBottom - upgradesTop
        val gapCount = (safeUpgradeCount - 1).coerceAtLeast(0)
        val upgradeGap = if (gapCount > 0) {
            minOf(preferredUpgradeGap, availableHeight / gapCount)
        } else {
            0f
        }
        val totalGaps = upgradeGap * gapCount
        val slotHeight = if (safeUpgradeCount > 0) {
            ((availableHeight - totalGaps) / safeUpgradeCount).coerceAtLeast(0f)
        } else {
            0f
        }
        val permanentUpgradeSlots = List(safeUpgradeCount) { index ->
            val top = upgradesTop + index * (slotHeight + upgradeGap)
            UiBounds(
                rightColumnLeft,
                top,
                maxOf(rightColumnLeft, card.left + width - horizontalInset),
                minOf(upgradesBottom, top + slotHeight),
            )
        }

        val upgradeTop = card.top + height * .78f
        val upgradeRight = maxOf(contentLeft, rightColumnLeft - minOf(8f, width * .02f))
        val upgradeButton = UiBounds(
            contentLeft,
            upgradeTop,
            upgradeRight,
            maxOf(upgradeTop, card.top + height - verticalInset),
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
