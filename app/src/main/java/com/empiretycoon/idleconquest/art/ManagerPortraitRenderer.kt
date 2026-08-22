package com.empiretycoon.idleconquest.art

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import com.empiretycoon.idleconquest.game.ManagerDefinition

class ManagerPortraitRenderer(private val context:Context){
 private val p=Paint(Paint.ANTI_ALIAS_FLAG)
 fun draw(c:Canvas,r:RectF,m:ManagerDefinition,state:String){
  val colors=when(m.id){"mia_flux"->intArrayOf(Color.rgb(240,90,40),Color.rgb(47,216,255),Color.rgb(242,179,141));"noah_vector"->intArrayOf(Color.rgb(30,116,216),Color.rgb(155,92,255),Color.rgb(185,121,85));"aya_forge"->intArrayOf(Color.rgb(217,119,31),Color.rgb(56,200,255),Color.rgb(217,149,114));else->intArrayOf(Color.rgb(138,63,209),Color.rgb(47,216,255),Color.rgb(142,91,70))}
  p.style=Paint.Style.FILL;p.color=Color.rgb(12,17,36);c.drawRoundRect(r,22f,22f,p)
  if(state!="locked"){p.color=colors[1];p.alpha=55;c.drawCircle(r.centerX(),r.centerY(),r.width()*.42f,p);p.alpha=255}
  val cx=r.centerX();val headY=r.top+r.height()*.35f;p.color=colors[2];c.drawCircle(cx,headY,r.width()*.18f,p)
  p.color=Color.rgb(25,27,40);c.drawArc(RectF(cx-r.width()*.19f,headY-r.width()*.20f,cx+r.width()*.19f,headY+r.width()*.08f),180f,180f,true,p)
  p.color=colors[0];c.drawRoundRect(RectF(r.left+r.width()*.25f,r.top+r.height()*.53f,r.right-r.width()*.25f,r.bottom-r.height()*.08f),18f,18f,p)
  p.style=Paint.Style.STROKE;p.strokeWidth=if(state=="hired")7f else 3f;p.color=if(state=="hired")Color.rgb(245,201,92)else colors[1];c.drawRoundRect(r,22f,22f,p)
  if(state=="locked"){p.style=Paint.Style.FILL;p.color=Color.argb(170,5,7,16);c.drawRoundRect(r,22f,22f,p);p.color=Color.LTGRAY;p.textAlign=Paint.Align.CENTER;p.textSize=r.width()*.23f;c.drawText("LOCK",cx,r.centerY()+p.textSize*.35f,p);p.textAlign=Paint.Align.LEFT}
 }
}
