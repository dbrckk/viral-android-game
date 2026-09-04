package com.empiretycoon.idleconquest.ui

class UiTapGestureTracker {
    private var pressedTarget: UiTouchTarget? = null

    fun press(target: UiTouchTarget?) {
        pressedTarget = target
    }

    fun release(target: UiTouchTarget?): UiTouchTarget? {
        val result = target?.takeIf { it == pressedTarget }
        pressedTarget = null
        return result
    }

    fun cancel() {
        pressedTarget = null
    }
}
