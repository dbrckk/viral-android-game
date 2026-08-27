package com.empiretycoon.idleconquest.art

import android.content.Context
import android.graphics.Canvas
import android.graphics.RectF

class StatusBadgeRenderer(context: Context) {
    private val columns = mapOf(
        "milestone" to 0,
        "upgrade_ready" to 1,
        "selected" to 2,
        "new" to 3,
        "limited_time" to 4,
        "hot" to 5,
        "not_available" to 6,
        "completed" to 7,
    )
    private val atlas = RasterAtlas(context, "art/ui/raster/status_badges.b64", 8)

    fun draw(canvas: Canvas, rect: RectF, id: String): Boolean {
        return atlas.drawColumn(canvas, rect, columns[id] ?: return false)
    }
}
