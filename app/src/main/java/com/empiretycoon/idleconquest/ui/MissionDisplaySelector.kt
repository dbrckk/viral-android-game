package com.empiretycoon.idleconquest.ui

import com.empiretycoon.idleconquest.game.MissionState

object MissionDisplaySelector {
    const val DEFAULT_MAX_VISIBLE = 6

    fun visible(
        missions: List<MissionState>,
        maxVisible: Int = DEFAULT_MAX_VISIBLE,
    ): List<MissionState> {
        val limit = maxVisible.coerceAtLeast(0)
        if (limit == 0 || missions.isEmpty()) return emptyList()

        val unclaimed = missions.filterNot { it.claimed }
        if (unclaimed.size >= limit) return unclaimed.take(limit)

        val claimedFallback = missions.asReversed().filter { it.claimed }
        return (unclaimed + claimedFallback).take(limit)
    }
}
