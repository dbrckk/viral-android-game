package com.empiretycoon.idleconquest

import android.app.Activity
import android.os.Bundle
import com.empiretycoon.idleconquest.art.RasterAssetLoader
import com.empiretycoon.idleconquest.ui.BusinessShowcaseView

class MainActivity : Activity() {
    private lateinit var gameView: BusinessShowcaseView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        gameView = BusinessShowcaseView(this).apply {
            keepScreenOn = false
        }
        setContentView(gameView)
    }

    override fun onResume() {
        super.onResume()
        gameView.resumeFromBackground()
    }

    override fun onPause() {
        gameView.pauseForBackground()
        super.onPause()
    }

    override fun onTrimMemory(level: Int) {
        super.onTrimMemory(level)
        if (MemoryPressurePolicy.shouldClearRasterCache(level)) {
            RasterAssetLoader.clearMemoryCache()
        }
    }

    override fun onLowMemory() {
        RasterAssetLoader.clearMemoryCache()
        super.onLowMemory()
    }
}
