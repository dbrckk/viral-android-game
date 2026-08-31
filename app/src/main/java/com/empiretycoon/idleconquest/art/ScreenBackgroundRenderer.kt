package com.empiretycoon.idleconquest.art

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF

class ScreenBackgroundRenderer(context: Context) {
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG)
    private val bitmap by lazy {
        RasterAssetLoader.load(context, "art/ui/raster/screen_background.b64")
    }

    fun draw(canvas: Canvas, width: Int, height: Int) {
        val bmp = bitmap ?: return
        canvas.drawBitmap(bmp, null, RectF(0f, 0f, width.toFloat(), height.toFloat()), paint)
    }
}
