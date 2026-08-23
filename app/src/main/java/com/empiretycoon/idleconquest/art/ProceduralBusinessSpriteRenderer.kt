package com.empiretycoon.idleconquest.art

import android.content.Context
import android.graphics.Canvas
import android.graphics.RectF

/** Routes every business to its authored raster tier atlas. */
class ProceduralBusinessSpriteRenderer(context: Context) {
    private val streetRaster = StreetStandRasterRenderer(context)
    private val cornerRaster = CornerShopRasterRenderer(context)
    private val workshopRaster = WorkshopRasterRenderer(context)
    private val factoryRaster = FactoryRasterRenderer(context)

    fun draw(canvas: Canvas, rect: RectF, businessId: String, tier: String, state: String) {
        when (businessId) {
            "street_stand" -> streetRaster.draw(canvas, rect, tier)
            "corner_shop" -> cornerRaster.draw(canvas, rect, tier)
            "workshop" -> workshopRaster.draw(canvas, rect, tier)
            "factory" -> factoryRaster.draw(canvas, rect, tier)
        }
    }
}
