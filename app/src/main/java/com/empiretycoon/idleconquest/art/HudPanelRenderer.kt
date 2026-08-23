package com.empiretycoon.idleconquest.art

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import android.util.Base64

class HudPanelRenderer(private val context: Context) {
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG)
    private val columns = mapOf("hud" to 0, "missions" to 1, "banner" to 2)
    private val statChips = HudStatChipRenderer(context)
    private val feedbackIcons = FeedbackIconRenderer(context)
    private val atlas: Bitmap? by lazy {
        runCatching {
            val encoded = context.assets.open("art/ui/raster/hud_panels.b64").bufferedReader().use { it.readText().trim() }
            val bytes = Base64.decode(encoded, Base64.DEFAULT)
            BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
        }.getOrNull()
    }

    fun draw(canvas: Canvas, rect: RectF, id: String): Boolean {
        val bmp = atlas ?: return false
        val col = columns[id] ?: return false
        val w = bmp.width / 3
        canvas.drawBitmap(bmp, Rect(col*w, 0, (col+1)*w, bmp.height), rect, paint)
        if (id == "hud") drawHudChips(canvas, rect)
        if (id == "banner") drawBannerFeedback(canvas, rect)
        return true
    }

    private fun drawHudChips(canvas: Canvas, rect: RectF) {
        val top = rect.top + rect.height() * .48f
        val bottom = rect.bottom - rect.height() * .08f
        val cash = RectF(rect.left + rect.width()*.015f, top, rect.left + rect.width()*.27f, bottom)
        val income = RectF(rect.left + rect.width()*.29f, top, rect.left + rect.width()*.53f, bottom)
        val gem = RectF(rect.left + rect.width()*.55f, top, rect.left + rect.width()*.72f, bottom)
        val prestige = RectF(rect.left + rect.width()*.73f, top, rect.left + rect.width()*.86f, bottom)
        statChips.draw(canvas, cash, "cash")
        statChips.draw(canvas, income, "income")
        statChips.draw(canvas, gem, "gem")
        statChips.draw(canvas, prestige, "prestige")
    }

    private fun drawBannerFeedback(canvas: Canvas, rect: RectF) {
        val size = rect.height() * .72f
        val iconRect = RectF(rect.left + rect.height()*.12f, rect.centerY()-size/2f, rect.left + rect.height()*.12f + size, rect.centerY()+size/2f)
        feedbackIcons.draw(canvas, iconRect, "confirm")
    }
}
