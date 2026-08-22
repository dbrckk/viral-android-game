package com.empiretycoon.idleconquest.game

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject
import kotlin.math.min

data class RestoreResult(
    val state: GameState,
    val offlineSeconds: Long,
    val offlineEarnings: Double
)

class GameSaveStore(context: Context) {
    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun save(state: GameState, nowEpochMillis: Long = System.currentTimeMillis()) {
        val levels = JSONArray().apply {
            state.businesses.forEach { business ->
                put(JSONObject().apply {
                    put("id", business.id)
                    put("level", business.level)
                })
            }
        }

        val payload = JSONObject().apply {
            put("schemaVersion", 1)
            put("cash", state.cash)
            put("gems", state.gems)
            put("savedAtEpochMillis", nowEpochMillis)
            put("businesses", levels)
        }

        prefs.edit().putString(KEY_SAVE, payload.toString()).apply()
    }

    fun restore(nowEpochMillis: Long = System.currentTimeMillis()): RestoreResult {
        val raw = prefs.getString(KEY_SAVE, null)
            ?: return RestoreResult(GameState(), 0L, 0.0)

        return try {
            val json = JSONObject(raw)
            val state = GameState()
            state.restoreEconomy(
                cash = json.optDouble("cash", state.cash),
                gems = json.optInt("gems", state.gems),
                levels = parseLevels(json.optJSONArray("businesses"))
            )

            val savedAt = json.optLong("savedAtEpochMillis", nowEpochMillis)
            val elapsedSeconds = ((nowEpochMillis - savedAt).coerceAtLeast(0L) / 1_000L)
            val creditedSeconds = min(elapsedSeconds, MAX_OFFLINE_SECONDS)
            val earnings = state.totalIncomePerSecond * creditedSeconds.toDouble()
            state.addCash(earnings)

            RestoreResult(state, creditedSeconds, earnings)
        } catch (_: Exception) {
            RestoreResult(GameState(), 0L, 0.0)
        }
    }

    private fun parseLevels(array: JSONArray?): Map<String, Int> {
        if (array == null) return emptyMap()
        val result = mutableMapOf<String, Int>()
        for (i in 0 until array.length()) {
            val item = array.optJSONObject(i) ?: continue
            val id = item.optString("id")
            val level = item.optInt("level", 1).coerceAtLeast(1)
            if (id.isNotBlank()) result[id] = level
        }
        return result
    }

    companion object {
        private const val PREFS_NAME = "empire_tycoon_save"
        private const val KEY_SAVE = "game_state_v1"
        private const val MAX_OFFLINE_SECONDS = 8L * 60L * 60L
    }
}
