package com.empiretycoon.idleconquest.art

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import java.util.concurrent.ConcurrentHashMap

/**
 * Centralized loader for raster assets.
 *
 * Supports the legacy Base64-wrapped WebP files as well as direct binary raster files.
 * Successfully decoded bitmaps are cached by asset path for the lifetime of the process.
 */
object RasterAssetLoader {
    private val cache = ConcurrentHashMap<String, Bitmap>()
    private val failedPaths = ConcurrentHashMap.newKeySet<String>()

    fun load(context: Context, assetPath: String): Bitmap? {
        cache[assetPath]?.let { return it }
        if (assetPath in failedPaths) return null

        val bitmap = runCatching {
            if (assetPath.endsWith(".b64", ignoreCase = true)) {
                val encoded = context.assets.open(assetPath)
                    .bufferedReader()
                    .use { it.readText().trim() }
                val bytes = Base64.decode(encoded, Base64.DEFAULT)
                BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
            } else {
                context.assets.open(assetPath).use(BitmapFactory::decodeStream)
            }
        }.getOrNull()

        if (bitmap != null) {
            cache.putIfAbsent(assetPath, bitmap)
            return cache[assetPath] ?: bitmap
        }

        failedPaths += assetPath
        return null
    }
}
