package com.empiretycoon.idleconquest.art

import android.content.Context
import android.graphics.Canvas
import android.graphics.RectF

class BusinessStatIconRenderer(context: Context) {
    private val columns = mapOf("level" to 0, "income" to 1, "manager" to 2, "permanent" to 3)
    private val atlas = RasterAtlas(context, "art/ui/raster/business_stat_icons.b64", 4)

    fun draw(canvas: Canvas, rect: RectF, id: String): Boolean {
        return atlas.drawColumn(canvas, rect, columns[id] ?: return false)
    }
}
