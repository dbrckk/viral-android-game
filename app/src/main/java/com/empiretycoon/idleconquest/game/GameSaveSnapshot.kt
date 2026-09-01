package com.empiretycoon.idleconquest.game

data class GameSaveSnapshot(
    val schemaVersion: Int,
    val cash: Double,
    val gems: Int,
    val levels: Map<String, Int>,
    val hiredManagers: Set<String> = emptySet(),
    val permanentUpgrades: Set<String> = emptySet(),
    val claimedMissions: Set<String> = emptySet(),
    val prestigeCrowns: Int = 0,
    val runEarnings: Double = 0.0,
    val savedAtEpochMillis: Long,
)

object GameSaveRestorer {
    const val CURRENT_SCHEMA_VERSION = 5

    fun restore(snapshot: GameSaveSnapshot, nowEpochMillis: Long): RestoreResult? {
        if (snapshot.schemaVersion !in 1..CURRENT_SCHEMA_VERSION) return null

        val state = GameState()
        state.restoreEconomy(
            cash = finiteOr(snapshot.cash, state.cash),
            gems = snapshot.gems,
            levels = snapshot.levels,
            hiredManagers = snapshot.hiredManagers,
            permanentUpgrades = snapshot.permanentUpgrades,
            claimedMissions = snapshot.claimedMissions,
            prestigeCrowns = snapshot.prestigeCrowns,
            runEarnings = finiteOr(snapshot.runEarnings, 0.0),
        )

        val offline = OfflineProgressCalculator.calculate(
            nowEpochMillis = nowEpochMillis,
            savedAtEpochMillis = snapshot.savedAtEpochMillis,
            incomePerSecond = state.totalIncomePerSecond,
        )
        state.addCash(offline.earnings, countForPrestige = true)
        return RestoreResult(state, offline.seconds, offline.earnings)
    }

    private fun finiteOr(value: Double, fallback: Double): Double =
        if (value.isFinite() && value >= 0.0) value else fallback
}
