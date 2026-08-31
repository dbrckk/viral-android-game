package com.empiretycoon.idleconquest.art

import android.content.Context
import android.graphics.Canvas
import android.graphics.RectF

class WorkshopRasterRenderer(context: Context) {
    private val tiers = mapOf("base" to 0, "lv25" to 1, "lv100" to 2, "lv250" to 3, "lv500" to 4, "master" to 5)
    private val atlas = RasterAtlas(context, "art/business/group_01/raster/workshop_tiers.b64", 6)

    fun draw(canvas: Canvas, rect: RectF, tier: String): Boolean {
        return atlas.drawColumn(canvas, rect, tiers[tier] ?: return false)
    }
}
