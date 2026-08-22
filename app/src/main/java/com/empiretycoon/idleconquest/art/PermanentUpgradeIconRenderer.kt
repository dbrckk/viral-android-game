package com.empiretycoon.idleconquest.art

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import org.json.JSONObject

class PermanentUpgradeIconRenderer(private val context:Context){
 private val p=Paint(Paint.ANTI_ALIAS_FLAG)
 private val root by lazy{JSONObject(context.assets.open("art/upgrades/permanent-upgrades.json").bufferedReader().use{it.readText()})}
 private fun visual(id:String):JSONObject?{val a=root.getJSONArray("upgrades");for(i in 0 until a.length()){val o=a.getJSONObject(i);if(o.getString("id")==id)return o};return null}
 fun draw(c:Canvas,r:RectF,id:String,state:String){
  val v=visual(id)?:return;val primary=Color.parseColor(v.getString("primary"));val neon=Color.parseColor(v.getString("neon"));val cx=r.centerX();val cy=r.centerY();val rad=minOf(r.width(),r.height())*.46f
  val path=Path();for(i in 0..5){val a=Math.toRadians((60*i-30).toDouble());val x=cx+(kotlin.math.cos(a)*rad).toFloat();val y=cy+(kotlin.math.sin(a)*rad).toFloat();if(i==0)path.moveTo(x,y)else path.lineTo(x,y)};path.close()
  p.style=Paint.Style.FILL;p.color=Color.rgb(12,17,36);c.drawPath(path,p);p.color=primary;p.alpha=if(state=="locked")45 else 90;c.drawPath(path,p);p.alpha=255
  p.style=Paint.Style.STROKE;p.strokeWidth=if(state=="purchased")6f else 3f;p.color=if(state=="purchased")Color.rgb(245,201,92)else neon;c.drawPath(path,p)
  p.style=Paint.Style.FILL;p.textAlign=Paint.Align.CENTER;p.isFakeBoldText=true;p.textSize=r.width()*.19f;p.color=if(state=="locked")Color.GRAY else Color.WHITE;c.drawText(v.getString("glyph"),cx,cy+p.textSize*.34f,p);p.textAlign=Paint.Align.LEFT;p.isFakeBoldText=false
  if(state=="locked"){p.color=Color.argb(150,5,7,16);c.drawPath(path,p);p.color=Color.LTGRAY;p.textAlign=Paint.Align.CENTER;p.textSize=r.width()*.14f;p.isFakeBoldText=true;c.drawText("LOCK",cx,cy+p.textSize*.32f,p);p.textAlign=Paint.Align.LEFT;p.isFakeBoldText=false}
 }
}
