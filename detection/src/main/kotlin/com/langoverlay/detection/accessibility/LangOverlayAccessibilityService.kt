package com.langoverlay.detection.accessibility

import android.accessibilityservice.AccessibilityService
import android.provider.Settings
import android.view.KeyEvent
import android.view.accessibility.AccessibilityEvent
import com.langoverlay.core.model.LayoutInput
import com.langoverlay.core.model.OverlayVisibilityMode
import com.langoverlay.detection.overlay.OverlayWindowManager
import com.langoverlay.detection.session.InputSessionDetector
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull

/**
 * Single service hosting overlay window, keyboard chord detection, and input session tracking.
 * Dependencies are injected via [AccessibilityServiceLocator] from the app module.
 */
class LangOverlayAccessibilityService : AccessibilityService() {

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
    private var overlayManager: OverlayWindowManager? = null
    private var chordMachine: ChordStateMachine? = null
    private var sessionDetector: InputSessionDetector? = null
    private var locator: AccessibilityServiceLocator? = null
    private var overlayVisible = false

    override fun onServiceConnected() {
        super.onServiceConnected()
        val serviceLocator = AccessibilityServiceLocator.from(applicationContext)
        locator = serviceLocator
        AccessibilityServiceLocator.markServiceConnected()
        serviceLocator.onServiceConnected()

        sessionDetector = InputSessionDetector(serviceScope)

        chordMachine = ChordStateMachine(
            shortcut = serviceLocator.currentSettings().shortcut,
        ) {
            serviceLocator.languageStateManager.onInput(LayoutInput.ToggleShortcut)
        }

        overlayManager = OverlayWindowManager(
            context = this,
            onPositionChanged = { x, y ->
                serviceLocator.onOverlayPositionChanged(x, y)
            },
            onTap = {
                serviceLocator.languageStateManager.onInput(LayoutInput.ToggleManual)
            },
        )

        serviceScope.launch {
            serviceLocator.bootstrapFromStorage()
            val settings = serviceLocator.currentSettings()
            overlayManager?.updateConfig(settings.overlay, settings.overlayAppearance)
            sessionDetector?.refreshWindows(this@LangOverlayAccessibilityService)
            updateOverlayVisibility(
                serviceLocator,
                computeOverlayVisible(
                    sessionActive = sessionDetector?.sessionActive?.value == true,
                    mode = settings.overlayVisibilityMode,
                ),
            )

            serviceLocator.languageStateManager.displayLabel.collectLatest { label ->
                overlayManager?.updateLayout(label)
            }
        }

        serviceScope.launch {
            val detector = sessionDetector ?: return@launch
            combine(
                detector.sessionActive,
                serviceLocator.settingsFlow(),
            ) { sessionActive, settings ->
                computeOverlayVisible(sessionActive, settings.overlayVisibilityMode)
            }
                .distinctUntilChanged()
                .collectLatest { show ->
                    updateOverlayVisibility(serviceLocator, show)
                }
        }

        serviceScope.launch {
            serviceLocator.settingsFlow().collectLatest { settings ->
                serviceLocator.languageStateManager.updateLanguages(settings.languages)
                chordMachine = ChordStateMachine(settings.shortcut) {
                    serviceLocator.languageStateManager.onInput(LayoutInput.ToggleShortcut)
                }
                overlayManager?.updateConfig(settings.overlay, settings.overlayAppearance)
                sessionDetector?.refreshWindows(this@LangOverlayAccessibilityService)
            }
        }
    }

    private fun computeOverlayVisible(
        sessionActive: Boolean,
        mode: OverlayVisibilityMode,
    ): Boolean {
        if (!Settings.canDrawOverlays(this)) return false
        return when (mode) {
            OverlayVisibilityMode.ALWAYS -> true
            OverlayVisibilityMode.AUTO -> sessionActive
        }
    }

    private fun updateOverlayVisibility(serviceLocator: AccessibilityServiceLocator, show: Boolean) {
        if (!Settings.canDrawOverlays(this)) {
            overlayManager?.hide()
            overlayVisible = false
            return
        }
        if (show) {
            if (!overlayVisible || overlayManager?.isShowing() != true) {
                overlayManager?.show()
                overlayVisible = true
            }
            overlayManager?.updateLayout(serviceLocator.languageStateManager.displayLabel.value)
        } else {
            overlayManager?.hide()
            overlayVisible = false
        }
    }

    override fun onKeyEvent(event: KeyEvent): Boolean {
        chordMachine?.process(event)
        return false
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        sessionDetector?.onAccessibilityEvent(this, event)
    }

    override fun onInterrupt() = Unit

    override fun onConfigurationChanged(newConfig: android.content.res.Configuration) {
        super.onConfigurationChanged(newConfig)
        val serviceLocator = locator ?: return
        val settings = serviceLocator.currentSettings()
        overlayManager?.updateConfig(settings.overlay, settings.overlayAppearance)
        overlayManager?.onConfigurationChanged()
        sessionDetector?.refreshWindows(this)
        overlayVisible = false
        updateOverlayVisibility(
            serviceLocator,
            computeOverlayVisible(
                sessionActive = sessionDetector?.sessionActive?.value == true,
                mode = settings.overlayVisibilityMode,
            ),
        )
    }

    override fun onDestroy() {
        serviceScope.launch {
            withTimeoutOrNull(500L) {
                locator?.languageStateManager?.flush()
            }
        }
        sessionDetector?.reset()
        overlayManager?.hide()
        overlayManager = null
        overlayVisible = false
        chordMachine = null
        sessionDetector = null
        AccessibilityServiceLocator.markServiceDisconnected()
        locator?.onServiceDisconnected()
        locator = null
        serviceScope.cancel()
        super.onDestroy()
    }
}
