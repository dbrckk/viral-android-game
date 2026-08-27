package com.empiretycoon.idleconquest.art

import android.content.Context
import android.graphics.Canvas
import android.graphics.RectF

class CurrencyIconRenderer(context: Context) {
    private val columns = mapOf("cash" to 0, "coin" to 1, "gem" to 2, "crown" to 3, "reward" to 4)
    private val atlas = RasterAtlas(context, "art/currency/raster/currency_atlas.b64", 5)

    fun draw(c: Canvas, r: RectF, id: String) {
        atlas.drawColumn(c, r, columns[id] ?: return)
    }
}
