package com.empiretycoon.idleconquest.game

class BackgroundSessionProgress {
    private var pausedAtEpochMillis: Long? = null

    fun markPaused(nowEpochMillis: Long) {
        pausedAtEpochMillis = nowEpochMillis
    }

    fun consumeResume(nowEpochMillis: Long, incomePerSecond: Double): OfflineProgress? {
        val pausedAt = pausedAtEpochMillis ?: return null
        pausedAtEpochMillis = null
        return OfflineProgressCalculator.calculate(nowEpochMillis, pausedAt, incomePerSecond)
    }
}
