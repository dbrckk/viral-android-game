package com.empiretycoon.idleconquest.art

import android.content.Context
import android.graphics.Canvas
import android.graphics.RectF

class MissionActionIconRenderer(context: Context) {
    private val columns = mapOf("cash" to 0, "gem" to 1, "claim" to 2, "locked" to 3)
    private val atlas = RasterAtlas(context, "art/missions/raster/mission_action_icons.b64", 4)

    fun draw(canvas: Canvas, rect: RectF, id: String): Boolean {
        return atlas.drawColumn(canvas, rect, columns[id] ?: return false)
    }
}
