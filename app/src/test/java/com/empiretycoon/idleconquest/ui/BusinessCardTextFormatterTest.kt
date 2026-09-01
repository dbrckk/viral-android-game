package com.empiretycoon.idleconquest.ui

import org.junit.Assert.assertEquals
import org.junit.Test

class BusinessCardTextFormatterTest {
    @Test
    fun formatsLevelIncomeAndPermanentText() {
        assertEquals("Lv.25 • LV25 • ×2", BusinessCardTextFormatter.level(25, "lv25", 2.0))
        assertEquals("+1.50K/s", BusinessCardTextFormatter.income(1_500.0))
        assertEquals("PERM income ×1.50 • cost ×0.90", BusinessCardTextFormatter.permanent(1.5, 0.9))
    }

    @Test
    fun formatsManagerStates() {
        assertEquals(
            "MANAGER UNAVAILABLE",
            BusinessCardTextFormatter.manager(null, false, 1, 10, 100.0, 2.0),
        )
        assertEquals(
            "MANAGER Lv.15",
            BusinessCardTextFormatter.manager("Mia Flux", false, 10, 15, 750.0, 1.5),
        )
        assertEquals(
            "HIRE Mia Flux $750",
            BusinessCardTextFormatter.manager("Mia Flux", false, 15, 15, 750.0, 1.5),
        )
        assertEquals(
            "Mia Flux ×1.50",
            BusinessCardTextFormatter.manager("Mia Flux", true, 15, 15, 750.0, 1.5),
        )
    }
}
