package com.empiretycoon.idleconquest.art

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import android.util.Base64

class PrestigeIconRenderer(private val context: Context) {
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG)

    private val atlas: Bitmap? by lazy {
        runCatching {
            val encoded = context.assets.open("art/prestige/raster/prestige_atlas.b64")
                .bufferedReader()
                .use { it.readText().trim() }
            val bytes = Base64.decode(encoded, Base64.DEFAULT)
            BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
        }.getOrNull()
    }

    private val stateColumns = mapOf(
        "locked" to 0,
        "ready" to 1,
        "prestiging" to 2,
        "owned" to 3
    )

    fun draw(c: Canvas, r: RectF, state: String, crowns: Int) {
        val bmp = atlas ?: return
        val col = stateColumns[state] ?: 0
        val cellW = bmp.width / 4
        val src = Rect(col * cellW, 0, (col + 1) * cellW, bmp.height)
        c.drawBitmap(bmp, src, r, paint)
    }
}
