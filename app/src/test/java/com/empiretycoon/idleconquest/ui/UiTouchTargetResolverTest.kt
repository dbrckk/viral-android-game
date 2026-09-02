package com.empiretycoon.idleconquest.ui

import com.empiretycoon.idleconquest.game.BuyMode
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class UiTouchTargetResolverTest {
    private val box = UiBounds(10f, 20f, 30f, 40f)

    @Test
    fun `left and top edges are included while right and bottom are excluded`() {
        val map = UiTouchMap(prestige = box)

        assertEquals(UiTouchTarget.Prestige, UiTouchTargetResolver.resolve(10f, 20f, map))
        assertEquals(UiTouchTarget.Prestige, UiTouchTargetResolver.resolve(29.999f, 39.999f, map))
        assertNull(UiTouchTargetResolver.resolve(30f, 30f, map))
        assertNull(UiTouchTargetResolver.resolve(20f, 40f, map))
    }

    @Test
    fun `empty or reversed bounds are not hittable`() {
        assertNull(
            UiTouchTargetResolver.resolve(
                20f,
                30f,
                UiTouchMap(prestige = UiBounds(30f, 40f, 10f, 20f)),
            ),
        )
        assertNull(
            UiTouchTargetResolver.resolve(
                10f,
                20f,
                UiTouchMap(prestige = UiBounds(10f, 20f, 10f, 40f)),
            ),
        )
    }

    @Test
    fun `prestige keeps highest priority when targets overlap`() {
        val map = UiTouchMap(
            prestige = box,
            missions = listOf("mission" to box),
            buyModes = listOf(BuyMode.MAX to box),
            permanentUpgrades = listOf("upgrade" to box),
            managers = listOf("manager" to box),
            businessUpgrades = listOf(box),
        )

        assertEquals(UiTouchTarget.Prestige, UiTouchTargetResolver.resolve(20f, 30f, map))
    }

    @Test
    fun `target categories preserve view dispatch order`() {
        val empty = UiBounds(100f, 100f, 110f, 110f)
        val point = UiBounds(0f, 0f, 20f, 20f)

        assertEquals(
            UiTouchTarget.Mission("m1"),
            UiTouchTargetResolver.resolve(10f, 10f, UiTouchMap(empty, missions = listOf("m1" to point), buyModes = listOf(BuyMode.X10 to point))),
        )
        assertEquals(
            UiTouchTarget.BuyModeTarget(BuyMode.X10),
            UiTouchTargetResolver.resolve(10f, 10f, UiTouchMap(empty, buyModes = listOf(BuyMode.X10 to point), permanentUpgrades = listOf("u1" to point))),
        )
        assertEquals(
            UiTouchTarget.PermanentUpgrade("u1"),
            UiTouchTargetResolver.resolve(10f, 10f, UiTouchMap(empty, permanentUpgrades = listOf("u1" to point), managers = listOf("mgr" to point))),
        )
        assertEquals(
            UiTouchTarget.Manager("mgr"),
            UiTouchTargetResolver.resolve(10f, 10f, UiTouchMap(empty, managers = listOf("mgr" to point), businessUpgrades = listOf(point))),
        )
    }

    @Test
    fun `business target exposes matching index`() {
        val map = UiTouchMap(
            prestige = UiBounds(100f, 100f, 110f, 110f),
            businessUpgrades = listOf(
                UiBounds(0f, 0f, 5f, 5f),
                UiBounds(10f, 10f, 20f, 20f),
            ),
        )

        assertEquals(
            UiTouchTarget.BusinessUpgrade(1),
            UiTouchTargetResolver.resolve(15f, 15f, map),
        )
    }

    @Test
    fun `outside all targets returns null`() {
        val map = UiTouchMap(prestige = box)

        assertNull(UiTouchTargetResolver.resolve(0f, 0f, map))
    }
}
