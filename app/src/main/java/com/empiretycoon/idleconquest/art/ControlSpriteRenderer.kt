package com.empiretycoon.idleconquest.art

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import android.util.Base64

class ControlSpriteRenderer(private val context: Context) {
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG)
    private val columns = mapOf(
        "x1_off" to 0, "x1_on" to 1,
        "x10_off" to 2, "x10_on" to 3,
        "x25_off" to 4, "x25_on" to 5,
        "max_off" to 6, "max_on" to 7,
        "upgrade_off" to 8, "upgrade_on" to 9
    )
    private val atlas: Bitmap? by lazy {
        runCatching {
            val encoded = context.assets.open("art/ui/raster/controls_atlas.b64").bufferedReader().use { it.readText().trim() }
            val bytes = Base64.decode(encoded, Base64.DEFAULT)
            BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
        }.getOrNull()
    }
    fun draw(c: Canvas, r: RectF, id: String) {
        val bmp = atlas ?: return
        val col = columns[id] ?: return
        val w = bmp.width / 10
        c.drawBitmap(bmp, Rect(col*w, 0, (col+1)*w, bmp.height), r, paint)
    }
}
