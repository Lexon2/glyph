package com.langoverlay.detection.accessibility

import android.accessibilityservice.AccessibilityService
import android.provider.Settings
import android.view.KeyEvent
import android.view.accessibility.AccessibilityEvent
import com.langoverlay.core.model.LayoutInput
import com.langoverlay.detection.overlay.OverlayWindowManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

/**
 * Single service hosting overlay window and keyboard chord detection.
 * Dependencies are injected via [AccessibilityServiceLocator] from the app module.
 */
class LangOverlayAccessibilityService : AccessibilityService() {

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
    private var overlayManager: OverlayWindowManager? = null
    private var chordMachine: ChordStateMachine? = null
    private var locator: AccessibilityServiceLocator? = null
    private var overlayVisible = false

    override fun onServiceConnected() {
        super.onServiceConnected()
        val serviceLocator = AccessibilityServiceLocator.from(applicationContext)
        locator = serviceLocator
        AccessibilityServiceLocator.markServiceConnected()
        serviceLocator.onServiceConnected()

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
            ensureOverlayVisible(serviceLocator)

            serviceLocator.languageStateManager.layout.collectLatest { layout ->
                overlayManager?.updateLayout(layout)
            }
        }

        serviceScope.launch {
            serviceLocator.settingsFlow().collectLatest { settings ->
                serviceLocator.languageStateManager.updateLanguagePair(
                    settings.languageA,
                    settings.languageB,
                )
                chordMachine = ChordStateMachine(settings.shortcut) {
                    serviceLocator.languageStateManager.onInput(LayoutInput.ToggleShortcut)
                }
                overlayManager?.updateConfig(settings.overlay, settings.overlayAppearance)
                ensureOverlayVisible(serviceLocator)
            }
        }
    }

    private fun ensureOverlayVisible(serviceLocator: AccessibilityServiceLocator) {
        if (!Settings.canDrawOverlays(this)) return
        if (!overlayVisible || overlayManager?.isShowing() != true) {
            overlayManager?.show()
            overlayVisible = true
        }
        overlayManager?.updateLayout(serviceLocator.languageStateManager.layout.value)
    }

    override fun onKeyEvent(event: KeyEvent): Boolean {
        chordMachine?.process(event)
        return false
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) = Unit

    override fun onInterrupt() = Unit

    override fun onConfigurationChanged(newConfig: android.content.res.Configuration) {
        super.onConfigurationChanged(newConfig)
        val settings = locator?.currentSettings() ?: return
        val serviceLocator = locator ?: return
        overlayManager?.updateConfig(settings.overlay, settings.overlayAppearance)
        overlayManager?.onConfigurationChanged()
        overlayVisible = false
        ensureOverlayVisible(serviceLocator)
    }

    override fun onDestroy() {
        runBlocking {
            locator?.languageStateManager?.flush()
        }
        overlayManager?.hide()
        overlayManager = null
        overlayVisible = false
        chordMachine = null
        AccessibilityServiceLocator.markServiceDisconnected()
        locator?.onServiceDisconnected()
        locator = null
        serviceScope.cancel()
        super.onDestroy()
    }
}
