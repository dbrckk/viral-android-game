package com.empiretycoon.idleconquest.ui

data class UiBounds(
    val left: Float,
    val top: Float,
    val right: Float,
    val bottom: Float,
) {
    val width: Float get() = (right - left).coerceAtLeast(0f)
    val height: Float get() = (bottom - top).coerceAtLeast(0f)
}

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
    ): ShowcaseFrameLayout = frame(
        contentBounds = UiBounds(
            left = 0f,
            top = 0f,
            right = width.coerceAtLeast(0).toFloat(),
            bottom = height.coerceAtLeast(0).toFloat(),
        ),
        bannerVisible = bannerVisible,
        businessCount = businessCount,
    )

    fun frame(
        contentBounds: UiBounds,
        bannerVisible: Boolean,
        businessCount: Int,
    ): ShowcaseFrameLayout {
        val safeLeft = minOf(contentBounds.left, contentBounds.right)
        val safeTop = minOf(contentBounds.top, contentBounds.bottom)
        val safeRight = maxOf(contentBounds.left, contentBounds.right)
        val safeBottom = maxOf(contentBounds.top, contentBounds.bottom)
        val safeWidth = (safeRight - safeLeft).coerceAtLeast(0f)
        val safeHeight = (safeBottom - safeTop).coerceAtLeast(0f)
        val margin = minOf(safeWidth, safeHeight) * .04f
        val hudHeight = safeHeight * .09f
        val modeHeight = safeHeight * .045f
        val missionHeight = safeHeight * .07f
        val left = safeLeft + margin
        val right = (safeRight - margin).coerceAtLeast(left)

        val hudTop = safeTop + margin
        val hud = UiBounds(left, hudTop, right, hudTop + hudHeight)
        val modesTop = hudTop + hudHeight + 5f
        val modes = UiBounds(left, modesTop, right, modesTop + modeHeight)
        val missionsTop = hudTop + hudHeight + modeHeight + 10f
        val missions = UiBounds(left, missionsTop, right, missionsTop + missionHeight)

        val bannerHeight = if (bannerVisible) safeHeight * .038f else 0f
        val banner = if (bannerVisible) {
            val top = hudTop + hudHeight + modeHeight + missionHeight + 16f
            UiBounds(left, top, right, top + bannerHeight)
        } else {
            null
        }

        val cardsTop = hudTop + hudHeight + modeHeight + missionHeight + margin + bannerHeight
        val safeCount = businessCount.coerceAtLeast(0)
        val availableHeight = (safeBottom - cardsTop - margin).coerceAtLeast(0f)
        val gapCount = (safeCount - 1).coerceAtLeast(0)
        val preferredGap = margin * .4f
        val gap = if (gapCount > 0) {
            minOf(preferredGap, availableHeight / gapCount)
        } else {
            0f
        }
        val totalGaps = gap * gapCount
        val cardHeight = if (safeCount > 0) {
            ((availableHeight - totalGaps) / safeCount).coerceAtLeast(0f)
        } else {
            0f
        }
        val businessCards = List(safeCount) { index ->
            val top = cardsTop + index * (cardHeight + gap)
            UiBounds(left, top, right, minOf(safeBottom - margin, top + cardHeight))
        }

        return ShowcaseFrameLayout(hud, modes, missions, banner, businessCards)
    }
}
