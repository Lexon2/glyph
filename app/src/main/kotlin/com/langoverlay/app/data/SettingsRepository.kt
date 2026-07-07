package com.langoverlay.app.data

import com.langoverlay.core.model.AppSettings
import com.langoverlay.core.model.KeyboardLayout
import com.langoverlay.core.model.OverlayAppearance
import com.langoverlay.core.model.OverlayConfig
import com.langoverlay.core.model.ShortcutPreset
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SettingsRepository @Inject constructor(
    private val dataStore: SettingsDataStore,
) {
    val settings: Flow<AppSettings> = dataStore.settings

    suspend fun current(): AppSettings = settings.first()

    suspend fun persistLayout(layout: KeyboardLayout) {
        dataStore.persistLayout(layout)
    }

    suspend fun updateOverlay(config: OverlayConfig, appearance: OverlayAppearance) {
        dataStore.update { it.copy(overlay = config, overlayAppearance = appearance) }
    }

    suspend fun updateOverlayPosition(anchorX: Float, anchorY: Float) {
        dataStore.update {
            it.copy(overlay = it.overlay.copy(anchorX = anchorX, anchorY = anchorY))
        }
    }

    suspend fun updateLanguages(languageA: KeyboardLayout, languageB: KeyboardLayout) {
        require(languageA != languageB) { "Languages must differ" }
        dataStore.update { it.copy(languageA = languageA, languageB = languageB) }
    }

    suspend fun updateShortcut(shortcut: ShortcutPreset) {
        dataStore.update { it.copy(shortcut = shortcut) }
    }

    suspend fun updateStartAtBoot(enabled: Boolean) {
        dataStore.update { it.copy(startAtBoot = enabled) }
    }

    suspend fun completeOnboarding() {
        dataStore.update { it.copy(onboardingCompleted = true) }
    }

    suspend fun resetOverlayPosition() {
        dataStore.update { it.copy(overlay = it.overlay.copy(anchorX = 0.95f, anchorY = 0.05f)) }
    }
}
