package com.langoverlay.detection.accessibility

import android.content.Context
import com.langoverlay.core.model.AppSettings
import com.langoverlay.core.state.LanguageStateManager
import kotlinx.coroutines.flow.Flow

/**
 * Bridge between the detection module and app-layer dependencies.
 * Implemented in the app module to avoid circular dependencies.
 */
interface AccessibilityServiceLocator {
    val languageStateManager: LanguageStateManager

    suspend fun bootstrapFromStorage()
    fun currentSettings(): AppSettings
    fun settingsFlow(): Flow<AppSettings>
    fun onOverlayPositionChanged(anchorX: Float, anchorY: Float)
    fun onServiceConnected()
    fun onServiceDisconnected()

    companion object {
        @Volatile
        private var instance: AccessibilityServiceLocator? = null

        @Volatile
        private var serviceRunning: Boolean = false

        fun install(locator: AccessibilityServiceLocator) {
            instance = locator
        }

        fun from(@Suppress("UNUSED_PARAMETER") context: Context): AccessibilityServiceLocator {
            return instance ?: error(
                "AccessibilityServiceLocator not installed. Ensure Application.onCreate() ran.",
            )
        }

        fun markServiceConnected() {
            serviceRunning = true
        }

        fun markServiceDisconnected() {
            serviceRunning = false
        }

        fun isServiceRunning(): Boolean = serviceRunning
    }
}
