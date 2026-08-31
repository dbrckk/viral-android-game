package com.empiretycoon.idleconquest.art

import android.content.Context
import android.graphics.Canvas
import android.graphics.RectF

class PrestigeIconRenderer(context: Context) {
    private val stateColumns = mapOf("locked" to 0, "ready" to 1, "prestiging" to 2, "owned" to 3)
    private val effectColumns = mapOf(
        "crown_burst" to 0,
        "energy_ring" to 1,
        "ascending_particles" to 2,
        "reset_flash" to 3,
        "crown_trail" to 4
    )
    private val atlas = RasterAtlas(context, "art/prestige/raster/prestige_atlas.b64", 4)
    private val effects = RasterAtlas(context, "art/prestige/raster/prestige_effects_atlas.webp", 5)

    fun draw(c: Canvas, r: RectF, state: String, crowns: Int) {
        when (state) {
            "ready" -> drawEffect(c, r, "energy_ring")
            "prestiging" -> {
                drawEffect(c, r, "reset_flash")
                drawEffect(c, r, "ascending_particles")
                drawEffect(c, r, "crown_burst")
            }
            "owned" -> drawEffect(c, r, "crown_trail")
        }

        atlas.drawColumn(c, r, stateColumns[state] ?: 0)
    }

    private fun drawEffect(c: Canvas, r: RectF, effect: String) {
        val column = effectColumns[effect] ?: return
        val pad = r.width() * .12f
        val dst = RectF(r.left - pad, r.top - pad, r.right + pad, r.bottom + pad)
        effects.drawColumn(c, dst, column)
    }
}
