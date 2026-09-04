package com.empiretycoon.idleconquest.ui

import android.graphics.Rect
import android.os.Bundle
import android.view.View
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import android.view.accessibility.AccessibilityNodeProvider
import kotlin.math.ceil
import kotlin.math.floor

class CanvasAccessibilityNodeProvider(
    private val host: View,
    private val nodesProvider: () -> List<UiAccessibilityNode>,
    private val clickTarget: (UiTouchTarget) -> Boolean,
) : AccessibilityNodeProvider() {

    override fun createAccessibilityNodeInfo(virtualViewId: Int): AccessibilityNodeInfo? {
        val nodes = nodesProvider()
        if (virtualViewId == HOST_VIEW_ID) {
            return AccessibilityNodeInfo.obtain(host).apply {
                host.onInitializeAccessibilityNodeInfo(this)
                isClickable = false
                isFocusable = false
                contentDescription = null
                nodes.forEach { addChild(host, it.virtualId) }
            }
        }

        val node = nodes.firstOrNull { it.virtualId == virtualViewId } ?: return null
        return AccessibilityNodeInfo.obtain().apply {
            setSource(host, node.virtualId)
            setParent(host)
            packageName = host.context.packageName
            className = "android.widget.Button"
            contentDescription = node.label
            isEnabled = host.isEnabled
            isClickable = true
            isFocusable = true
            isVisibleToUser = host.visibility == View.VISIBLE && host.alpha > 0f
            setBoundsInParent(node.bounds.toRect())

            val location = IntArray(2)
            host.getLocationOnScreen(location)
            val local = node.bounds.toRect()
            setBoundsInScreen(
                Rect(
                    location[0] + local.left,
                    location[1] + local.top,
                    location[0] + local.right,
                    location[1] + local.bottom,
                )
            )
            addAction(AccessibilityNodeInfo.AccessibilityAction.ACTION_CLICK)
        }
    }

    override fun performAction(virtualViewId: Int, action: Int, arguments: Bundle?): Boolean {
        if (virtualViewId == HOST_VIEW_ID) {
            return host.performAccessibilityAction(action, arguments)
        }
        if (action != AccessibilityNodeInfo.ACTION_CLICK) return false

        val node = nodesProvider().firstOrNull { it.virtualId == virtualViewId } ?: return false
        if (!clickTarget(node.target)) return false

        sendVirtualEvent(node, AccessibilityEvent.TYPE_VIEW_CLICKED)
        host.sendAccessibilityEvent(AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED)
        return true
    }

    override fun findAccessibilityNodeInfosByText(
        searched: String?,
        virtualViewId: Int,
    ): List<AccessibilityNodeInfo> {
        val query = searched?.trim()?.takeIf { it.isNotEmpty() } ?: return emptyList()
        return nodesProvider()
            .asSequence()
            .filter { it.label.contains(query, ignoreCase = true) }
            .mapNotNull { createAccessibilityNodeInfo(it.virtualId) }
            .toList()
    }

    private fun sendVirtualEvent(node: UiAccessibilityNode, eventType: Int) {
        val parent = host.parent ?: return
        val event = AccessibilityEvent.obtain(eventType).apply {
            packageName = host.context.packageName
            className = "android.widget.Button"
            contentDescription = node.label
            setSource(host, node.virtualId)
            isEnabled = host.isEnabled
        }
        parent.requestSendAccessibilityEvent(host, event)
    }

    private fun UiBounds.toRect(): Rect = Rect(
        floor(left).toInt(),
        floor(top).toInt(),
        ceil(right).toInt(),
        ceil(bottom).toInt(),
    )

    private companion object {
        const val HOST_VIEW_ID = -1
    }
}
