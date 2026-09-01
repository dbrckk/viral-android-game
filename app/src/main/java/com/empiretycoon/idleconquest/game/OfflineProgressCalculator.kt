package com.empiretycoon.idleconquest.game

data class OfflineProgress(
    val seconds: Long,
    val earnings: Double,
)

object OfflineProgressCalculator {
    const val MAX_OFFLINE_SECONDS = 8L * 60L * 60L

    fun calculate(
        nowEpochMillis: Long,
        savedAtEpochMillis: Long,
        incomePerSecond: Double,
    ): OfflineProgress {
        val elapsedMillis = when {
            savedAtEpochMillis >= nowEpochMillis -> 0L
            else -> runCatching { Math.subtractExact(nowEpochMillis, savedAtEpochMillis) }
                .getOrDefault(Long.MAX_VALUE)
        }
        val seconds = (elapsedMillis / 1_000L).coerceIn(0L, MAX_OFFLINE_SECONDS)
        val earnings = when {
            !incomePerSecond.isFinite() || incomePerSecond < 0.0 -> 0.0
            seconds == 0L || incomePerSecond == 0.0 -> 0.0
            else -> {
                val raw = incomePerSecond * seconds
                when {
                    raw.isFinite() && raw >= 0.0 -> raw
                    raw == Double.POSITIVE_INFINITY -> Double.MAX_VALUE
                    else -> 0.0
                }
            }
        }
        return OfflineProgress(seconds, earnings)
    }
}
