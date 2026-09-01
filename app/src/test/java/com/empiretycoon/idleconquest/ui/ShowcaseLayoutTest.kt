package com.empiretycoon.idleconquest.ui

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ShowcaseLayoutTest {
    @Test
    fun frameWithoutBannerBuildsFourNonOverlappingCards() {
        val layout = ShowcaseLayout.frame(
            width = 1_000,
            height = 2_000,
            bannerVisible = false,
            businessCount = 4,
        )

        assertNull(layout.banner)
        assertEquals(4, layout.businessCards.size)
        assertEquals(40f, layout.hud.left, 0f)
        assertEquals(960f, layout.hud.right, 0f)
        for (index in 1 until layout.businessCards.size) {
            assertTrue(layout.businessCards[index].top > layout.businessCards[index - 1].bottom)
        }
        assertTrue(layout.businessCards.last().bottom <= 2_000f)
    }

    @Test
    fun visibleBannerPushesBusinessCardsDownByBannerHeight() {
        val hidden = ShowcaseLayout.frame(1_000, 2_000, false, 4)
        val visible = ShowcaseLayout.frame(1_000, 2_000, true, 4)

        assertEquals(76f, visible.banner!!.bottom - visible.banner.top, 0.001f)
        assertEquals(76f, visible.businessCards.first().top - hidden.businessCards.first().top, 0.001f)
    }

    @Test
    fun safeAreaKeepsInteractiveLayoutInsideSystemInsets() {
        val safeArea = UiBounds(
            left = 24f,
            top = 96f,
            right = 976f,
            bottom = 1_880f,
        )
        val layout = ShowcaseLayout.frame(safeArea, bannerVisible = true, businessCount = 4)

        assertTrue(layout.hud.left >= safeArea.left)
        assertTrue(layout.hud.top >= safeArea.top)
        assertTrue(layout.hud.right <= safeArea.right)
        assertTrue(layout.businessCards.last().bottom <= safeArea.bottom)
        layout.businessCards.forEach { card ->
            assertTrue(card.left >= safeArea.left)
            assertTrue(card.right <= safeArea.right)
        }
    }

    @Test
    fun safeAreaOffsetsLayoutInsteadOfShrinkingTowardScreenOrigin() {
        val base = ShowcaseLayout.frame(
            UiBounds(0f, 0f, 900f, 1_600f),
            bannerVisible = false,
            businessCount = 4,
        )
        val shifted = ShowcaseLayout.frame(
            UiBounds(30f, 80f, 930f, 1_680f),
            bannerVisible = false,
            businessCount = 4,
        )

        assertEquals(30f, shifted.hud.left - base.hud.left, 0.001f)
        assertEquals(80f, shifted.hud.top - base.hud.top, 0.001f)
        assertEquals(30f, shifted.businessCards.first().left - base.businessCards.first().left, 0.001f)
        assertEquals(80f, shifted.businessCards.first().top - base.businessCards.first().top, 0.001f)
    }

    @Test
    fun denseBusinessStacksRemainBounded() {
        val layout = ShowcaseLayout.frame(480, 800, false, 100)

        assertEquals(100, layout.businessCards.size)
        layout.businessCards.forEach { card ->
            assertTrue(card.bottom >= card.top)
            assertTrue(card.bottom <= 800f)
        }
        layout.businessCards.zipWithNext().forEach { (first, second) ->
            assertTrue(second.top >= first.bottom)
        }
    }

    @Test
    fun veryWideViewportUsesHeightBoundedMargin() {
        val layout = ShowcaseLayout.frame(2_400, 400, false, 4)

        assertEquals(16f, layout.hud.left, 0.001f)
        assertEquals(2_384f, layout.hud.right, 0.001f)
        assertTrue(layout.businessCards.first().top < 400f)
        assertTrue(layout.businessCards.last().bottom <= 400f)
        layout.businessCards.forEach { card -> assertTrue(card.bottom >= card.top) }
    }

    @Test
    fun zeroBusinessesReturnsNoCards() {
        val layout = ShowcaseLayout.frame(1_000, 2_000, false, 0)
        assertTrue(layout.businessCards.isEmpty())
    }
}
