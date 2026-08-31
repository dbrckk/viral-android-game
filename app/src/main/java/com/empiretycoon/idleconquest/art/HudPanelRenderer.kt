package com.empiretycoon.idleconquest.art

import android.content.Context
import android.graphics.Canvas
import android.graphics.RectF

class HudPanelRenderer(context: Context) {
    private val columns = mapOf("hud" to 0, "missions" to 1, "banner" to 2)
    private val statChips = HudStatChipRenderer(context)
    private val feedbackIcons = FeedbackIconRenderer(context)
    private val atlas = RasterAtlas(context, "art/ui/raster/hud_panels.b64", 3)

    fun draw(canvas: Canvas, rect: RectF, id: String): Boolean {
        val col = columns[id] ?: return false
        if (!atlas.drawColumn(canvas, rect, col)) return false
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
