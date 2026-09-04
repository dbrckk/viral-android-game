package com.empiretycoon.idleconquest.ui

object GameLoopTiming {
    const val REDRAW_DELAY_MILLIS = 50L
    const val POWER_SAVE_REDRAW_DELAY_MILLIS = 150L
    const val AUTOSAVE_INTERVAL_NANOS = 10_000_000_000L

    fun redrawDelayMillis(powerSaveMode: Boolean): Long =
        if (powerSaveMode) POWER_SAVE_REDRAW_DELAY_MILLIS else REDRAW_DELAY_MILLIS

    fun frameDeltaSeconds(nowNanos: Long, previousNanos: Long): Double {
        if (previousNanos == 0L || nowNanos <= previousNanos) return 0.0
        val elapsedNanos = runCatching { Math.subtractExact(nowNanos, previousNanos) }
            .getOrDefault(Long.MAX_VALUE)
        return (elapsedNanos / 1_000_000_000.0).coerceAtLeast(0.0)
    }

    fun shouldAutosave(nowNanos: Long, lastSaveNanos: Long): Boolean {
        if (lastSaveNanos == 0L) return true
        if (nowNanos <= lastSaveNanos) return false
        val elapsedNanos = runCatching { Math.subtractExact(nowNanos, lastSaveNanos) }
            .getOrDefault(Long.MAX_VALUE)
        return elapsedNanos >= AUTOSAVE_INTERVAL_NANOS
    }
}
