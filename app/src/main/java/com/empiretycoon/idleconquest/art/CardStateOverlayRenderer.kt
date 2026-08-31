package com.empiretycoon.idleconquest.art

import android.content.Context
import android.graphics.Canvas
import android.graphics.RectF

class CardStateOverlayRenderer(context: Context) {
    private val columns = mapOf("locked" to 0, "available" to 1, "active" to 2)
    private val atlas = RasterAtlas(context, "art/ui/raster/card_state_overlays.b64", 3)

    fun draw(canvas: Canvas, destination: RectF, state: String): Boolean {
        val column = columns[state] ?: return false
        val size = destination.width().coerceAtMost(destination.height()) * .34f
        val target = RectF(
            destination.right - size,
            destination.top,
            destination.right,
            destination.top + size,
        )
        return atlas.drawColumn(canvas, target, column)
    }
}
