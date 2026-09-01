package com.empiretycoon.idleconquest.ui

import org.junit.Assert.assertTrue
import org.junit.Test

class ShowcaseLayoutEdgeCaseTest {
    @Test
    fun tinyWindowNeverProducesNegativeCardHeights() {
        val layout = ShowcaseLayout.frame(320, 120, true, 4)
        assertTrue(layout.businessCards.all { it.bottom >= it.top })
        assertTrue(layout.hud.right >= layout.hud.left)
        assertTrue(layout.modes.right >= layout.modes.left)
        assertTrue(layout.missions.right >= layout.missions.left)
    }

    @Test
    fun negativeDimensionsAreClampedSafely() {
        val layout = ShowcaseLayout.frame(-100, -100, false, 2)
        assertTrue(layout.businessCards.all { it.bottom >= it.top && it.right >= it.left })
        assertTrue(layout.hud.right >= layout.hud.left)
    }
}
