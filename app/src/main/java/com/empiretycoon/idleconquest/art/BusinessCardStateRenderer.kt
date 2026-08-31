package com.empiretycoon.idleconquest.art

import android.content.Context
import android.graphics.Canvas
import android.graphics.RectF

class BusinessCardStateRenderer(context: Context) {
    private val statusBadges = StatusBadgeRenderer(context)
    private val columns = mapOf("normal" to 0, "milestone" to 1, "upgrade_ready" to 2)
    private val atlas = RasterAtlas(context, "art/ui/raster/business_card_states.b64", 3)

    fun draw(canvas: Canvas, rect: RectF, state: String): Boolean {
        val col = columns[state] ?: return false
        if (!atlas.drawColumn(canvas, rect, col)) return false
        if (state == "upgrade_ready") {
            val s = rect.width() * .20f
            statusBadges.draw(canvas, RectF(rect.right - s, rect.top, rect.right, rect.top + s), "upgrade_ready")
        }
        return true
    }
}
