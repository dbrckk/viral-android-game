package com.empiretycoon.idleconquest.ui

object UiVisualStateMapper {
    fun businessCard(active: Boolean, canUpgrade: Boolean): String = when {
        active -> "milestone"
        canUpgrade -> "upgrade_ready"
        else -> "normal"
    }

    fun mission(claimed: Boolean, completed: Boolean, progress: Double): String = when {
        claimed -> "claimed"
        completed -> "complete"
        progress > 0.0 -> "active"
        else -> "locked"
    }

    fun manager(hired: Boolean, businessLevel: Int, unlockLevel: Int): String = when {
        hired -> "hired"
        businessLevel >= unlockLevel -> "available"
        else -> "locked"
    }

    fun permanentUpgrade(purchased: Boolean, businessLevel: Int, unlockLevel: Int): String = when {
        purchased -> "purchased"
        businessLevel >= unlockLevel -> "available"
        else -> "locked"
    }

    fun prestige(available: Boolean, crowns: Int): String = when {
        available -> "ready"
        crowns > 0 -> "owned"
        else -> "locked"
    }
}
