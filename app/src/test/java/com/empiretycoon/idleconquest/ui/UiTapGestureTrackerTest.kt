package com.empiretycoon.idleconquest.ui

import com.empiretycoon.idleconquest.game.BuyMode
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class UiTapGestureTrackerTest {
    @Test
    fun sameTargetPressAndReleaseProducesTap() {
        val tracker = UiTapGestureTracker()
        val target = UiTouchTarget.BuyModeTarget(BuyMode.X10)

        tracker.press(target)

        assertEquals(target, tracker.release(target))
    }

    @Test
    fun releaseOnDifferentTargetIsRejected() {
        val tracker = UiTapGestureTracker()
        tracker.press(UiTouchTarget.BusinessUpgrade(0))

        assertNull(tracker.release(UiTouchTarget.BusinessUpgrade(1)))
    }

    @Test
    fun pressOutsideThenReleaseInsideIsRejected() {
        val tracker = UiTapGestureTracker()
        tracker.press(null)

        assertNull(tracker.release(UiTouchTarget.Prestige))
    }

    @Test
    fun cancelClearsPressedTarget() {
        val tracker = UiTapGestureTracker()
        tracker.press(UiTouchTarget.Mission("mission"))
        tracker.cancel()

        assertNull(tracker.release(UiTouchTarget.Mission("mission")))
    }

    @Test
    fun releaseConsumesPressedTarget() {
        val tracker = UiTapGestureTracker()
        tracker.press(UiTouchTarget.Manager("manager"))
        tracker.release(UiTouchTarget.Manager("manager"))

        assertNull(tracker.release(UiTouchTarget.Manager("manager")))
    }
}
