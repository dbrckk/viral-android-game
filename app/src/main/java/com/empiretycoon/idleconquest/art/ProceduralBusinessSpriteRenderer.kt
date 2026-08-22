package com.empiretycoon.idleconquest.art

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import org.json.JSONObject
import kotlin.math.cos
import kotlin.math.sin

class ProceduralBusinessSpriteRenderer(private val context: Context) {
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val streetRaster = StreetStandRasterRenderer(context)
    private val cornerRaster = CornerShopRasterRenderer(context)
    private val workshopRaster = WorkshopRasterRenderer(context)
    private val config: JSONObject by lazy {
        context.assets.open("art/business/group_01/vector-sprites.json")
            .bufferedReader().use { JSONObject(it.readText()) }
    }

    fun draw(canvas: Canvas, rect: RectF, businessId: String, tier: String, state: String) {
        if (businessId == "street_stand" && streetRaster.draw(canvas, rect, tier)) return
        if (businessId == "corner_shop" && cornerRaster.draw(canvas, rect, tier)) return
        if (businessId == "workshop" && workshopRaster.draw(canvas, rect, tier)) return

        val business = config.getJSONObject("businesses").getJSONObject(businessId)
        val palette = business.getJSONObject("palette")
        val spec = business.getJSONObject("tiers").getJSONObject(tier)
        val stateSpec = config.getJSONObject("states").optJSONObject(state)
            ?: config.getJSONObject("states").getJSONObject("default")

        val primary = Color.parseColor(palette.getString("primary"))
        val secondary = Color.parseColor(palette.getString("secondary"))
        val neon = Color.parseColor(palette.getString("neon"))
        val roofColor = Color.parseColor(palette.getString("roof"))
        val widthScale = spec.getDouble("width").toFloat()
        val heightScale = spec.getDouble("height").toFloat()
        val floors = spec.getInt("floors")
        val props = spec.getInt("props")
        val lights = spec.getInt("lights")
        val towers = spec.getInt("tower")
        val sign = spec.getString("sign")
        val glow = stateSpec.optDouble("glow", 1.0).toFloat()

        val cx = rect.centerX()
        val groundY = rect.bottom - rect.height() * .12f
        val w = rect.width() * widthScale * .74f
        val d = w * .46f
        val totalH = rect.height() * heightScale * .66f
        val floorH = totalH / floors.coerceAtLeast(1)

        if (stateSpec.optBoolean("ring", false)) {
            paint.style = Paint.Style.STROKE
            paint.strokeWidth = rect.width() * .035f
            paint.color = Color.parseColor(stateSpec.optString("ringColor", "#28B8FF"))
            paint.alpha = 130
            canvas.drawOval(RectF(cx-w*.68f, groundY-d*.58f, cx+w*.68f, groundY+d*.58f), paint)
            paint.alpha = 255
        }

        drawGround(canvas, cx, groundY, w * 1.22f, d * 1.35f, neon)

        repeat(floors) { floor ->
            val bottom = groundY - floor * floorH * .78f
            val top = bottom - floorH
            val shrink = 1f - floor * .035f
            drawIsoBlock(canvas, cx, top, bottom, w * shrink, d * shrink, primary, secondary)
            drawWindows(canvas, cx, top, bottom, w * shrink, d * shrink, lights.coerceAtMost(12), neon, glow)
        }

        val roofY = groundY - (floors - 1) * floorH * .78f - floorH
        drawRoof(canvas, cx, roofY, w * (1f - (floors - 1) * .035f), d, roofColor, neon, spec.getString("roof"))
        drawSign(canvas, cx, roofY + floorH * .45f, sign, neon, w)
        drawProps(canvas, cx, groundY, w, d, props, secondary)
        drawTowers(canvas, cx, roofY, w, towers, secondary, neon)

        if (stateSpec.optBoolean("crown", false)) drawCrown(canvas, cx, roofY - floorH * .42f, neon, w * .22f)
    }

    private fun drawGround(c: Canvas, cx: Float, y: Float, w: Float, d: Float, neon: Int) {
        val p = diamond(cx, y, w, d)
        paint.style = Paint.Style.FILL; paint.color = Color.rgb(21, 30, 53); c.drawPath(p, paint)
        paint.style = Paint.Style.STROKE; paint.strokeWidth = 4f; paint.color = neon; paint.alpha = 170; c.drawPath(p, paint); paint.alpha = 255
    }

    private fun drawIsoBlock(c: Canvas, cx: Float, top: Float, bottom: Float, w: Float, d: Float, primary: Int, secondary: Int) {
        val left = Path().apply { moveTo(cx, top); lineTo(cx-w/2, top+d/2); lineTo(cx-w/2, bottom+d/2); lineTo(cx, bottom); close() }
        val right = Path().apply { moveTo(cx, top); lineTo(cx+w/2, top+d/2); lineTo(cx+w/2, bottom+d/2); lineTo(cx, bottom); close() }
        paint.style = Paint.Style.FILL; paint.color = darken(primary, .72f); c.drawPath(left, paint)
        paint.color = darken(secondary, .62f); c.drawPath(right, paint)
    }

