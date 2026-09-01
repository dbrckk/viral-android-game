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
        val margin = width * .04f
        val hudHeight = height * .09f
        val modeHeight = height * .045f
        val missionHeight = height * .07f

        val hud = UiBounds(margin, margin, width - margin, margin + hudHeight)
        val modesTop = margin + hudHeight + 5f
        val modes = UiBounds(margin, modesTop, width - margin, modesTop + modeHeight)
        val missionsTop = margin + hudHeight + modeHeight + 10f
        val missions = UiBounds(margin, missionsTop, width - margin, missionsTop + missionHeight)

        val bannerHeight = if (bannerVisible) height * .038f else 0f
        val banner = if (bannerVisible) {
            val top = margin + hudHeight + modeHeight + missionHeight + 16f
            UiBounds(margin, top, width - margin, top + bannerHeight)
        } else {
            null
        }

        val cardsTop = margin + hudHeight + modeHeight + missionHeight + margin + bannerHeight
        val gap = margin * .4f
        val safeCount = businessCount.coerceAtLeast(0)
        val totalGaps = gap * (safeCount - 1).coerceAtLeast(0)
        val cardHeight = if (safeCount > 0) {
            (height - cardsTop - margin - totalGaps) / safeCount
        } else {
            0f
        }
        val businessCards = List(safeCount) { index ->
            val top = cardsTop + index * (cardHeight + gap)
            UiBounds(margin, top, width - margin, top + cardHeight)
        }

        return ShowcaseFrameLayout(hud, modes, missions, banner, businessCards)
    }
}
