package com.empiretycoon.idleconquest.art

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF

/** Draws the six authored Factory tier sprites from the raster atlas. */
class FactoryRasterRenderer(context: Context) {
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG)
    private val tierColumns = mapOf(
        "base" to 0,
        "lv25" to 1,
        "lv100" to 2,
        "lv250" to 3,
        "lv500" to 4,
        "master" to 5
    )
    private val atlas by lazy {
        RasterAssetLoader.load(context, "art/business/group_01/raster/factory_tiers.b64")
    }

    fun draw(canvas: Canvas, rect: RectF, tier: String): Boolean {
        val bitmap = atlas ?: return false
        val column = tierColumns[tier] ?: return false
        val cellWidth = bitmap.width / 6
        val source = Rect(column * cellWidth, 0, (column + 1) * cellWidth, bitmap.height)
        canvas.drawBitmap(bitmap, source, rect, paint)
        return true
    }
}
