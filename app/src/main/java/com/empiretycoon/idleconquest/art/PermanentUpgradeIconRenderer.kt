package com.empiretycoon.idleconquest.art

import android.content.Context
import android.graphics.Canvas
import android.graphics.RectF

class PermanentUpgradeIconRenderer(context: Context) {
    private val stateOverlay = CardStateOverlayRenderer(context)
    private val ids = listOf(
        "street_solar_grill",
        "street_bulk_supply",
        "shop_holo_signage",
        "shop_auto_stock",
        "workshop_plasma_tools",
        "workshop_modular_lines",
        "factory_quantum_core",
        "factory_predictive_ai"
    )
    private val states = listOf("locked", "available", "purchased")
    private val atlas = RasterAtlas(
        context,
        "art/upgrades/raster/permanent_upgrades_atlas.webp",
        ids.size,
        states.size,
    )

    fun draw(canvas: Canvas, destination: RectF, id: String, state: String) {
        val column = ids.indexOf(id)
        if (column < 0) return
        val row = states.indexOf(state).takeIf { it >= 0 } ?: 0
        if (!atlas.drawCell(canvas, destination, column, row)) return

        val overlayState = when (state) {
            "locked" -> "locked"
            "available" -> "available"
            "purchased" -> "active"
            else -> "available"
        }
        stateOverlay.draw(canvas, destination, overlayState)
    }
}
