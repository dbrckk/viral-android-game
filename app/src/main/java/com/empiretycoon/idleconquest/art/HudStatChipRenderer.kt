package com.empiretycoon.idleconquest.art

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import android.util.Base64

class HudStatChipRenderer(private val context: Context) {
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG)
    private val columns = mapOf("cash" to 0, "income" to 1, "gem" to 2, "prestige" to 3, "reward" to 4)
    private val atlas: Bitmap? by lazy {
        runCatching {
            val encoded = context.assets.open("art/ui/raster/hud_stat_chips.b64").bufferedReader().use { it.readText().trim() }
            val bytes = Base64.decode(encoded, Base64.DEFAULT)
            BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
        }.getOrNull()
    }

    fun draw(canvas: Canvas, rect: RectF, id: String): Boolean {
        val bmp = atlas ?: return false
        val col = columns[id] ?: return false
        val w = bmp.width / 5
        canvas.drawBitmap(bmp, Rect(col*w, 0, (col+1)*w, bmp.height), rect, paint)
        return true
    }
}
