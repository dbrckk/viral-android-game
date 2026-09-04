package com.empiretycoon.idleconquest.ui

import android.content.Context
import android.graphics.Rect
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityManager
import android.view.accessibility.AccessibilityNodeInfo
import android.view.accessibility.AccessibilityNodeProvider
import kotlin.math.ceil
import kotlin.math.floor

class CanvasAccessibilityNodeProvider(
    private val host: View,
    private val nodesProvider: () -> List<UiAccessibilityNode>,
    private val clickTarget: (UiTouchTarget) -> Boolean,
) : AccessibilityNodeProvider() {
    private val accessibilityManager =
        host.context.getSystemService(Context.ACCESSIBILITY_SERVICE) as? AccessibilityManager
    private var accessibilityFocusedId: Int? = null
    private var hoveredId: Int? = null

    override fun createAccessibilityNodeInfo(virtualViewId: Int): AccessibilityNodeInfo? {
        val nodes = currentNodes()
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
            isAccessibilityFocused = accessibilityFocusedId == node.virtualId
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
            if (isAccessibilityFocused) {
                addAction(AccessibilityNodeInfo.AccessibilityAction.ACTION_CLEAR_ACCESSIBILITY_FOCUS)
            } else {
                addAction(AccessibilityNodeInfo.AccessibilityAction.ACTION_ACCESSIBILITY_FOCUS)
            }
        }
    }

    override fun performAction(virtualViewId: Int, action: Int, arguments: Bundle?): Boolean {
        if (virtualViewId == HOST_VIEW_ID) {
            return host.performAccessibilityAction(action, arguments)
        }

        val node = currentNodes().firstOrNull { it.virtualId == virtualViewId } ?: return false
        return when (action) {
            AccessibilityNodeInfo.ACTION_CLICK -> {
                if (!clickTarget(node.target)) return false
                sendVirtualEvent(node, AccessibilityEvent.TYPE_VIEW_CLICKED)
                host.sendAccessibilityEvent(AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED)
                true
            }

            AccessibilityNodeInfo.ACTION_ACCESSIBILITY_FOCUS -> requestAccessibilityFocus(node)
            AccessibilityNodeInfo.ACTION_CLEAR_ACCESSIBILITY_FOCUS -> clearAccessibilityFocus(node)
            else -> false
        }
    }

    override fun findFocus(focus: Int): AccessibilityNodeInfo? {
        if (focus != AccessibilityNodeInfo.FOCUS_ACCESSIBILITY) return null
        val focusedId = accessibilityFocusedId ?: return null
        return createAccessibilityNodeInfo(focusedId)
    }

    override fun findAccessibilityNodeInfosByText(
        searched: String?,
        virtualViewId: Int,
    ): List<AccessibilityNodeInfo> {
        val query = searched?.trim()?.takeIf { it.isNotEmpty() } ?: return emptyList()
        return currentNodes()
            .asSequence()
            .filter { it.label.contains(query, ignoreCase = true) }
            .mapNotNull { createAccessibilityNodeInfo(it.virtualId) }
            .toList()
    }

    fun dispatchHoverEvent(event: MotionEvent): Boolean {
        if (accessibilityManager?.isTouchExplorationEnabled != true) return false
        return when (event.actionMasked) {
            MotionEvent.ACTION_HOVER_ENTER,
            MotionEvent.ACTION_HOVER_MOVE -> {
                val node = currentNodes().firstOrNull { it.bounds.contains(event.x, event.y) }
                updateHoveredNode(node)
                node != null
            }

            MotionEvent.ACTION_HOVER_EXIT -> {
                val handled = hoveredId != null
                updateHoveredNode(null)
                handled
            }

            else -> false
        }
    }

    private fun requestAccessibilityFocus(node: UiAccessibilityNode): Boolean {
        if (accessibilityManager?.isEnabled != true) return false
        if (accessibilityFocusedId == node.virtualId) return false

        accessibilityFocusedId?.let { oldId ->
            currentNodes().firstOrNull { it.virtualId == oldId }
                ?.let { sendVirtualEvent(it, AccessibilityEvent.TYPE_VIEW_ACCESSIBILITY_FOCUS_CLEARED) }
        }
        accessibilityFocusedId = node.virtualId
        sendVirtualEvent(node, AccessibilityEvent.TYPE_VIEW_ACCESSIBILITY_FOCUSED)
        host.invalidate()
        return true
    }

    private fun clearAccessibilityFocus(node: UiAccessibilityNode): Boolean {
        if (accessibilityFocusedId != node.virtualId) return false
        accessibilityFocusedId = null
        sendVirtualEvent(node, AccessibilityEvent.TYPE_VIEW_ACCESSIBILITY_FOCUS_CLEARED)
        host.invalidate()
        return true
    }

    private fun updateHoveredNode(node: UiAccessibilityNode?) {
        if (hoveredId == node?.virtualId) return
        val nodes = currentNodes()
        hoveredId?.let { oldId ->
            nodes.firstOrNull { it.virtualId == oldId }
                ?.let { sendVirtualEvent(it, AccessibilityEvent.TYPE_VIEW_HOVER_EXIT) }
        }
        hoveredId = node?.virtualId
        node?.let { sendVirtualEvent(it, AccessibilityEvent.TYPE_VIEW_HOVER_ENTER) }
    }

    private fun currentNodes(): List<UiAccessibilityNode> {
        val nodes = nodesProvider()
        if (accessibilityFocusedId != null && nodes.none { it.virtualId == accessibilityFocusedId }) {
            accessibilityFocusedId = null
        }
        if (hoveredId != null && nodes.none { it.virtualId == hoveredId }) {
            hoveredId = null
        }
        return nodes
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

    private fun UiBounds.contains(x: Float, y: Float): Boolean =
        left < right && top < bottom && x >= left && x < right && y >= top && y < bottom

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
