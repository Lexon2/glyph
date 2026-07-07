package com.langoverlay.app.data

import com.langoverlay.core.model.AppSettings
import com.langoverlay.core.model.LanguageEntry
import com.langoverlay.core.model.OverlayAppearance
import com.langoverlay.core.model.OverlayConfig
import com.langoverlay.core.model.OverlayVisibilityMode
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

    suspend fun persistLanguageId(languageId: String) {
        dataStore.persistLanguageId(languageId)
    }

    suspend fun updateOverlay(config: OverlayConfig, appearance: OverlayAppearance) {
        dataStore.update { it.copy(overlay = config, overlayAppearance = appearance) }
    }

    suspend fun updateOverlayPosition(anchorX: Float, anchorY: Float) {
        dataStore.update {
            it.copy(overlay = it.overlay.copy(anchorX = anchorX, anchorY = anchorY))
        }
    }

    suspend fun updateLanguages(languages: List<LanguageEntry>) {
        require(languages.size >= 2) { "At least two languages are required" }
        require(languages.map { it.id }.distinct().size == languages.size) {
            "Language ids must be unique"
        }
        dataStore.update { settings ->
            val resolvedCurrent = AppSettings(
                languages = languages,
                currentLanguageId = settings.currentLanguageId,
            ).resolvedCurrentLanguageId()
            settings.copy(languages = languages, currentLanguageId = resolvedCurrent)
        }
    }

    suspend fun updateShortcut(shortcut: ShortcutPreset) {
        dataStore.update { it.copy(shortcut = shortcut) }
    }

    suspend fun updateOverlayVisibility(mode: OverlayVisibilityMode) {
        dataStore.update { it.copy(overlayVisibilityMode = mode) }
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
