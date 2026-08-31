package com.empiretycoon.idleconquest.art

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF

/** Draws resolved business art layers through the shared raster cache. */
class BusinessLayerRenderer(context: Context) {
    private val appContext = context.applicationContext
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG)

    fun draw(canvas: Canvas, destination: RectF, layerPaths: List<String>): Boolean {
        if (layerPaths.isEmpty()) return false

        var drewAny = false
        layerPaths.forEach { path ->
            RasterAssetLoader.load(appContext, path)?.let { bitmap ->
                canvas.drawBitmap(bitmap, null, destination, paint)
                drewAny = true
            }
        }
        return drewAny
    }
}
