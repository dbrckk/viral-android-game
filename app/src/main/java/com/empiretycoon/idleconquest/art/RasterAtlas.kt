package com.empiretycoon.idleconquest.art

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF

/** Shared drawer for raster sprite atlases backed by the process-wide raster asset cache. */
class RasterAtlas(
    context: Context,
    private val assetPath: String,
    private val columnCount: Int,
    private val rowCount: Int = 1,
) {
    init {
        require(columnCount > 0)
        require(rowCount > 0)
    }

    private val appContext = context.applicationContext
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG)

    fun drawColumn(canvas: Canvas, destination: RectF, column: Int): Boolean =
        drawCell(canvas, destination, column, 0)

    fun drawCell(canvas: Canvas, destination: RectF, column: Int, row: Int): Boolean {
        if (column !in 0 until columnCount || row !in 0 until rowCount) return false
        val bmp = RasterAssetLoader.load(appContext, assetPath) ?: return false

        val cellWidth = bmp.width / columnCount
        val cellHeight = bmp.height / rowCount
        if (cellWidth <= 0 || cellHeight <= 0) return false

        val src = Rect(
            column * cellWidth,
            row * cellHeight,
            (column + 1) * cellWidth,
            (row + 1) * cellHeight,
        )
        canvas.drawBitmap(bmp, src, destination, paint)
        return true
    }
}
