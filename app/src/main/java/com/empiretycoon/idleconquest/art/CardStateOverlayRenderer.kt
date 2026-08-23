package com.empiretycoon.idleconquest.art

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import android.util.Base64

class CardStateOverlayRenderer(private val context: Context) {
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG)
    private val columns = mapOf("locked" to 0, "available" to 1, "active" to 2)
    private val atlas: Bitmap? by lazy {
        runCatching {
            val encoded = context.assets.open("art/ui/raster/card_state_overlays.b64")
                .bufferedReader().use { it.readText().trim() }
            val bytes = Base64.decode(encoded, Base64.DEFAULT)
            BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
        }.getOrNull()
    }

    fun draw(canvas: Canvas, destination: RectF, state: String): Boolean {
        val bmp = atlas ?: return false
        val col = columns[state] ?: return false
        val w = bmp.width / 3
        val s = destination.width().coerceAtMost(destination.height()) * .34f
        val dst = RectF(destination.right - s, destination.top, destination.right, destination.top + s)
        canvas.drawBitmap(bmp, Rect(col*w, 0, (col+1)*w, bmp.height), dst, paint)
        return true
    }
}
