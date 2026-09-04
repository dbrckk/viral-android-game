package com.empiretycoon.idleconquest

import android.content.ComponentCallbacks2
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class MemoryPressurePolicyTest {
    @Test
    fun ordinaryUiHiddenDoesNotClearRasterCache() {
        assertFalse(
            MemoryPressurePolicy.shouldClearRasterCache(ComponentCallbacks2.TRIM_MEMORY_UI_HIDDEN)
        )
    }

    @Test
    fun activeMemoryPressureClearsRasterCache() {
        assertTrue(
            MemoryPressurePolicy.shouldClearRasterCache(ComponentCallbacks2.TRIM_MEMORY_RUNNING_LOW)
        )
        assertTrue(
            MemoryPressurePolicy.shouldClearRasterCache(ComponentCallbacks2.TRIM_MEMORY_RUNNING_CRITICAL)
        )
    }

    @Test
    fun backgroundMemoryPressureClearsRasterCache() {
        assertTrue(
            MemoryPressurePolicy.shouldClearRasterCache(ComponentCallbacks2.TRIM_MEMORY_BACKGROUND)
        )
        assertTrue(
            MemoryPressurePolicy.shouldClearRasterCache(ComponentCallbacks2.TRIM_MEMORY_COMPLETE)
        )
    }

    @Test
    fun mildRunningPressureKeepsRasterCache() {
        assertFalse(
            MemoryPressurePolicy.shouldClearRasterCache(ComponentCallbacks2.TRIM_MEMORY_RUNNING_MODERATE)
        )
    }
}
