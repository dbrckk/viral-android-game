package com.empiretycoon.idleconquest.game

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject

data class RestoreResult(val state:GameState,val offlineSeconds:Long,val offlineEarnings:Double)

class GameSaveStore(context:Context){
 private val prefs=context.getSharedPreferences(PREFS_NAME,Context.MODE_PRIVATE)

 fun save(state:GameState,nowEpochMillis:Long=System.currentTimeMillis()){
  val levels=JSONArray().apply{state.businesses.forEach{b->put(JSONObject().apply{put("id",b.id);put("level",b.level)})}}
  val managers=JSONArray().apply{state.hiredManagers().forEach{put(it)}}
  val permanent=JSONArray().apply{state.purchasedPermanentUpgrades().forEach{put(it)}}
  val missions=JSONArray().apply{state.claimedMissions().forEach{put(it)}}
  val payload=JSONObject().apply{
   put("schemaVersion",SCHEMA_VERSION)
   put("cash",state.cash)
   put("gems",state.gems)
   put("prestigeCrowns",state.prestigeCrowns)
   put("runEarnings",state.runEarnings)
   put("savedAtEpochMillis",nowEpochMillis)
   put("businesses",levels)
   put("hiredManagers",managers)
   put("permanentUpgrades",permanent)
   put("claimedMissions",missions)
  }.toString()

  val previous=prefs.getString(KEY_SAVE,null)
  prefs.edit().apply{
   if(previous!=null)putString(KEY_BACKUP,previous)
   putString(KEY_SAVE,payload)
  }.apply()
 }

 fun restore(nowEpochMillis:Long=System.currentTimeMillis()):RestoreResult{
  val primary=prefs.getString(KEY_SAVE,null)
  if(primary!=null){
   restorePayload(primary,nowEpochMillis)?.let{return it}
  }
  val backup=prefs.getString(KEY_BACKUP,null)
  if(backup!=null){
   restorePayload(backup,nowEpochMillis)?.let{
    prefs.edit().putString(KEY_SAVE,backup).apply()
    return it
   }
  }
  return RestoreResult(GameState(),0,0.0)
 }

 private fun restorePayload(raw:String,nowEpochMillis:Long):RestoreResult?{
  return try{
   val j=JSONObject(raw)
   val schema=j.optInt("schemaVersion",1)
   if(schema<1||schema>SCHEMA_VERSION){
    null
   }else{
    val s=GameState()
    s.restoreEconomy(
     finiteOr(j.optDouble("cash",s.cash),s.cash),
     j.optInt("gems",s.gems),
     parseLevels(j.optJSONArray("businesses")),
     parseIds(j.optJSONArray("hiredManagers")),
     parseIds(j.optJSONArray("permanentUpgrades")),
     parseIds(j.optJSONArray("claimedMissions")),
     j.optInt("prestigeCrowns",0),
     finiteOr(j.optDouble("runEarnings",0.0),0.0)
    )

    val saved=j.optLong("savedAtEpochMillis",nowEpochMillis)
    val offline=OfflineProgressCalculator.calculate(nowEpochMillis,saved,s.totalIncomePerSecond)
    s.addCash(offline.earnings,true)
    RestoreResult(s,offline.seconds,offline.earnings)
   }
  }catch(_:Exception){
   null
  }
 }

 private fun finiteOr(value:Double,fallback:Double)=if(value.isFinite()&&value>=0.0)value else fallback

 private fun parseLevels(a:JSONArray?):Map<String,Int>{
  if(a==null)return emptyMap()
  val r=mutableMapOf<String,Int>()
  for(i in 0 until a.length()){
   val o=a.optJSONObject(i)?:continue
   val id=o.optString("id")
   if(id.isNotBlank())r[id]=o.optInt("level",1).coerceAtLeast(1)
  }
  return r
 }

 private fun parseIds(a:JSONArray?):Set<String>{
  if(a==null)return emptySet()
  return buildSet{
   for(i in 0 until a.length()){
    val id=a.optString(i)
    if(id.isNotBlank())add(id)
   }
  }
 }

 companion object{
  private const val PREFS_NAME="empire_tycoon_save"
  private const val KEY_SAVE="game_state_v1"
  private const val KEY_BACKUP="game_state_backup_v1"
  private const val SCHEMA_VERSION=5
 }
}
