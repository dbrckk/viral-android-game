package com.empiretycoon.idleconquest.art

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import android.util.Base64

/** Shared loader/drawer for horizontal raster sprite atlases. Supports Base64-wrapped and direct image assets. */
class RasterAtlas(
    private val context: Context,
    private val assetPath: String,
    private val columnCount: Int,
) {
    init { require(columnCount > 0) }

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG)
    private val bitmap: Bitmap? by lazy { loadBitmap() }

    fun drawColumn(canvas: Canvas, destination: RectF, column: Int): Boolean {
        val bmp = bitmap ?: return false
        if (column !in 0 until columnCount) return false
        val cellWidth = bmp.width / columnCount
        if (cellWidth <= 0) return false
        val src = Rect(column * cellWidth, 0, (column + 1) * cellWidth, bmp.height)
        canvas.drawBitmap(bmp, src, destination, paint)
        return true
    }

    private fun loadBitmap(): Bitmap? = runCatching {
        if (assetPath.endsWith(".b64", ignoreCase = true)) {
            val encoded = context.assets.open(assetPath).bufferedReader().use { it.readText().trim() }
            val bytes = Base64.decode(encoded, Base64.DEFAULT)
            BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
        } else {
            context.assets.open(assetPath).use(BitmapFactory::decodeStream)
        }
    }.getOrNull()
}