    private fun drawRoof(c: Canvas, cx: Float, y: Float, w: Float, d: Float, roof: Int, neon: Int, type: String) {
        val p = diamond(cx, y, w*1.04f, d*1.08f)
        paint.style = Paint.Style.FILL; paint.color = roof; c.drawPath(p, paint)
        paint.style = Paint.Style.STROKE; paint.strokeWidth = if (type.contains("neon") || type=="billboard") 4f else 2f; paint.color = neon; paint.alpha = 180; c.drawPath(p, paint); paint.alpha = 255
        if (type == "umbrella") {
            paint.style = Paint.Style.FILL; paint.color = Color.rgb(241,80,40); c.drawCircle(cx, y-d*.48f, w*.22f, paint)
            paint.color = Color.rgb(246,194,65); c.drawRect(cx-3f, y-d*.48f, cx+3f, y+d*.30f, paint)
        }
    }

    private fun drawWindows(c: Canvas, cx: Float, top: Float, bottom: Float, w: Float, d: Float, count: Int, neon: Int, glow: Float) {
        if (count <= 0) return
        val perSide = (count/2).coerceAtLeast(1)
        paint.style = Paint.Style.FILL; paint.color = neon; paint.alpha = (120*glow).toInt().coerceAtMost(235)
        repeat(perSide.coerceAtMost(6)) { i ->
            val t = (i+1f)/(perSide.coerceAtMost(6)+1f)
            val y = top + (bottom-top)*.55f
            c.drawRoundRect(RectF(cx-w*.45f+t*w*.40f, y, cx-w*.39f+t*w*.40f, y+(bottom-top)*.18f), 3f,3f,paint)
            c.drawRoundRect(RectF(cx+w*.04f+t*w*.39f, y, cx+w*.10f+t*w*.39f, y+(bottom-top)*.18f), 3f,3f,paint)
        }
        paint.alpha = 255
    }

    private fun drawSign(c: Canvas, cx: Float, y: Float, text: String, neon: Int, w: Float) {
        paint.style = Paint.Style.FILL; paint.color = Color.rgb(20,24,45); c.drawRoundRect(RectF(cx-w*.30f,y-w*.055f,cx+w*.30f,y+w*.055f),8f,8f,paint)
        paint.color = neon; paint.textAlign = Paint.Align.CENTER; paint.isFakeBoldText = true; paint.textSize = (w*.085f).coerceAtLeast(10f); c.drawText(text, cx, y+paint.textSize*.34f, paint); paint.textAlign = Paint.Align.LEFT; paint.isFakeBoldText = false
    }

    private fun drawProps(c: Canvas, cx: Float, y: Float, w: Float, d: Float, count: Int, color: Int) {
        paint.style = Paint.Style.FILL
        repeat(count.coerceAtMost(10)) { i ->
            val angle = (i * 2.399963f)
            val px = cx + cos(angle) * w * (.42f + (i%2)*.06f)
            val py = y + sin(angle) * d * .45f
            paint.color = if(i%3==0) Color.rgb(54,142,74) else darken(color,.85f)
            c.drawCircle(px, py, (w*.025f).coerceAtLeast(4f), paint)
        }
    }

    private fun drawTowers(c: Canvas, cx: Float, roofY: Float, w: Float, count: Int, color: Int, neon: Int) {
        repeat(count.coerceAtMost(5)) { i ->
            val x = cx + (i-(count-1)/2f) * w*.16f
            val h = w*(.16f+i*.012f)
            paint.style = Paint.Style.FILL; paint.color = darken(color,.75f); c.drawRect(x-w*.025f, roofY-h, x+w*.025f, roofY, paint)
            paint.color = neon; c.drawCircle(x, roofY-h, w*.018f, paint)
        }
    }

    private fun drawCrown(c: Canvas, cx: Float, y: Float, color: Int, size: Float) {
        val p=Path().apply{moveTo(cx-size/2,y);lineTo(cx-size*.34f,y-size*.45f);lineTo(cx,y-size*.18f);lineTo(cx+size*.34f,y-size*.45f);lineTo(cx+size/2,y);close()}
        paint.style=Paint.Style.FILL;paint.color=Color.rgb(255,190,44);c.drawPath(p,paint);paint.style=Paint.Style.STROKE;paint.strokeWidth=3f;paint.color=color;c.drawPath(p,paint)
    }

    private fun diamond(cx: Float, y: Float, w: Float, d: Float)=Path().apply{moveTo(cx,y-d/2);lineTo(cx+w/2,y);lineTo(cx,y+d/2);lineTo(cx-w/2,y);close()}
    private fun darken(color:Int,f:Float)=Color.rgb((Color.red(color)*f).toInt(),(Color.green(color)*f).toInt(),(Color.blue(color)*f).toInt())
}
