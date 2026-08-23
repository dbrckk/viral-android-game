package com.empiretycoon.idleconquest.art

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import android.util.Base64

class StatusBadgeRenderer(private val context: Context) {
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG)
    private val columns = mapOf(
        "milestone" to 0,
        "upgrade_ready" to 1,
        "selected" to 2,
        "new" to 3,
        "limited_time" to 4,
        "hot" to 5,
        "not_available" to 6,
        "completed" to 7
    )
    private val atlas: Bitmap? by lazy {
        runCatching {
            val encoded = context.assets.open("art/ui/raster/status_badges.b64")
                .bufferedReader().use { it.readText().trim() }
            val bytes = Base64.decode(encoded, Base64.DEFAULT)
            BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
        }.getOrNull()
    }
    fun draw(canvas: Canvas, rect: RectF, id: String): Boolean {
        val bmp = atlas ?: return false
        val col = columns[id] ?: return false
        val w = bmp.width / 8
        canvas.drawBitmap(bmp, Rect(col*w, 0, (col+1)*w, bmp.height), rect, paint)
        return true
    }
}
