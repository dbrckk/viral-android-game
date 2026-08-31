package com.empiretycoon.idleconquest.art

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import com.empiretycoon.idleconquest.game.ManagerDefinition

class ManagerPortraitRenderer(private val context: Context) {
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG)
    private val stateOverlay = CardStateOverlayRenderer(context)

    private fun assetState(state: String): String = when (state) {
        "locked" -> "locked"
        "available" -> "available"
        "hired", "boosted" -> "hired"
        else -> "available"
    }

    private fun bitmap(managerId: String, state: String) =
        RasterAssetLoader.load(context, "art/managers/raster/${managerId}__${assetState(state)}.webp")

    fun draw(canvas: Canvas, rect: RectF, manager: ManagerDefinition, state: String) {
        bitmap(manager.id, state)?.let { canvas.drawBitmap(it, null, rect, paint) }
        val overlayState = when (state) {
            "locked" -> "locked"
            "available" -> "available"
            "hired", "boosted" -> "active"
            else -> "available"
        }
        stateOverlay.draw(canvas, rect, overlayState)
    }
}
