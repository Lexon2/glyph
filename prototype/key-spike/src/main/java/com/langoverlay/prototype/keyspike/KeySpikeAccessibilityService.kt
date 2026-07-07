package com.langoverlay.prototype.keyspike

import android.accessibilityservice.AccessibilityService
import android.graphics.Color
import android.graphics.PixelFormat
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.provider.Settings
import android.util.Log
import android.util.TypedValue
import android.view.Gravity
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.WindowManager
import android.view.accessibility.AccessibilityEvent
import android.widget.FrameLayout
import android.widget.ScrollView
import android.widget.TextView
import kotlin.math.roundToInt

/**
 * Phase 0 hardware spike: logs all key events and hosts a draggable overlay TextView.
 * Install on Samsung One UI 8.5 tablet to verify shortcut interception.
 */
class KeySpikeAccessibilityService : AccessibilityService() {

    private var windowManager: WindowManager? = null
    private var overlayRoot: FrameLayout? = null
    private var logView: TextView? = null
    private var layoutParams: WindowManager.LayoutParams? = null
    private var dragStartX = 0f
    private var dragStartY = 0f
    private var paramStartX = 0
    private var paramStartY = 0

    override fun onServiceConnected() {
        super.onServiceConnected()
        if (!Settings.canDrawOverlays(this)) {
            Log.e(TAG, "Overlay permission missing")
            return
        }
        showOverlay()
        appendLog("Service connected. Press Alt+Shift, Ctrl+Space, Shift+Space.")
    }

    override fun onKeyEvent(event: KeyEvent): Boolean {
        val meta = event.metaState
        val line = "key=${KeyEvent.keyCodeToString(event.keyCode)} " +
            "action=${event.action} meta=0x${meta.toString(16)} repeat=${event.repeatCount}"
        Log.d(TAG, line)
        appendLog(line)
        return false
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) = Unit

    override fun onInterrupt() = Unit

    override fun onDestroy() {
        overlayRoot?.let { windowManager?.removeView(it) }
        overlayRoot = null
        logView = null
        super.onDestroy()
    }

    private fun showOverlay() {
        windowManager = getSystemService(WindowManager::class.java)
        val root = FrameLayout(this)
        val scroll = ScrollView(this)
        val log = TextView(this).apply {
            setTextColor(Color.WHITE)
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 11f)
            text = "Key Spike ready\n"
        }
        scroll.addView(log)
        root.addView(
            scroll,
            FrameLayout.LayoutParams(
                dp(280),
                dp(200),
            ),
        )
        val bg = GradientDrawable().apply {
            cornerRadius = dp(8).toFloat()
            setColor(0xCC000000.toInt())
        }
        root.background = bg

        val params = WindowManager.LayoutParams(
            dp(280),
            dp(200),
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            } else {
                @Suppress("DEPRECATION")
                WindowManager.LayoutParams.TYPE_PHONE
            },
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            PixelFormat.TRANSLUCENT,
        ).apply {
            gravity = Gravity.TOP or Gravity.START
            x = dp(16)
            y = dp(16)
        }

        root.setOnTouchListener { _, motionEvent ->
            handleDrag(motionEvent, params, root)
        }

        windowManager?.addView(root, params)
        overlayRoot = root
        logView = log
        layoutParams = params
    }

    private fun handleDrag(event: MotionEvent, params: WindowManager.LayoutParams, root: FrameLayout): Boolean {
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                dragStartX = event.rawX
                dragStartY = event.rawY
                paramStartX = params.x
                paramStartY = params.y
                return true
            }
            MotionEvent.ACTION_MOVE -> {
                params.x = paramStartX + (event.rawX - dragStartX).roundToInt()
                params.y = paramStartY + (event.rawY - dragStartY).roundToInt()
                windowManager?.updateViewLayout(root, params)
                return true
            }
        }
        return false
    }

    private fun appendLog(line: String) {
        logView?.post {
            logView?.append("$line\n")
        }
    }

    private fun dp(value: Int): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            value.toFloat(),
            resources.displayMetrics,
        ).roundToInt()
    }

    companion object {
        private const val TAG = "KeySpike"
    }
}
