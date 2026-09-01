package com.empiretycoon.idleconquest.ui

import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class UiTransientStateTest {
    @Test
    fun bannerExpiresIndependently() {
        val state = UiTransientState()
        state.showBanner("HELLO", 100L, 1_000L)

        assertEquals("HELLO", state.bannerAt(1_050L))
        assertNull(state.bannerAt(1_100L))
    }

    @Test
    fun laterBannerDoesNotReactivateExpiredHighlight() {
        val state = UiTransientState()
        state.highlightBusiness("street_stand", 100L, 1_000L)

        assertTrue(state.isHighlighted("street_stand", 1_050L))
        assertFalse(state.isHighlighted("street_stand", 1_100L))

        state.showBanner("MISSION COMPLETE", 500L, 2_000L)

        assertEquals("MISSION COMPLETE", state.bannerAt(2_100L))
        assertFalse(state.isHighlighted("street_stand", 2_100L))
    }

    @Test
    fun clearHighlightStopsItImmediately() {
        val state = UiTransientState()
        state.highlightBusiness("factory", 1_000L, 10L)
        state.clearHighlight()

        assertFalse(state.isHighlighted("factory", 11L))
    }

    @Test
    fun deadlineSaturatesInsteadOfOverflowing() {
        val state = UiTransientState()
        state.showBanner("SAFE", 100L, Long.MAX_VALUE - 10L)

        assertEquals("SAFE", state.bannerAt(Long.MAX_VALUE - 1L))
    }

    @Test
    fun negativeNanoTimeOriginRemainsValid() {
        val state = UiTransientState()
        state.showBanner("NEGATIVE", 100L, -1_000L)

        assertEquals("NEGATIVE", state.bannerAt(-950L))
        assertNull(state.bannerAt(-900L))
    }
}
