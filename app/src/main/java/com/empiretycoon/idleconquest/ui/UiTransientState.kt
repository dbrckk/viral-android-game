package com.empiretycoon.idleconquest.ui

class UiTransientState {
    private var bannerText: String? = null
    private var bannerUntilNanos: Long = 0L
    private var highlightedBusinessId: String? = null
    private var highlightUntilNanos: Long = 0L

    fun showBanner(text: String, durationNanos: Long, nowNanos: Long) {
        bannerText = text
        bannerUntilNanos = safeDeadline(nowNanos, durationNanos)
    }

    fun bannerAt(nowNanos: Long): String? =
        bannerText?.takeIf { nowNanos < bannerUntilNanos }

    fun highlightBusiness(businessId: String, durationNanos: Long, nowNanos: Long) {
        highlightedBusinessId = businessId
        highlightUntilNanos = safeDeadline(nowNanos, durationNanos)
    }

    fun clearHighlight() {
        highlightedBusinessId = null
        highlightUntilNanos = 0L
    }

    fun isHighlighted(businessId: String, nowNanos: Long): Boolean =
        highlightedBusinessId == businessId && nowNanos < highlightUntilNanos

    private fun safeDeadline(nowNanos: Long, durationNanos: Long): Long {
        val safeDuration = durationNanos.coerceAtLeast(0L)
        return if (nowNanos > Long.MAX_VALUE - safeDuration) Long.MAX_VALUE
        else nowNanos + safeDuration
    }
}
