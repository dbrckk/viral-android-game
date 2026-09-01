package com.empiretycoon.idleconquest.ui

data class UiBounds(
    val left: Float,
    val top: Float,
    val right: Float,
    val bottom: Float,
)

data class ShowcaseFrameLayout(
    val hud: UiBounds,
    val modes: UiBounds,
    val missions: UiBounds,
    val banner: UiBounds?,
    val businessCards: List<UiBounds>,
)

object ShowcaseLayout {
    fun frame(
        width: Int,
        height: Int,
        bannerVisible: Boolean,
        businessCount: Int,
    ): ShowcaseFrameLayout {
        val safeWidth = width.coerceAtLeast(0)
        val safeHeight = height.coerceAtLeast(0)
        val margin = safeWidth * .04f
        val hudHeight = safeHeight * .09f
        val modeHeight = safeHeight * .045f
        val missionHeight = safeHeight * .07f
        val right = (safeWidth - margin).coerceAtLeast(margin)

        val hud = UiBounds(margin, margin, right, margin + hudHeight)
        val modesTop = margin + hudHeight + 5f
        val modes = UiBounds(margin, modesTop, right, modesTop + modeHeight)
        val missionsTop = margin + hudHeight + modeHeight + 10f
        val missions = UiBounds(margin, missionsTop, right, missionsTop + missionHeight)

        val bannerHeight = if (bannerVisible) safeHeight * .038f else 0f
        val banner = if (bannerVisible) {
            val top = margin + hudHeight + modeHeight + missionHeight + 16f
            UiBounds(margin, top, right, top + bannerHeight)
        } else {
            null
        }

        val cardsTop = margin + hudHeight + modeHeight + missionHeight + margin + bannerHeight
        val gap = margin * .4f
        val safeCount = businessCount.coerceAtLeast(0)
        val totalGaps = gap * (safeCount - 1).coerceAtLeast(0)
        val cardHeight = if (safeCount > 0) {
            ((safeHeight - cardsTop - margin - totalGaps) / safeCount).coerceAtLeast(0f)
        } else {
            0f
        }
        val businessCards = List(safeCount) { index ->
            val top = cardsTop + index * (cardHeight + gap)
            UiBounds(margin, top, right, top + cardHeight)
        }

        return ShowcaseFrameLayout(hud, modes, missions, banner, businessCards)
    }
}
