package com.empiretycoon.idleconquest.art

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import com.empiretycoon.idleconquest.game.ManagerDefinition
import org.json.JSONObject

class ManagerPortraitRenderer(private val context:Context){
 private val p=Paint(Paint.ANTI_ALIAS_FLAG)
 private val root by lazy{JSONObject(context.assets.open("art/managers/manager-sprites.json").bufferedReader().use{it.readText()})}
 private fun visual(id:String):JSONObject?{val a=root.getJSONArray("portraits");for(i in 0 until a.length()){val o=a.getJSONObject(i);if(o.getString("id")==id)return o};return null}
 fun draw(c:Canvas,r:RectF,m:ManagerDefinition,state:String){
  val v=visual(m.id)?:return;val primary=Color.parseColor(v.getString("primary"));val neon=Color.parseColor(v.getString("neon"));val skin=Color.parseColor(v.getString("skin"));val hair=Color.parseColor(v.getString("hair"));val outfit=Color.parseColor(v.getString("outfit"));val cx=r.centerX();val headY=r.top+r.height()*.35f
  p.style=Paint.Style.FILL;p.color=Color.rgb(12,17,36);c.drawRoundRect(r,22f,22f,p)
  if(state!="locked"){p.color=neon;p.alpha=if(state=="boosted")90 else 55;c.drawCircle(cx,r.centerY(),r.width()*.42f,p);p.alpha=255}
  p.color=skin;c.drawCircle(cx,headY,r.width()*.18f,p);p.color=hair;c.drawArc(RectF(cx-r.width()*.19f,headY-r.width()*.20f,cx+r.width()*.19f,headY+r.width()*.08f),180f,180f,true,p)
  p.color=outfit;c.drawRoundRect(RectF(r.left+r.width()*.25f,r.top+r.height()*.53f,r.right-r.width()*.25f,r.bottom-r.height()*.08f),18f,18f,p)
  p.style=Paint.Style.STROKE;p.strokeWidth=r.width()*.035f;p.color=neon
  when(v.getString("accessory")){"visor"->c.drawLine(cx-r.width()*.13f,headY,cx+r.width()*.13f,headY,p);"holo_glasses"->{c.drawCircle(cx-r.width()*.07f,headY,r.width()*.045f,p);c.drawCircle(cx+r.width()*.07f,headY,r.width()*.045f,p)};"headset"->c.drawArc(RectF(cx-r.width()*.22f,headY-r.width()*.20f,cx+r.width()*.22f,headY+r.width()*.13f),190f,160f,false,p);else->{c.drawLine(cx+r.width()*.12f,headY-r.width()*.12f,cx+r.width()*.17f,headY+r.width()*.09f,p);c.drawCircle(cx+r.width()*.15f,headY,r.width()*.022f,p)}}
  p.strokeWidth=if(state=="hired"||state=="boosted")7f else 3f;p.color=if(state=="hired"||state=="boosted")Color.rgb(245,201,92)else neon;c.drawRoundRect(r,22f,22f,p)
  p.style=Paint.Style.FILL;p.color=primary;c.drawCircle(r.left+r.width()*.18f,r.bottom-r.height()*.15f,r.width()*.035f,p)
  if(state=="locked"){p.color=Color.argb(170,5,7,16);c.drawRoundRect(r,22f,22f,p);p.color=Color.LTGRAY;p.textAlign=Paint.Align.CENTER;p.textSize=r.width()*.16f;p.isFakeBoldText=true;c.drawText("LOCK",cx,r.centerY()+p.textSize*.35f,p);p.textAlign=Paint.Align.LEFT;p.isFakeBoldText=false}
 }
}
