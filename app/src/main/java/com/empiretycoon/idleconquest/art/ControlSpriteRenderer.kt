package com.empiretycoon.idleconquest.art

import android.content.Context
import android.graphics.Canvas
import android.graphics.RectF

class ControlSpriteRenderer(context: Context) {
    private val columns = mapOf(
        "x1_off" to 0, "x1_on" to 1,
        "x10_off" to 2, "x10_on" to 3,
        "x25_off" to 4, "x25_on" to 5,
        "max_off" to 6, "max_on" to 7,
        "upgrade_off" to 8, "upgrade_on" to 9,
    )
    private val atlas = RasterAtlas(context, "art/ui/raster/controls_atlas.b64", 10)

    fun draw(c: Canvas, r: RectF, id: String) {
        atlas.drawColumn(c, r, columns[id] ?: return)
    }
}
