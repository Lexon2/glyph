package com.langoverlay.detection.overlay

import android.content.Context
import android.content.res.Configuration
import android.graphics.PixelFormat
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.util.TypedValue
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.langoverlay.core.model.OverlayAppearance
import com.langoverlay.core.model.OverlayConfig
import com.langoverlay.detection.R
import kotlin.math.max
import kotlin.math.roundToInt

class OverlayWindowManager(
    private val context: Context,
    private val onPositionChanged: (anchorX: Float, anchorY: Float) -> Unit,
    private val onTap: () -> Unit,
) {
    private val windowManager = context.getSystemService(WindowManager::class.java)
    private var container: FrameLayout? = null
    private var labelView: TextView? = null
    private var layoutParams: WindowManager.LayoutParams? = null

    private var config: OverlayConfig = OverlayConfig()
    private var appearance: OverlayAppearance = OverlayAppearance.SYSTEM
    private var dragStartX = 0f
    private var dragStartY = 0f
    private var paramStartX = 0
    private var paramStartY = 0
    private var isDragging = false

    fun show() {
        if (container != null) return

        val frame = FrameLayout(context)
        val label = TextView(context).apply {
            gravity = Gravity.CENTER
            setTextSize(TypedValue.COMPLEX_UNIT_SP, config.fontSizeSp)
            setPadding(dp(12), dp(8), dp(12), dp(8))
        }
        frame.addView(
            label,
            FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT,
            ),
        )

        val params = createLayoutParams()
        applyAppearance(label, frame)
        applyPosition(params)
        updateLabelText(label, "EN")

        frame.setOnTouchListener { _, event -> handleTouch(event, params, frame) }

        windowManager.addView(frame, params)
        container = frame
        labelView = label
        layoutParams = params
    }

    fun hide() {
        container?.let { windowManager.removeView(it) }
        container = null
        labelView = null
        layoutParams = null
    }

    fun updateLayout(displayLabel: String) {
        labelView?.let { updateLabelText(it, displayLabel) }
    }

    fun updateConfig(newConfig: OverlayConfig, newAppearance: OverlayAppearance) {
        config = newConfig
        appearance = newAppearance
        labelView?.let { label ->
            label.setTextSize(TypedValue.COMPLEX_UNIT_SP, config.fontSizeSp)
            container?.let { applyAppearance(label, it) }
        }
        layoutParams?.let { params ->
            applyPosition(params)
            container?.let { windowManager.updateViewLayout(it, params) }
        }
    }

    fun onConfigurationChanged() {
        layoutParams?.let { params ->
            applyPosition(params)
            container?.let { windowManager.updateViewLayout(it, params) }
        }
    }

    fun isShowing(): Boolean = container != null

    private fun handleTouch(
        event: MotionEvent,
        params: WindowManager.LayoutParams,
        frame: FrameLayout,
    ): Boolean {
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                isDragging = false
                dragStartX = event.rawX
                dragStartY = event.rawY
                paramStartX = params.x
                paramStartY = params.y
                return true
            }

            MotionEvent.ACTION_MOVE -> {
                val dx = event.rawX - dragStartX
                val dy = event.rawY - dragStartY
                if (!isDragging && (kotlin.math.abs(dx) > DRAG_THRESHOLD || kotlin.math.abs(dy) > DRAG_THRESHOLD)) {
                    isDragging = true
                }
                if (isDragging) {
                    params.x = paramStartX + dx.roundToInt()
                    params.y = paramStartY + dy.roundToInt()
                    windowManager.updateViewLayout(frame, params)
                    return true
                }
            }

            MotionEvent.ACTION_UP -> {
                if (isDragging) {
                    val metrics = context.resources.displayMetrics
                    val anchorX = (params.x.toFloat() / max(metrics.widthPixels, 1)).coerceIn(0f, 1f)
                    val anchorY = (params.y.toFloat() / max(metrics.heightPixels, 1)).coerceIn(0f, 1f)
                    onPositionChanged(anchorX, anchorY)
                } else {
                    onTap()
                }
                isDragging = false
                return true
            }
        }
        return false
    }

    private fun createLayoutParams(): WindowManager.LayoutParams {
        val type = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        } else {
            @Suppress("DEPRECATION")
            WindowManager.LayoutParams.TYPE_PHONE
        }

        return WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            type,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            PixelFormat.TRANSLUCENT,
        ).apply {
            gravity = Gravity.TOP or Gravity.START
        }
    }

    private fun applyPosition(params: WindowManager.LayoutParams) {
        val metrics = context.resources.displayMetrics
        params.x = (config.anchorX * metrics.widthPixels).roundToInt()
        params.y = (config.anchorY * metrics.heightPixels).roundToInt()
    }

    private fun applyAppearance(label: TextView, frame: FrameLayout) {
        val isDark = when (appearance) {
            OverlayAppearance.DARK -> true
            OverlayAppearance.LIGHT -> false
            OverlayAppearance.SYSTEM -> {
                val nightMode = context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
                nightMode == Configuration.UI_MODE_NIGHT_YES
            }
        }

        val backgroundColor = if (isDark) {
            ContextCompat.getColor(context, R.color.overlay_background_dark)
        } else {
            ContextCompat.getColor(context, R.color.overlay_background_light)
        }
        val textColor = if (isDark) {
            ContextCompat.getColor(context, R.color.overlay_text_dark)
        } else {
            ContextCompat.getColor(context, R.color.overlay_text_light)
        }

        val alpha = (config.opacity * 255).roundToInt().coerceIn(0, 255)
        val drawable = GradientDrawable().apply {
            cornerRadius = dp(8).toFloat()
            setColor(backgroundColor)
            this.alpha = alpha
        }
        frame.background = drawable
        label.setTextColor(textColor)
    }

    private fun updateLabelText(label: TextView, displayLabel: String) {
        label.text = displayLabel
    }

    private fun dp(value: Int): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            value.toFloat(),
            context.resources.displayMetrics,
        ).roundToInt()
    }

    companion object {
        private const val DRAG_THRESHOLD = 10f
    }
}
