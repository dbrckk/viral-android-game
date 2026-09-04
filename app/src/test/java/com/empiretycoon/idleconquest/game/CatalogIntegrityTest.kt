package com.empiretycoon.idleconquest.game

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class CatalogIntegrityTest {
    private val businessIds = GameState().businesses.map { it.id }.toSet()
    private val missionBadgeStyles = setOf(
        "stack_up",
        "street_badge",
        "income_wave",
        "team",
        "chip_star",
        "factory_crown",
    )

    @Test
    fun catalogIdsAreUnique() {
        assertUnique(ManagerCatalog.all.map { it.id })
        assertUnique(PermanentUpgradeCatalog.all.map { it.id })
        assertUnique(MissionCatalog.all.map { it.id })
    }

    @Test
    fun everyBusinessHasExactlyOneManagerAndManagersReferenceKnownBusinesses() {
        assertTrue(ManagerCatalog.all.all { it.businessId in businessIds })
        businessIds.forEach { businessId ->
            assertEquals(1, ManagerCatalog.all.count { it.businessId == businessId })
        }
    }

    @Test
    fun permanentUpgradesReferenceKnownBusinessesAndHaveValidEconomyValues() {
        assertTrue(PermanentUpgradeCatalog.all.all { it.businessId in businessIds })
        assertTrue(PermanentUpgradeCatalog.all.all { it.unlockLevel > 0 })
        assertTrue(PermanentUpgradeCatalog.all.all { it.cost.isFinite() && it.cost > 0.0 })
        assertTrue(PermanentUpgradeCatalog.all.all { it.value.isFinite() && it.value > 0.0 })
    }

    @Test
    fun missionsHaveValidTargetsRewardsBusinessReferencesAndBadgeStyles() {
        MissionCatalog.all.forEach { mission ->
            assertTrue(mission.target.isFinite() && mission.target > 0.0)
            assertTrue(mission.reward.amount.isFinite() && mission.reward.amount > 0.0)
            assertTrue(mission.icon in missionBadgeStyles)
            if (mission.metric == MissionMetric.BUSINESS_LEVEL) {
                assertTrue(mission.businessId in businessIds)
            } else {
                assertTrue(mission.businessId == null)
            }
        }
    }

    @Test
    fun managerEconomyValuesAreValid() {
        assertTrue(ManagerCatalog.all.all { it.unlockLevel > 0 })
        assertTrue(ManagerCatalog.all.all { it.cost.isFinite() && it.cost > 0.0 })
        assertTrue(ManagerCatalog.all.all { it.incomeMultiplier.isFinite() && it.incomeMultiplier >= 1.0 })
    }

    private fun assertUnique(ids: List<String>) {
        assertTrue(ids.all { it.isNotBlank() })
        assertEquals(ids.size, ids.toSet().size)
    }
}
