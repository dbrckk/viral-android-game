package com.empiretycoon.idleconquest.art

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import android.util.Base64

class WorkshopRasterRenderer(private val context: Context) {
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG)
    private val tiers = mapOf("base" to 0, "lv25" to 1, "lv100" to 2, "lv250" to 3, "lv500" to 4, "master" to 5)
    private val atlas: Bitmap? by lazy {
        runCatching {
            val encoded = context.assets.open("art/business/group_01/raster/workshop_tiers.b64")
                .bufferedReader().use { it.readText().trim() }
            val bytes = Base64.decode(encoded, Base64.DEFAULT)
            BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
        }.getOrNull()
    }

    fun draw(canvas: Canvas, rect: RectF, tier: String): Boolean {
        val bmp = atlas ?: return false
        val col = tiers[tier] ?: 0
        val cellW = bmp.width / 6
        canvas.drawBitmap(bmp, Rect(col * cellW, 0, (col + 1) * cellW, bmp.height), rect, paint)
        return true
    }
}
