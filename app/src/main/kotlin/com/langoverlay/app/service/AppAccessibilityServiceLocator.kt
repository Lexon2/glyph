package com.langoverlay.app.service

import com.langoverlay.app.data.SettingsRepository
import com.langoverlay.app.di.ApplicationScope
import com.langoverlay.core.model.AppSettings
import com.langoverlay.core.state.LanguageStateManager
import com.langoverlay.detection.accessibility.AccessibilityServiceLocator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppAccessibilityServiceLocator @Inject constructor(
    private val settingsRepository: SettingsRepository,
    override val languageStateManager: LanguageStateManager,
    @ApplicationScope private val applicationScope: CoroutineScope,
) : AccessibilityServiceLocator {

    @Volatile
    private var cachedSettings: AppSettings = AppSettings()

    init {
        AccessibilityServiceLocator.install(this)
        applicationScope.launch {
            settingsRepository.settings.collect { settings ->
                cachedSettings = settings
            }
        }
    }

    override suspend fun bootstrapFromStorage() {
        val settings = settingsRepository.current()
        cachedSettings = settings
        languageStateManager.updateLanguagePair(settings.languageA, settings.languageB)
        languageStateManager.restoreLayout(settings.currentLayout)
    }

    override fun currentSettings(): AppSettings = cachedSettings

    override fun settingsFlow(): Flow<AppSettings> = settingsRepository.settings

    override fun onOverlayPositionChanged(anchorX: Float, anchorY: Float) {
        applicationScope.launch {
            settingsRepository.updateOverlayPosition(anchorX, anchorY)
        }
    }

    override fun onServiceConnected() = Unit

    override fun onServiceDisconnected() = Unit
}
