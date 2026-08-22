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
import com.empiretycoon.idleconquest.game.BuyMode
import com.empiretycoon.idleconquest.game.GameSaveStore
import com.empiretycoon.idleconquest.game.GameState
import java.util.Locale

class BusinessShowcaseView(context: Context) : View(context) {
    private val resolver = BusinessArtResolver(context)
    private val saveStore = GameSaveStore(context)
    private var gameState: GameState
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val upgradeRects = mutableListOf<RectF>()
    private val modeRects = mutableListOf<Pair<BuyMode, RectF>>()
    private var buyMode = BuyMode.X1
    private var lastFrameNanos = 0L
    private var lastAutosaveNanos = 0L
    private var bannerText: String? = null
    private var bannerUntil = 0L
    private var highlightedBusinessId: String? = null

    init {
        val restore = saveStore.restore(); gameState = restore.state
        if (restore.offlineEarnings > 0.0) showBanner("WELCOME BACK  +$ ${formatNumber(restore.offlineEarnings)}", 6_000_000_000L)
        isClickable = true; keepScreenOn = true
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas); updateEconomy(); maybeAutosave(); canvas.drawColor(Color.rgb(8, 10, 24))
        val margin = width * .045f; val hudH = height * .105f; val modeH = height * .052f
        drawHud(canvas, RectF(margin, margin, width-margin, margin+hudH))
        drawBuyModes(canvas, RectF(margin, margin+hudH+8f, width-margin, margin+hudH+8f+modeH))
        val now = System.nanoTime(); val visibleBanner = bannerText != null && now < bannerUntil
        val bannerH = if (visibleBanner) height*.045f else 0f
        if (visibleBanner) drawBanner(canvas, RectF(margin, margin+hudH+modeH+16f, width-margin, margin+hudH+modeH+16f+bannerH), bannerText.orEmpty())
        upgradeRects.clear()
        val top = margin+hudH+modeH+margin+bannerH; val gap=margin*.55f
        val cardH=(height-top-margin-gap*3f)/4f
        gameState.businesses.indices.forEach { i -> drawBusinessCard(canvas, RectF(margin, top+i*(cardH+gap), width-margin, top+i*(cardH+gap)+cardH), i, now) }
        postInvalidateOnAnimation()
    }

    private fun updateEconomy(){ val n=System.nanoTime(); if(lastFrameNanos!=0L) gameState.tick(((n-lastFrameNanos)/1e9).coerceAtMost(.25)); lastFrameNanos=n }
    private fun maybeAutosave(){ val n=System.nanoTime(); if(lastAutosaveNanos==0L||n-lastAutosaveNanos>=10_000_000_000L){saveStore.save(gameState);lastAutosaveNanos=n} }
    fun persistNow()=saveStore.save(gameState)

    private fun drawHud(c:Canvas,r:RectF){ paint.style=Paint.Style.FILL;paint.color=Color.rgb(13,17,36);c.drawRoundRect(r,30f,30f,paint);paint.isFakeBoldText=true;paint.textSize=width*.043f;paint.color=Color.rgb(245,201,92);c.drawText("EMPIRE TYCOON",r.left+24,r.top+r.height()*.38f,paint);paint.isFakeBoldText=false;paint.textSize=width*.033f;paint.color=Color.WHITE;c.drawText("$ ${formatNumber(gameState.cash)}",r.left+24,r.bottom-18,paint);paint.color=Color.rgb(92,230,145);c.drawText("+${formatNumber(gameState.totalIncomePerSecond)}/s",r.centerX()-10,r.bottom-18,paint);paint.textAlign=Paint.Align.RIGHT;paint.color=Color.rgb(120,187,255);c.drawText("◆ ${gameState.gems}",r.right-24,r.bottom-18,paint);paint.textAlign=Paint.Align.LEFT }

    private fun drawBuyModes(c:Canvas,r:RectF){ modeRects.clear(); val modes=BuyMode.entries; val gap=8f; val w=(r.width()-gap*3)/4; modes.forEachIndexed{i,m-> val b=RectF(r.left+i*(w+gap),r.top,r.left+i*(w+gap)+w,r.bottom);modeRects+=m to b;paint.style=Paint.Style.FILL;paint.color=if(m==buyMode) Color.rgb(43,121,220) else Color.rgb(29,39,70);c.drawRoundRect(b,18f,18f,paint);paint.color=Color.WHITE;paint.textAlign=Paint.Align.CENTER;paint.isFakeBoldText=true;paint.textSize=width*.027f;c.drawText(if(m==BuyMode.MAX)"MAX" else "×${m.name.drop(1)}",b.centerX(),b.centerY()+paint.textSize*.35f,paint)};paint.textAlign=Paint.Align.LEFT;paint.isFakeBoldText=false }
    private fun drawBanner(c:Canvas,r:RectF,t:String){paint.style=Paint.Style.FILL;paint.color=Color.rgb(95,67,18);c.drawRoundRect(r,18f,18f,paint);paint.color=Color.rgb(255,220,112);paint.textAlign=Paint.Align.CENTER;paint.isFakeBoldText=true;paint.textSize=width*.026f;c.drawText(t,r.centerX(),r.centerY()+paint.textSize*.35f,paint);paint.textAlign=Paint.Align.LEFT;paint.isFakeBoldText=false}

    private fun drawBusinessCard(c:Canvas,r:RectF,index:Int,now:Long){ val b=gameState.businesses[index];val tier=gameState.tierFor(b.level);val active=highlightedBusinessId==b.id&&now<bannerUntil;val art=resolver.resolve(b.id,tier,state=if(active)"milestone" else "default");paint.style=Paint.Style.FILL;paint.color=if(active)Color.rgb(38,30,54)else Color.rgb(15,20,42);c.drawRoundRect(r,26f,26f,paint);if(active){paint.style=Paint.Style.STROKE;paint.strokeWidth=5f;paint.color=Color.rgb(245,201,92);c.drawRoundRect(r,26f,26f,paint)};val inset=14f;val ar=RectF(r.left+inset,r.top+inset,r.left+r.width()*.32f,r.bottom-inset);if(art.layerPaths.isNotEmpty())art.layerPaths.forEach{p->context.assets.open(p).use{BitmapFactory.decodeStream(it)?.let{x->c.drawBitmap(x,null,ar,paint)}}}else drawFallbackArt(c,ar,art.accent);val x=r.left+r.width()*.37f;paint.style=Paint.Style.FILL;paint.color=Color.WHITE;paint.textSize=width*.034f;paint.isFakeBoldText=true;c.drawText(b.displayName,x,r.top+r.height()*.20f,paint);paint.isFakeBoldText=false;paint.textSize=width*.025f;paint.color=Color.rgb(188,198,225);c.drawText("Lv.${b.level} • ${tier.uppercase(Locale.US)} • ×${formatMultiplier(b.productionMultiplier)}",x,r.top+r.height()*.37f,paint);paint.color=Color.rgb(92,230,145);c.drawText("+${formatNumber(b.incomePerSecond)}/s",x,r.top+r.height()*.52f,paint);val next=BusinessState.nextMilestoneAfter(b.level);paint.color=Color.rgb(245,201,92);paint.textSize=width*.021f;c.drawText(if(next!=null)"NEXT Lv.${next.level} ×${formatMultiplier(next.multiplier)}" else "MAX MILESTONE",x,r.top+r.height()*.65f,paint);val button=RectF(x,r.bottom-r.height()*.27f,r.right-inset,r.bottom-inset);upgradeRects+=button;drawUpgradeButton(c,button,index) }

    private fun drawUpgradeButton(c:Canvas,r:RectF,index:Int){val q=gameState.quoteUpgrade(index,buyMode);val enabled=gameState.canUpgrade(index,buyMode);paint.style=Paint.Style.FILL;paint.color=if(enabled)Color.rgb(42,166,105)else Color.rgb(59,68,91);c.drawRoundRect(r,20f,20f,paint);paint.color=Color.WHITE;paint.textAlign=Paint.Align.CENTER;paint.isFakeBoldText=true;paint.textSize=width*.024f;val label=if(q.levels>0)"UPGRADE +${q.levels}  $ ${formatNumber(q.cost)}" else "UPGRADE MAX";c.drawText(label,r.centerX(),r.centerY()+paint.textSize*.35f,paint);paint.textAlign=Paint.Align.LEFT;paint.isFakeBoldText=false}

    override fun onTouchEvent(e:MotionEvent):Boolean{if(e.action==MotionEvent.ACTION_UP){modeRects.firstOrNull{it.second.contains(e.x,e.y)}?.let{buyMode=it.first;performClick();invalidate();return true};val i=upgradeRects.indexOfFirst{it.contains(e.x,e.y)};if(i>=0){val before=gameState.businesses[i];val result=gameState.upgrade(i,buyMode);if(result.upgraded){result.reachedMilestones.lastOrNull()?.let{m->highlightedBusinessId=before.id;showBanner("MILESTONE! ${before.displayName} Lv.${m.level} ×${formatMultiplier(m.multiplier)}",5_000_000_000L)};saveStore.save(gameState);performClick();invalidate()}}};return true}
    override fun performClick():Boolean{super.performClick();return true}
    private fun showBanner(t:String,d:Long){bannerText=t;bannerUntil=System.nanoTime()+d}
    private fun drawFallbackArt(c:Canvas,r:RectF,a:String){paint.color=when{a.startsWith("green")->Color.rgb(82,210,126);a.startsWith("blue")->Color.rgb(72,183,255);a.startsWith("orange")->Color.rgb(255,157,72);a.startsWith("violet")->Color.rgb(196,98,255);else->Color.LTGRAY};paint.style=Paint.Style.STROKE;paint.strokeWidth=7f;c.drawRoundRect(r,22f,22f,paint);paint.style=Paint.Style.FILL;paint.alpha=45;c.drawRoundRect(r,22f,22f,paint);paint.alpha=255}
    private fun formatMultiplier(v:Double)=if(v%1.0==0.0)v.toInt().toString() else String.format(Locale.US,"%.1f",v)
    private fun formatNumber(v:Double)=when{v>=1e12->String.format(Locale.US,"%.2fT",v/1e12);v>=1e9->String.format(Locale.US,"%.2fB",v/1e9);v>=1e6->String.format(Locale.US,"%.2fM",v/1e6);v>=1e3->String.format(Locale.US,"%.2fK",v/1e3);else->String.format(Locale.US,"%.0f",v)}
}
