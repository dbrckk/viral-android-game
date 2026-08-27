package com.empiretycoon.idleconquest.art

import android.content.Context
import android.graphics.Canvas
import android.graphics.RectF

class HudStatChipRenderer(context: Context) {
    private val columns = mapOf("cash" to 0, "income" to 1, "gem" to 2, "prestige" to 3, "reward" to 4)
    private val atlas = RasterAtlas(context, "art/ui/raster/hud_stat_chips.b64", 5)

    fun draw(canvas: Canvas, rect: RectF, id: String): Boolean {
        return atlas.drawColumn(canvas, rect, columns[id] ?: return false)
    }
}
