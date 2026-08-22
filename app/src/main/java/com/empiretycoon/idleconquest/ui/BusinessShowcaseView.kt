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
import com.empiretycoon.idleconquest.game.BusinessState
import com.empiretycoon.idleconquest.game.GameSaveStore
import com.empiretycoon.idleconquest.game.GameState
import java.util.Locale

class BusinessShowcaseView(context: Context) : View(context) {
    private val resolver = BusinessArtResolver(context)
    private val saveStore = GameSaveStore(context)
    private var gameState: GameState
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val upgradeRects = mutableListOf<RectF>()
    private var lastFrameNanos = 0L
    private var lastAutosaveNanos = 0L
    private var offlineBannerText: String? = null
    private var offlineBannerUntilNanos = 0L
    private var milestoneBannerText: String? = null
    private var milestoneBannerUntilNanos = 0L
    private var highlightedMilestoneBusinessId: String? = null
    private var highlightedMilestoneUntilNanos = 0L

    init {
        val restore = saveStore.restore()
        gameState = restore.state
        if (restore.offlineEarnings > 0.0) {
            offlineBannerText = "WELCOME BACK  +$ ${formatNumber(restore.offlineEarnings)}"
            offlineBannerUntilNanos = System.nanoTime() + 6_000_000_000L
        }
        isClickable = true
        keepScreenOn = true
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        updateEconomy()
        maybeAutosave()
        canvas.drawColor(Color.rgb(8, 10, 24))

        val margin = width * 0.045f
        val hudHeight = height * 0.12f
        drawHud(canvas, RectF(margin, margin, width - margin, margin + hudHeight))

        val now = System.nanoTime()
        val bannerText = when {
            milestoneBannerText != null && now < milestoneBannerUntilNanos -> milestoneBannerText
            offlineBannerText != null && now < offlineBannerUntilNanos -> offlineBannerText
            else -> null
        }

        val bannerHeight = if (bannerText != null) height * 0.055f else 0f
        if (bannerHeight > 0f) {
            drawBanner(
                canvas,
                RectF(margin, margin + hudHeight + 8f, width - margin, margin + hudHeight + 8f + bannerHeight),
                bannerText.orEmpty(),
                milestone = milestoneBannerText != null && now < milestoneBannerUntilNanos
            )
        }

        upgradeRects.clear()

        val topStart = margin + hudHeight + margin + bannerHeight
        val availableHeight = height - topStart - margin
        val gap = margin * 0.75f
        val cardHeight = (availableHeight - gap * 3f) / 4f

        gameState.businesses.forEachIndexed { index, _ ->
            val top = topStart + index * (cardHeight + gap)
            val rect = RectF(margin, top, width - margin, top + cardHeight)
            drawBusinessCard(canvas, rect, index, now)
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

    private fun maybeAutosave() {
        val now = System.nanoTime()
        if (lastAutosaveNanos == 0L || now - lastAutosaveNanos >= AUTOSAVE_INTERVAL_NANOS) {
            saveStore.save(gameState)
            lastAutosaveNanos = now
        }
    }

    fun persistNow() {
        saveStore.save(gameState)
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

    private fun drawBanner(canvas: Canvas, rect: RectF, text: String, milestone: Boolean) {
        paint.style = Paint.Style.FILL
        paint.color = if (milestone) Color.rgb(95, 67, 18) else Color.rgb(34, 74, 63)
        canvas.drawRoundRect(rect, 20f, 20f, paint)
        paint.color = if (milestone) Color.rgb(255, 220, 112) else Color.rgb(143, 255, 194)
        paint.textAlign = Paint.Align.CENTER
        paint.isFakeBoldText = true
        paint.textSize = width * 0.030f
        canvas.drawText(text, rect.centerX(), rect.centerY() + paint.textSize * 0.35f, paint)
        paint.textAlign = Paint.Align.LEFT
        paint.isFakeBoldText = false
    }

    private fun drawBusinessCard(canvas: Canvas, rect: RectF, index: Int, nowNanos: Long) {
        val business = gameState.businesses[index]
        val tier = gameState.tierFor(business.level)
        val milestoneActive = highlightedMilestoneBusinessId == business.id && nowNanos < highlightedMilestoneUntilNanos
        val selection = resolver.resolve(
            businessId = business.id,
            tier = tier,
            state = if (milestoneActive) "milestone" else "default"
        )

        paint.style = Paint.Style.FILL
        paint.color = if (milestoneActive) Color.rgb(38, 30, 54) else Color.rgb(15, 20, 42)
        canvas.drawRoundRect(rect, 28f, 28f, paint)

        if (milestoneActive) {
            paint.style = Paint.Style.STROKE
            paint.strokeWidth = 5f
            paint.color = Color.rgb(245, 201, 92)
            canvas.drawRoundRect(rect, 28f, 28f, paint)
        }

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
        paint.style = Paint.Style.FILL
        paint.color = Color.WHITE
        paint.textSize = width * 0.038f
        paint.isFakeBoldText = true
        canvas.drawText(business.displayName, textLeft, rect.top + rect.height() * 0.22f, paint)

        paint.isFakeBoldText = false
        paint.textSize = width * 0.028f
        paint.color = Color.rgb(188, 198, 225)
        canvas.drawText("Lv. ${business.level}  •  ${tier.uppercase(Locale.US)}", textLeft, rect.top + rect.height() * 0.39f, paint)

        paint.color = Color.rgb(92, 230, 145)
        canvas.drawText(
            "+${formatNumber(business.incomePerSecond)}/s  ×${formatMultiplier(business.productionMultiplier)}",
            textLeft,
            rect.top + rect.height() * 0.55f,
            paint
        )

        val nextMilestone = BusinessState.nextMilestoneAfter(business.level)
        paint.color = Color.rgb(245, 201, 92)
        paint.textSize = width * 0.023f
        val milestoneText = if (nextMilestone != null) {
            "NEXT MILESTONE Lv.${nextMilestone.level}  ×${formatMultiplier(nextMilestone.multiplier)}"
        } else {
            "MAX MILESTONE  ×${formatMultiplier(business.productionMultiplier)}"
        }
        canvas.drawText(milestoneText, textLeft, rect.top + rect.height() * 0.69f, paint)

        val button = RectF(
            textLeft,
            rect.bottom - rect.height() * 0.24f,
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
        paint.textSize = width * 0.027f
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
            if (index >= 0) {
                val businessBefore = gameState.businesses[index]
                val result = gameState.upgrade(index)
                if (result.upgraded) {
                    result.reachedMilestone?.let { milestone ->
                        milestoneBannerText = "MILESTONE! ${businessBefore.displayName} Lv.${milestone.level}  ×${formatMultiplier(milestone.multiplier)} PRODUCTION"
                        milestoneBannerUntilNanos = System.nanoTime() + MILESTONE_FEEDBACK_NANOS
                        highlightedMilestoneBusinessId = businessBefore.id
                        highlightedMilestoneUntilNanos = milestoneBannerUntilNanos
                    }
                    saveStore.save(gameState)
                    performClick()
                    invalidate()
                }
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

    private fun formatMultiplier(value: Double): String = if (value % 1.0 == 0.0) {
        value.toInt().toString()
    } else {
        String.format(Locale.US, "%.1f", value)
    }

    private fun formatNumber(value: Double): String = when {
        value >= 1_000_000_000_000.0 -> String.format(Locale.US, "%.2fT", value / 1_000_000_000_000.0)
        value >= 1_000_000_000.0 -> String.format(Locale.US, "%.2fB", value / 1_000_000_000.0)
        value >= 1_000_000.0 -> String.format(Locale.US, "%.2fM", value / 1_000_000.0)
        value >= 1_000.0 -> String.format(Locale.US, "%.2fK", value / 1_000.0)
        else -> String.format(Locale.US, "%.0f", value)
    }

    companion object {
        private const val AUTOSAVE_INTERVAL_NANOS = 10_000_000_000L
        private const val MILESTONE_FEEDBACK_NANOS = 5_000_000_000L
    }
}
