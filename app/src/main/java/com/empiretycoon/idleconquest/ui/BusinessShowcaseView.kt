package com.empiretycoon.idleconquest.ui

import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.view.View
import com.empiretycoon.idleconquest.art.BusinessArtResolver

class BusinessShowcaseView(context: Context) : View(context) {
    private val resolver = BusinessArtResolver(context)
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val businesses = listOf("street_stand", "corner_shop", "workshop", "factory")

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawColor(Color.rgb(8, 10, 24))

        val margin = width * 0.06f
        val cardWidth = width - margin * 2f
        val cardHeight = (height - margin * 5f) / 4f

        businesses.forEachIndexed { index, id ->
            val top = margin + index * (cardHeight + margin)
            val rect = RectF(margin, top, margin + cardWidth, top + cardHeight)
            drawBusinessCard(canvas, rect, id)
        }
    }

    private fun drawBusinessCard(canvas: Canvas, rect: RectF, businessId: String) {
        val selection = resolver.resolve(businessId)

        paint.style = Paint.Style.FILL
        paint.color = Color.rgb(15, 20, 42)
        canvas.drawRoundRect(rect, 28f, 28f, paint)

        val contentInset = 18f
        val artRect = RectF(
            rect.left + contentInset,
            rect.top + contentInset,
            rect.left + rect.width() * 0.42f,
            rect.bottom - contentInset
        )

        if (selection.layerPaths.isNotEmpty()) {
            selection.layerPaths.forEach { path ->
                context.assets.open(path).use { input ->
                    BitmapFactory.decodeStream(input)?.let { bitmap ->
                        canvas.drawBitmap(bitmap, null, artRect, paint)
                    }
                }
            }
        } else {
            drawFallbackArt(canvas, artRect, selection.accent)
        }

        paint.color = Color.WHITE
        paint.textSize = 34f
        paint.isFakeBoldText = true
        canvas.drawText(labelFor(businessId), rect.left + rect.width() * 0.47f, rect.top + 52f, paint)

        paint.isFakeBoldText = false
        paint.textSize = 24f
        paint.color = Color.rgb(180, 190, 220)
        canvas.drawText("Tier: BASE", rect.left + rect.width() * 0.47f, rect.top + 90f, paint)
        canvas.drawText(
            if (selection.layerPaths.isEmpty()) "Art pipeline ready" else "Layered art loaded",
            rect.left + rect.width() * 0.47f,
            rect.top + 126f,
            paint
        )
    }

    private fun drawFallbackArt(canvas: Canvas, rect: RectF, accent: String) {
        paint.color = when {
            accent.startsWith("green") -> Color.rgb(82, 210, 126)
            accent.startsWith("blue") -> Color.rgb(72, 183, 255)
            accent.startsWith("orange") -> Color.rgb(255, 157, 72)
            accent.startsWith("violet") -> Color.rgb(196, 98, 255)
            else -> Color.LTGRAY
        }
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 7f
        canvas.drawRoundRect(rect, 22f, 22f, paint)

        paint.style = Paint.Style.FILL
        paint.alpha = 45
        canvas.drawRoundRect(rect, 22f, 22f, paint)
        paint.alpha = 255
    }

    private fun labelFor(id: String): String = when (id) {
        "street_stand" -> "Street Stand"
        "corner_shop" -> "Corner Shop"
        "workshop" -> "Workshop"
        "factory" -> "Factory"
        else -> id
    }
}
