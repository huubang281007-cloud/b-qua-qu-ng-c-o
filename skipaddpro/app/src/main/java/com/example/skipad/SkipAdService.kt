package com.example.skipad

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.GestureDescription
import android.graphics.Path
import android.graphics.Rect
import android.os.SystemClock
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import android.widget.Toast
import java.util.ArrayDeque
import java.util.Locale

class SkipAdService : AccessibilityService() {

    private var lastScan = 0L
    private var lastClick = 0L

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event == null) return
        val prefs = Prefs.get(this)
        if (!prefs.getBoolean(Prefs.KEY_ENABLED, true)) return

        val pkg = event.packageName?.toString() ?: return
        val isYt = Targets.isYouTube(pkg)
        val isBrowser = Targets.isBrowser(pkg)
        if (!isYt && !(isBrowser && prefs.getBoolean(Prefs.KEY_BROWSERS, true))) return

        // Giới hạn tần suất quét để tiết kiệm pin.
        val now = SystemClock.uptimeMillis()
        if (now - lastScan < 200) return
        lastScan = now
        if (now - lastClick < 1200) return

        val root = rootInActiveWindow ?: event.source ?: return
        val button = findSkipButton(root, pkg, isYt) ?: return

        if (clickNode(button)) {
            lastClick = now
            Prefs.addSkipped(this)
            if (prefs.getBoolean(Prefs.KEY_TOAST, false)) {
                Toast.makeText(this, R.string.toast_skipped, Toast.LENGTH_SHORT).show()
            }
        }
    }

    /** Tìm nút bỏ qua: ưu tiên view id, sau đó duyệt cây theo chiều rộng (giới hạn). */
    private fun findSkipButton(root: AccessibilityNodeInfo, pkg: String, isYt: Boolean): AccessibilityNodeInfo? {
        if (isYt) {
            for (hint in listOf("skip_ad_button", "skip_ad_button_container")) {
                val hits = root.findAccessibilityNodeInfosByViewId("$pkg:id/$hint")
                hits?.firstOrNull { it.isVisibleToUser }?.let { return it }
            }
            // Bản mod có thể dùng package id khác.
            for (hint in listOf("com.google.android.youtube:id/skip_ad_button")) {
                root.findAccessibilityNodeInfosByViewId(hint)
                    ?.firstOrNull { it.isVisibleToUser }?.let { return it }
            }
        }

        val queue = ArrayDeque<Pair<AccessibilityNodeInfo, Int>>()
        queue.add(root to 0)
        var visited = 0
        while (queue.isNotEmpty() && visited < 800) {
            val (node, depth) = queue.poll()!!
            visited++
            if (node.isVisibleToUser && matches(node, isYt)) return node
            if (depth < 30) {
                for (i in 0 until node.childCount) {
                    node.getChild(i)?.let { queue.add(it to depth + 1) }
                }
            }
        }
        return null
    }

    private fun matches(node: AccessibilityNodeInfo, isYt: Boolean): Boolean {
        val id = node.viewIdResourceName?.lowercase(Locale.ROOT).orEmpty()
        if (isYt && Targets.SKIP_ID_HINTS.any { id.endsWith(it) || id.contains("/$it") }) return true

        val label = (node.text?.toString() ?: node.contentDescription?.toString())
            ?.trim()?.lowercase(Locale.ROOT) ?: return false
        if (label.isEmpty() || label.length > 40) return false
        // "Skip ad in 5" / "Bỏ qua sau 5" là đếm ngược, chưa bấm được.
        if (label.any { it.isDigit() }) return false

        if (Targets.STRONG_PHRASES.any { label.startsWith(it) }) return true
        if (isYt && id.contains("skip") && Targets.WEAK_PHRASES.any { label == it }) return true
        return false
    }

    private fun clickNode(node: AccessibilityNodeInfo): Boolean {
        // 1) Bấm trực tiếp vào node hoặc tổ tiên có thể bấm.
        var cur: AccessibilityNodeInfo? = node
        var depth = 0
        while (cur != null && depth < 6) {
            if (cur.isClickable && cur.isEnabled && cur.performAction(AccessibilityNodeInfo.ACTION_CLICK)) {
                return true
            }
            cur = cur.parent
            depth++
        }
        // 2) Dự phòng: chạm vào tâm nút bằng cử chỉ.
        val r = Rect()
        node.getBoundsInScreen(r)
        if (r.isEmpty) return false
        val path = Path().apply { moveTo(r.exactCenterX(), r.exactCenterY()) }
        val gesture = GestureDescription.Builder()
            .addStroke(GestureDescription.StrokeDescription(path, 0, 50))
            .build()
        return dispatchGesture(gesture, null, null)
    }

    override fun onInterrupt() {}
}
