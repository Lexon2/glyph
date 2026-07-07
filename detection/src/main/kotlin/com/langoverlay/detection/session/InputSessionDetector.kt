package com.langoverlay.detection.session

import android.accessibilityservice.AccessibilityService
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import android.view.accessibility.AccessibilityWindowInfo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Fuses accessibility signals into a single input-session state with grace-period exit.
 */
class InputSessionDetector(
    private val scope: CoroutineScope,
    private val gracePeriodMs: Long = DEFAULT_GRACE_PERIOD_MS,
    private val textChangeThrottleMs: Long = DEFAULT_TEXT_CHANGE_THROTTLE_MS,
) {
    private val _sessionActive = MutableStateFlow(false)
    val sessionActive: StateFlow<Boolean> = _sessionActive.asStateFlow()

    private var editableFocused = false
    private var imeVisible = false
    private var graceJob: Job? = null
    private var lastTextChangeAt = 0L

    fun onAccessibilityEvent(service: AccessibilityService, event: AccessibilityEvent?) {
        if (event == null) return
        when (event.eventType) {
            AccessibilityEvent.TYPE_VIEW_FOCUSED,
            AccessibilityEvent.TYPE_VIEW_ACCESSIBILITY_FOCUSED,
            -> {
                val source = event.source
                editableFocused = source?.isEditable == true || hasFocusedEditable(service)
                source?.recycle()
                refreshImeState(service)
                recomputeSession()
            }

            AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED -> {
                val now = System.currentTimeMillis()
                if (now - lastTextChangeAt >= textChangeThrottleMs) {
                    lastTextChangeAt = now
                    editableFocused = true
                    refreshImeState(service)
                    recomputeSession()
                }
            }

            AccessibilityEvent.TYPE_WINDOWS_CHANGED,
            AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED,
            -> {
                refreshImeState(service)
                editableFocused = hasFocusedEditable(service)
                recomputeSession()
            }
        }
    }

    fun refreshWindows(service: AccessibilityService) {
        refreshImeState(service)
        editableFocused = hasFocusedEditable(service)
        recomputeSession()
    }

    fun reset() {
        editableFocused = false
        imeVisible = false
        graceJob?.cancel()
        graceJob = null
        _sessionActive.value = false
    }

    private fun refreshImeState(service: AccessibilityService) {
        imeVisible = detectImeVisible(service)
    }

    private fun recomputeSession() {
        val shouldBeActive = editableFocused || imeVisible
        if (shouldBeActive) {
            graceJob?.cancel()
            graceJob = null
            if (!_sessionActive.value) {
                Log.d(TAG, "Input session active")
            }
            _sessionActive.value = true
        } else if (_sessionActive.value) {
            startGracePeriod()
        }
    }

    private fun startGracePeriod() {
        if (graceJob?.isActive == true) return
        graceJob = scope.launch {
            delay(gracePeriodMs)
            if (!editableFocused && !imeVisible) {
                Log.d(TAG, "Input session ended")
                _sessionActive.value = false
            }
        }
    }

    private fun hasFocusedEditable(service: AccessibilityService): Boolean {
        val root = service.rootInActiveWindow ?: return false
        return try {
            val focused = root.findFocus(AccessibilityNodeInfo.FOCUS_INPUT)
            val editable = focused?.let { it.isEditable || it.isPassword } == true
            focused?.recycle()
            editable
        } catch (_: Exception) {
            false
        } finally {
            root.recycle()
        }
    }

    private fun detectImeVisible(service: AccessibilityService): Boolean {
        return try {
            @Suppress("DEPRECATION")
            service.windows?.any { window ->
                window.type == AccessibilityWindowInfo.TYPE_INPUT_METHOD
            } == true
        } catch (_: Exception) {
            false
        }
    }

    companion object {
        private const val TAG = "GlyphSession"
        const val DEFAULT_GRACE_PERIOD_MS = 400L
        const val DEFAULT_TEXT_CHANGE_THROTTLE_MS = 500L
    }
}
