package com.empiretycoon.idleconquest.art

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import com.empiretycoon.idleconquest.game.MissionDefinition
import org.json.JSONObject

class MissionBadgeRenderer(private val context:Context){
 private val p=Paint(Paint.ANTI_ALIAS_FLAG)
 private val root by lazy{JSONObject(context.assets.open("art/missions/missions.json").bufferedReader().use{it.readText()})}
 private fun visual(id:String):JSONObject?{val a=root.getJSONArray("missions");for(i in 0 until a.length()){val o=a.getJSONObject(i);if(o.getString("id")==id)return o};return null}
 fun draw(c:Canvas,r:RectF,m:MissionDefinition,state:String){
  val v=visual(m.id)?:return;val primary=Color.parseColor(v.getString("primary"));val secondary=Color.parseColor(v.getString("secondary"));val cx=r.centerX();val cy=r.centerY();val rad=r.width().coerceAtMost(r.height())*.42f
  p.style=Paint.Style.FILL;p.color=Color.rgb(12,17,36);c.drawRoundRect(r,18f,18f,p)
  val hex=Path();for(i in 0..5){val a=Math.PI/3*i-Math.PI/2;val x=cx+(rad*Math.cos(a)).toFloat();val y=cy+(rad*Math.sin(a)).toFloat();if(i==0)hex.moveTo(x,y)else hex.lineTo(x,y)};hex.close();p.color=secondary;p.alpha=70;c.drawPath(hex,p);p.alpha=255;p.style=Paint.Style.STROKE;p.strokeWidth=5f;p.color=when(state){"complete"->Color.rgb(245,201,92);"claimed"->Color.rgb(92,230,145);"locked"->Color.DKGRAY;else->primary};c.drawPath(hex,p)
  p.style=Paint.Style.FILL;p.textAlign=Paint.Align.CENTER;p.isFakeBoldText=true;p.textSize=r.width()*.22f;p.color=if(state=="locked")Color.GRAY else primary
  val glyph=when(v.getString("icon")){"stack_up"->"↑";"street_badge"->"S";"income_wave"->"$";"team"->"II";"chip_star"->"★";else->"♛"};c.drawText(glyph,cx,cy+p.textSize*.35f,p)
  if(state=="claimed"){p.textSize=r.width()*.14f;p.color=Color.rgb(92,230,145);c.drawText("✓",r.right-r.width()*.16f,r.top+r.height()*.24f,p)}
  p.textAlign=Paint.Align.LEFT;p.isFakeBoldText=false
 }
}
