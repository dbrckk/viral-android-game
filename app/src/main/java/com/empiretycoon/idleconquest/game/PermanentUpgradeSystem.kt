package com.empiretycoon.idleconquest.game

enum class PermanentUpgradeEffect { INCOME_MULTIPLIER, COST_MULTIPLIER }

data class PermanentUpgradeDefinition(
    val id: String,
    val name: String,
    val businessId: String,
    val unlockLevel: Int,
    val cost: Double,
    val effect: PermanentUpgradeEffect,
    val value: Double,
    val icon: String
)

data class PermanentUpgradeState(
    val definition: PermanentUpgradeDefinition,
    val purchased: Boolean
)

data class PermanentUpgradePurchaseResult(
    val purchased: Boolean,
    val upgrade: PermanentUpgradeDefinition? = null
)

object PermanentUpgradeCatalog {
    val all = listOf(
        PermanentUpgradeDefinition("street_solar_grill", "Solar Grill", "street_stand", 25, 5_000.0, PermanentUpgradeEffect.INCOME_MULTIPLIER, 2.0, "sun_flame"),
        PermanentUpgradeDefinition("street_bulk_supply", "Bulk Supply", "street_stand", 75, 35_000.0, PermanentUpgradeEffect.COST_MULTIPLIER, 0.90, "crate_down"),
        PermanentUpgradeDefinition("shop_holo_signage", "Holo Signage", "corner_shop", 100, 120_000.0, PermanentUpgradeEffect.INCOME_MULTIPLIER, 2.25, "holo_sign"),
        PermanentUpgradeDefinition("shop_auto_stock", "Auto Stock", "corner_shop", 175, 420_000.0, PermanentUpgradeEffect.COST_MULTIPLIER, 0.88, "box_cycle"),
        PermanentUpgradeDefinition("workshop_plasma_tools", "Plasma Tools", "workshop", 250, 1_500_000.0, PermanentUpgradeEffect.INCOME_MULTIPLIER, 2.5, "plasma_wrench"),
        PermanentUpgradeDefinition("workshop_modular_lines", "Modular Lines", "workshop", 400, 6_000_000.0, PermanentUpgradeEffect.COST_MULTIPLIER, 0.86, "conveyor_down"),
        PermanentUpgradeDefinition("factory_quantum_core", "Quantum Core", "factory", 500, 25_000_000.0, PermanentUpgradeEffect.INCOME_MULTIPLIER, 3.0, "quantum_core"),
        PermanentUpgradeDefinition("factory_predictive_ai", "Predictive AI", "factory", 750, 120_000_000.0, PermanentUpgradeEffect.COST_MULTIPLIER, 0.84, "ai_chip")
    )
}
