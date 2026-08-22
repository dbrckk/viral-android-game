package com.empiretycoon.idleconquest.art

import android.content.Context
import org.json.JSONObject

data class BusinessArtSelection(
    val businessId: String,
    val tier: String,
    val profile: String,
    val accent: String,
    val assetPath: String?
)

class BusinessArtResolver(private val context: Context) {
    private val runtimeIndex: JSONObject by lazy {
        context.assets.open("art/business/group_01/runtime-index.json")
            .bufferedReader()
            .use { JSONObject(it.readText()) }
    }

    fun resolve(
        businessId: String,
        tier: String = "base",
        profile: String = "full"
    ): BusinessArtSelection {
        val businesses = runtimeIndex.getJSONObject("businesses")
        val business = businesses.getJSONObject(businessId)
        val accent = business.optString("accent", "neutral")

        val preferredPath = "art/business/group_01/$businessId/$tier/default/full.png"
        val reducedPath = "art/business/group_01/$businessId/$tier/default/reduced_motion.png"
        val powerSavePath = "art/business/group_01/$businessId/$tier/default/power_save.png"

        val requested = when (profile) {
            "reduced_motion" -> reducedPath
            "power_save" -> powerSavePath
            else -> preferredPath
        }

        val resolved = when {
            assetExists(requested) -> requested
            profile != "full" && assetExists(preferredPath) -> preferredPath
            else -> null
        }

        return BusinessArtSelection(
            businessId = businessId,
            tier = tier,
            profile = profile,
            accent = accent,
            assetPath = resolved
        )
    }

    private fun assetExists(path: String): Boolean = try {
        context.assets.open(path).close()
        true
    } catch (_: Exception) {
        false
    }
}
