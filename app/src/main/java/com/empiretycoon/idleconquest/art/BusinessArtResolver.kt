package com.empiretycoon.idleconquest.art

import android.content.Context
import org.json.JSONObject

data class BusinessArtSelection(
    val businessId: String,
    val tier: String,
    val profile: String,
    val accent: String,
    val layerPaths: List<String>
)

class BusinessArtResolver(private val context: Context) {
    private val runtimeIndex: JSONObject by lazy {
        context.assets.open("art/business/group_01/runtime-index.json")
            .bufferedReader()
            .use { JSONObject(it.readText()) }
    }
    private val selectionCache = mutableMapOf<String, BusinessArtSelection>()
    private val existenceCache = mutableMapOf<String, Boolean>()

    fun resolve(
        businessId: String,
        tier: String = "base",
        state: String = "default",
        profile: String = "full"
    ): BusinessArtSelection {
        val key = "$businessId|$tier|$state|$profile"
        return selectionCache.getOrPut(key) {
            val businesses = runtimeIndex.getJSONObject("businesses")
            val business = businesses.getJSONObject(businessId)
            val accent = business.optString("accent", "neutral")
            val layers = runtimeIndex.getJSONArray("layers")
            val profiles = profileFallbacks(profile)
            val states = if (state == "default") listOf("default") else listOf(state, "default")

            val paths = buildList {
                for (layerIndex in 0 until layers.length()) {
                    val layer = layers.getString(layerIndex)
                    val path = firstExistingPath(businessId, tier, states, layer, profiles)
                    if (path != null) add(path)
                }
            }

            BusinessArtSelection(
                businessId = businessId,
                tier = tier,
                profile = profile,
                accent = accent,
                layerPaths = paths
            )
        }
    }

    private fun profileFallbacks(profile: String): List<String> = when (profile) {
        "reduced_motion" -> listOf("reduced_motion", "full")
        "power_save" -> listOf("power_save", "full")
        else -> listOf("full")
    }

    private fun firstExistingPath(
        businessId: String,
        tier: String,
        states: List<String>,
        layer: String,
        profiles: List<String>
    ): String? {
        for (state in states) {
            for (profile in profiles) {
                val filename = "${businessId}__${tier}__${state}__${layer}__${profile}.png"
                val path = "art/business/group_01/$filename"
                if (assetExists(path)) return path
            }
        }
        return null
    }

    private fun assetExists(path: String): Boolean = existenceCache.getOrPut(path) {
        try {
            context.assets.open(path).close()
            true
        } catch (_: Exception) {
            false
        }
    }
}
