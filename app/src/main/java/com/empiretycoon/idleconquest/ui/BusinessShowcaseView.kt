package com.empiretycoon.idleconquest.ui

import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.view.MotionEvent
import android.view.View
import com.empiretycoon.idleconquest.art.BusinessArtResolver
import com.empiretycoon.idleconquest.game.GameState
import java.util.Locale

class BusinessShowcaseView(context: Context) : View(context) {
    private val resolver = BusinessArtResolver(context)
    private val gameState = GameState()
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val cardRects = mutableListOf<RectF>()
    private val upgradeRects = mutableListOf<RectF>()
    private var lastFrameNanos = 0L

    init {
        isClickable = true
        keepScreenOn = true
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        updateEconomy()
        canvas.drawColor(Color.rgb(8, 10, 24))

        val margin = width * 0.045f
        val hudHeight = height * 0.12f
        drawHud(canvas, RectF(margin, margin, width - margin, margin + hudHeight))

        cardRects.clear()
        upgradeRects.clear()

        val topStart = margin + hudHeight + margin
        val availableHeight = height - topStart - margin
        val gap = margin * 0.75f
        val cardHeight = (availableHeight - gap * 3f) / 4f

        gameState.businesses.forEachIndexed { index, business ->
            val top = topStart + index * (cardHeight + gap)
            val rect = RectF(margin, top, width - margin, top + cardHeight)
            cardRects += rect
            drawBusinessCard(canvas, rect, index)
        }

        postInvalidateOnAnimation()
    }

    private fun updateEconomy() {
        val now = System.nanoTime()
        if (lastFrameNanos != 0L) {
            val delta = ((now - lastFrameNanos) / 1_000_000_000.0).coerceAtMost(0.25)
            gameState.tick(delta)
        }
        lastFrameNanos = now
    }

    private fun drawHud(canvas: Canvas, rect: RectF) {
        paint.style = Paint.Style.FILL
        paint.color = Color.rgb(13, 17, 36)
        canvas.drawRoundRect(rect, 30f, 30f, paint)

        paint.color = Color.rgb(245, 201, 92)
        paint.textSize = width * 0.045f
        paint.isFakeBoldText = true
        canvas.drawText("EMPIRE TYCOON", rect.left + 24f, rect.top + rect.height() * 0.38f, paint)

        paint.isFakeBoldText = false
        paint.textSize = width * 0.036f
        paint.color = Color.WHITE
        canvas.drawText("$ ${formatNumber(gameState.cash)}", rect.left + 24f, rect.bottom - 22f, paint)

        paint.color = Color.rgb(92, 230, 145)
        canvas.drawText("+${formatNumber(gameState.totalIncomePerSecond)}/s", rect.centerX() - 10f, rect.bottom - 22f, paint)

        paint.color = Color.rgb(120, 187, 255)
        paint.textAlign = Paint.Align.RIGHT
        canvas.drawText("◆ ${gameState.gems}", rect.right - 24f, rect.bottom - 22f, paint)
        paint.textAlign = Paint.Align.LEFT
    }

    private fun drawBusinessCard(canvas: Canvas, rect: RectF, index: Int) {
        val business = gameState.businesses[index]
        val tier = gameState.tierFor(business.level)
        val selection = resolver.resolve(business.id, tier = tier)

        paint.style = Paint.Style.FILL
        paint.color = Color.rgb(15, 20, 42)
        canvas.drawRoundRect(rect, 28f, 28f, paint)

        val contentInset = 16f
        val artRect = RectF(
            rect.left + contentInset,
            rect.top + contentInset,
            rect.left + rect.width() * 0.34f,
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

        val textLeft = rect.left + rect.width() * 0.39f
        paint.color = Color.WHITE
        paint.textSize = width * 0.038f
        paint.isFakeBoldText = true
        canvas.drawText(business.displayName, textLeft, rect.top + rect.height() * 0.24f, paint)

        paint.isFakeBoldText = false
        paint.textSize = width * 0.030f
        paint.color = Color.rgb(188, 198, 225)
        canvas.drawText("Lv. ${business.level}  •  ${tier.uppercase(Locale.US)}", textLeft, rect.top + rect.height() * 0.43f, paint)

        paint.color = Color.rgb(92, 230, 145)
        canvas.drawText("+${formatNumber(business.incomePerSecond)}/s", textLeft, rect.top + rect.height() * 0.61f, paint)

        val button = RectF(
            textLeft,
            rect.bottom - rect.height() * 0.30f,
            rect.right - contentInset,
            rect.bottom - contentInset
        )
        upgradeRects += button
        drawUpgradeButton(canvas, button, index)
    }

    private fun drawUpgradeButton(canvas: Canvas, rect: RectF, index: Int) {
        val business = gameState.businesses[index]
        val enabled = gameState.canUpgrade(index)

        paint.style = Paint.Style.FILL
        paint.color = if (enabled) Color.rgb(42, 166, 105) else Color.rgb(59, 68, 91)
        canvas.drawRoundRect(rect, 22f, 22f, paint)

        paint.color = Color.WHITE
        paint.textAlign = Paint.Align.CENTER
        paint.isFakeBoldText = true
        paint.textSize = width * 0.029f
        canvas.drawText(
            "UPGRADE  $ ${formatNumber(business.nextUpgradeCost)}",
            rect.centerX(),
            rect.centerY() + paint.textSize * 0.35f,
            paint
        )
        paint.textAlign = Paint.Align.LEFT
        paint.isFakeBoldText = false
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_UP) {
            val index = upgradeRects.indexOfFirst { it.contains(event.x, event.y) }
            if (index >= 0 && gameState.upgrade(index)) {
                performClick()
                invalidate()
            }
            return true
        }
        return true
    }

    override fun performClick(): Boolean {
        super.performClick()
        return true
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

    private fun formatNumber(value: Double): String = when {
        value >= 1_000_000_000_000.0 -> String.format(Locale.US, "%.2fT", value / 1_000_000_000_000.0)
        value >= 1_000_000_000.0 -> String.format(Locale.US, "%.2fB", value / 1_000_000_000.0)
        value >= 1_000_000.0 -> String.format(Locale.US, "%.2fM", value / 1_000_000.0)
        value >= 1_000.0 -> String.format(Locale.US, "%.2fK", value / 1_000.0)
        else -> String.format(Locale.US, "%.0f", value)
    }
}
