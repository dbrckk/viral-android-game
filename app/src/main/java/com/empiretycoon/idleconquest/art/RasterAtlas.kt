package com.empiretycoon.idleconquest.art

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF

/** Shared drawer for horizontal raster sprite atlases backed by the process-wide raster asset cache. */
class RasterAtlas(
    context: Context,
    private val assetPath: String,
    private val columnCount: Int,
) {
    init { require(columnCount > 0) }

    private val appContext = context.applicationContext
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG)
    private val bitmap: Bitmap? by lazy { RasterAssetLoader.load(appContext, assetPath) }

    fun drawColumn(canvas: Canvas, destination: RectF, column: Int): Boolean {
        val bmp = bitmap ?: return false
        if (column !in 0 until columnCount) return false
        val cellWidth = bmp.width / columnCount
        if (cellWidth <= 0) return false
        val src = Rect(column * cellWidth, 0, (column + 1) * cellWidth, bmp.height)
        canvas.drawBitmap(bmp, src, destination, paint)
        return true
    }
}
