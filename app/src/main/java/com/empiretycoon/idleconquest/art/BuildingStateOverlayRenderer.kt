package com.empiretycoon.idleconquest.art

import android.content.Context
import android.graphics.Canvas
import android.graphics.RectF

class BuildingStateOverlayRenderer(context: Context) {
    private val cardStates = BusinessCardStateRenderer(context)
    private val columns = mapOf("milestone_glow" to 0, "milestone_badge" to 1, "selected" to 2, "upgrade_glow" to 3, "upgrade_arrow" to 4)
    private val atlas = RasterAtlas(context, "art/business/states/raster/building_state_overlays.b64", 5)

    fun draw(canvas: Canvas, rect: RectF, id: String): Boolean {
        return atlas.drawColumn(canvas, rect, columns[id] ?: return false)
    }

    fun drawMilestone(canvas: Canvas, rect: RectF) {
        cardStates.draw(canvas, rect, "milestone")
        draw(canvas, rect, "milestone_glow")
        val size = rect.width() * .42f
        draw(
            canvas,
            RectF(rect.centerX() - size / 2, rect.top, rect.centerX() + size / 2, rect.top + size),
            "milestone_badge",
        )
    }
}
