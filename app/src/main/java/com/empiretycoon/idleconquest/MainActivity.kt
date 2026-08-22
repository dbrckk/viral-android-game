package com.empiretycoon.idleconquest

import android.app.Activity
import android.os.Bundle
import com.empiretycoon.idleconquest.ui.BusinessShowcaseView

class MainActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(BusinessShowcaseView(this))
    }
}
