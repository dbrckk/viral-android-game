package com.empiretycoon.idleconquest

import android.app.Activity
import android.os.Bundle
import com.empiretycoon.idleconquest.ui.BusinessShowcaseView

class MainActivity : Activity() {
    private lateinit var gameView: BusinessShowcaseView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        gameView = BusinessShowcaseView(this)
        setContentView(gameView)
    }

    override fun onPause() {
        gameView.persistNow()
        super.onPause()
    }

    override fun onStop() {
        gameView.persistNow()
        super.onStop()
    }
}
