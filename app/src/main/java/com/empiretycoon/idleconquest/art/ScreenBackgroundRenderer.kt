package com.empiretycoon.idleconquest.art

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.util.Base64

class ScreenBackgroundRenderer(private val context: Context) {
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG)
    private val bitmap: Bitmap? by lazy {
        runCatching {
            val encoded = context.assets.open("art/ui/raster/screen_background.b64")
                .bufferedReader().use { it.readText().trim() }
            val bytes = Base64.decode(encoded, Base64.DEFAULT)
            BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
        }.getOrNull()
    }

    fun draw(canvas: Canvas, width: Int, height: Int) {
        val bmp = bitmap ?: return
        canvas.drawBitmap(bmp, null, RectF(0f, 0f, width.toFloat(), height.toFloat()), paint)
    }
}
