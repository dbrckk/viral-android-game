package com.empiretycoon.idleconquest.art

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF

/** Draws the authored Empire Tycoon raster logo used in the HUD. */
class EmpireLogoRenderer(context: Context) {
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG)
    private val bitmap by lazy {
        RasterAssetLoader.load(context, "art/ui/raster/empire_tycoon_logo.b64")
    }

    fun draw(canvas: Canvas, rect: RectF): Boolean {
        val bmp = bitmap ?: return false
        canvas.drawBitmap(bmp, null, rect, paint)
        return true
    }
}
