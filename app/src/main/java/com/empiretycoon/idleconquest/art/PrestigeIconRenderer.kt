package com.empiretycoon.idleconquest.art

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import org.json.JSONObject

class PrestigeIconRenderer(private val context:Context){
 private val p=Paint(Paint.ANTI_ALIAS_FLAG)
 private val root by lazy{JSONObject(context.assets.open("art/prestige/prestige.json").bufferedReader().use{it.readText()})}
 fun draw(c:Canvas,r:RectF,state:String,crowns:Int){
  val currency=root.getJSONObject("currency");val primary=Color.parseColor(currency.getString("primary"));val energy=Color.parseColor(currency.getString("energy"));val states=root.getJSONObject("states");val s=states.optJSONObject(state)?:states.getJSONObject("locked");val ring=Color.parseColor(s.getString("ring"));val glow=s.getDouble("glow").toFloat();
  p.style=Paint.Style.FILL;p.color=Color.rgb(12,17,36);c.drawRoundRect(r,20f,20f,p)
  p.color=ring;p.alpha=(45*glow).toInt().coerceIn(20,140);c.drawCircle(r.centerX(),r.centerY(),r.width()*.42f,p);p.alpha=255
  val cx=r.centerX();val cy=r.centerY();val w=r.width()*.48f;val h=r.height()*.30f
  val crown=Path().apply{moveTo(cx-w*.5f,cy+h*.25f);lineTo(cx-w*.38f,cy-h*.35f);lineTo(cx-w*.12f,cy-h*.05f);lineTo(cx,cy-h*.48f);lineTo(cx+w*.12f,cy-h*.05f);lineTo(cx+w*.38f,cy-h*.35f);lineTo(cx+w*.5f,cy+h*.25f);close()}
  p.color=primary;c.drawPath(crown,p);p.color=energy;c.drawCircle(cx,cy-h*.02f,r.width()*.045f,p)
  p.style=Paint.Style.STROKE;p.strokeWidth=if(state=="ready"||state=="prestiging")6f else 3f;p.color=ring;c.drawRoundRect(r,20f,20f,p)
  p.style=Paint.Style.FILL;p.color=Color.WHITE;p.textAlign=Paint.Align.CENTER;p.textSize=r.width()*.16f;p.isFakeBoldText=true;c.drawText(crowns.toString(),cx,r.bottom-r.height()*.10f,p);p.textAlign=Paint.Align.LEFT;p.isFakeBoldText=false
 }
}
