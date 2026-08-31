package com.empiretycoon.idleconquest.art

import android.content.Context
import android.graphics.Canvas
import android.graphics.RectF

class FeedbackIconRenderer(context: Context) {
    private val columns = mapOf("success" to 0, "warning" to 1, "error" to 2, "confirm" to 3)
    private val atlas = RasterAtlas(context, "art/ui/raster/feedback_icons.b64", 4)

    fun draw(canvas: Canvas, rect: RectF, id: String): Boolean {
        return atlas.drawColumn(canvas, rect, columns[id] ?: return false)
    }
}
