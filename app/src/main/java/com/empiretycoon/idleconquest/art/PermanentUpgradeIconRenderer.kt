package com.empiretycoon.idleconquest.art

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF

class PermanentUpgradeIconRenderer(private val context: Context) {
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG)
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
    private val atlas: Bitmap by lazy {
        context.assets.open("art/upgrades/raster/permanent_upgrades_atlas.webp").use {
            BitmapFactory.decodeStream(it) ?: error("Unable to decode permanent upgrade sprite atlas")
        }
    }

    fun draw(canvas: Canvas, destination: RectF, id: String, state: String) {
        val column = ids.indexOf(id)
        if (column < 0) return
        val row = states.indexOf(state).takeIf { it >= 0 } ?: 0
        val cellWidth = atlas.width / ids.size
        val cellHeight = atlas.height / states.size
        val source = Rect(
            column * cellWidth,
            row * cellHeight,
            (column + 1) * cellWidth,
            (row + 1) * cellHeight
        )
        canvas.drawBitmap(atlas, source, destination, paint)
    }
}
