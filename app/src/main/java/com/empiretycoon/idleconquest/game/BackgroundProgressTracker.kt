package com.empiretycoon.idleconquest.game

class BackgroundProgressTracker {
    private var pausedAtEpochMillis: Long? = null

    fun pause(nowEpochMillis: Long) {
        if (pausedAtEpochMillis == null) pausedAtEpochMillis = nowEpochMillis
    }

    fun saveTimestamp(nowEpochMillis: Long): Long = pausedAtEpochMillis ?: nowEpochMillis

    fun resume(nowEpochMillis: Long, incomePerSecond: Double): OfflineProgress {
        val pausedAt = pausedAtEpochMillis ?: return OfflineProgress(0L, 0.0)
        pausedAtEpochMillis = null
        return OfflineProgressCalculator.calculate(nowEpochMillis, pausedAt, incomePerSecond)
    }
}
