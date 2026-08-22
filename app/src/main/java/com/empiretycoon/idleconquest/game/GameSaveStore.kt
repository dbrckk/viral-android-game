package com.empiretycoon.idleconquest.game

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject
import kotlin.math.min

data class RestoreResult(val state:GameState,val offlineSeconds:Long,val offlineEarnings:Double)
class GameSaveStore(context:Context){
 private val prefs=context.getSharedPreferences(PREFS_NAME,Context.MODE_PRIVATE)
 fun save(state:GameState,nowEpochMillis:Long=System.currentTimeMillis()){
  val levels=JSONArray().apply{state.businesses.forEach{b->put(JSONObject().apply{put("id",b.id);put("level",b.level)})}}
  val managers=JSONArray().apply{state.hiredManagers().forEach{put(it)}}
  val payload=JSONObject().apply{put("schemaVersion",2);put("cash",state.cash);put("gems",state.gems);put("savedAtEpochMillis",nowEpochMillis);put("businesses",levels);put("hiredManagers",managers)}
  prefs.edit().putString(KEY_SAVE,payload.toString()).apply()
 }
 fun restore(nowEpochMillis:Long=System.currentTimeMillis()):RestoreResult{
  val raw=prefs.getString(KEY_SAVE,null)?:return RestoreResult(GameState(),0,0.0)
  return try{val j=JSONObject(raw);val s=GameState();s.restoreEconomy(j.optDouble("cash",s.cash),j.optInt("gems",s.gems),parseLevels(j.optJSONArray("businesses")),parseManagers(j.optJSONArray("hiredManagers")));val saved=j.optLong("savedAtEpochMillis",nowEpochMillis);val seconds=min(((nowEpochMillis-saved).coerceAtLeast(0)/1000),MAX_OFFLINE_SECONDS);val earnings=s.totalIncomePerSecond*seconds;s.addCash(earnings);RestoreResult(s,seconds,earnings)}catch(_:Exception){RestoreResult(GameState(),0,0.0)}
 }
 private fun parseLevels(a:JSONArray?):Map<String,Int>{if(a==null)return emptyMap();val r=mutableMapOf<String,Int>();for(i in 0 until a.length()){val o=a.optJSONObject(i)?:continue;val id=o.optString("id");if(id.isNotBlank())r[id]=o.optInt("level",1).coerceAtLeast(1)};return r}
 private fun parseManagers(a:JSONArray?):Set<String>{if(a==null)return emptySet();return buildSet{for(i in 0 until a.length()){val id=a.optString(i);if(id.isNotBlank())add(id)}}}
 companion object{private const val PREFS_NAME="empire_tycoon_save";private const val KEY_SAVE="game_state_v1";private const val MAX_OFFLINE_SECONDS=8L*60*60}
}
