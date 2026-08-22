package com.empiretycoon.idleconquest.game

enum class MissionMetric { TOTAL_LEVELS, BUSINESS_LEVEL, INCOME_PER_SECOND, MANAGERS_HIRED, UPGRADES_PURCHASED }
enum class MissionRewardType { CASH, GEMS }

data class MissionReward(val type: MissionRewardType, val amount: Double)
data class MissionDefinition(
    val id: String,
    val title: String,
    val description: String,
    val metric: MissionMetric,
    val target: Double,
    val businessId: String? = null,
    val reward: MissionReward,
    val icon: String
)

data class MissionState(val definition: MissionDefinition, val progress: Double, val completed: Boolean, val claimed: Boolean)
data class MissionClaimResult(val claimed: Boolean, val mission: MissionDefinition? = null)

object MissionCatalog {
    val all = listOf(
        MissionDefinition("first_25_levels","First Expansion","Reach 25 total business levels",MissionMetric.TOTAL_LEVELS,25.0,reward=MissionReward(MissionRewardType.CASH,2_500.0),icon="stack_up"),
        MissionDefinition("street_lv100","Street Empire","Reach Street Stand Lv.100",MissionMetric.BUSINESS_LEVEL,100.0,"street_stand",MissionReward(MissionRewardType.GEMS,5.0),"street_badge"),
        MissionDefinition("income_10k","Five Figures","Reach $10K income per second",MissionMetric.INCOME_PER_SECOND,10_000.0,reward=MissionReward(MissionRewardType.CASH,100_000.0),icon="income_wave"),
        MissionDefinition("hire_two","Build a Team","Hire 2 managers",MissionMetric.MANAGERS_HIRED,2.0,reward=MissionReward(MissionRewardType.GEMS,8.0),icon="team"),
        MissionDefinition("buy_four_upgrades","Systems Online","Buy 4 permanent upgrades",MissionMetric.UPGRADES_PURCHASED,4.0,reward=MissionReward(MissionRewardType.CASH,1_000_000.0),icon="chip_star"),
        MissionDefinition("factory_lv500","Industrial Titan","Reach Factory Lv.500",MissionMetric.BUSINESS_LEVEL,500.0,"factory",MissionReward(MissionRewardType.GEMS,25.0),"factory_crown")
    )
}
