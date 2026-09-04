package com.empiretycoon.idleconquest

import android.content.ComponentCallbacks2

object MemoryPressurePolicy {
    fun shouldClearRasterCache(level: Int): Boolean {
        val runningPressure = level in
            ComponentCallbacks2.TRIM_MEMORY_RUNNING_LOW..ComponentCallbacks2.TRIM_MEMORY_RUNNING_CRITICAL
        val backgroundPressure = level >= ComponentCallbacks2.TRIM_MEMORY_BACKGROUND
        return runningPressure || backgroundPressure
    }
}
