package com.langoverlay.app.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.langoverlay.app.data.SettingsRepository
import com.langoverlay.app.util.PermissionUtils
import com.langoverlay.core.model.AppSettings
import com.langoverlay.core.model.LanguageEntry
import com.langoverlay.core.model.OverlayAppearance
import com.langoverlay.core.model.OverlayConfig
import com.langoverlay.core.model.OverlayVisibilityMode
import com.langoverlay.core.model.ShortcutPreset
import com.langoverlay.detection.accessibility.AccessibilityServiceLocator
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import android.content.Context
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SettingsUiState(
    val settings: AppSettings = AppSettings(),
    val accessibilityEnabled: Boolean = false,
    val overlayGranted: Boolean = false,
    val serviceRunning: Boolean = false,
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val repository: SettingsRepository,
) : ViewModel() {

    private val permissionRefresh = MutableStateFlow(0)

    val uiState: StateFlow<SettingsUiState> = combine(
        repository.settings,
        permissionRefresh,
    ) { settings, _ ->
        SettingsUiState(
            settings = settings,
            accessibilityEnabled = PermissionUtils.isAccessibilityServiceEnabled(context),
            overlayGranted = PermissionUtils.canDrawOverlays(context),
            serviceRunning = AccessibilityServiceLocator.isServiceRunning(),
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = SettingsUiState(),
    )

    fun refreshPermissions() {
        permissionRefresh.value++
    }

    fun updateLanguages(languages: List<LanguageEntry>) {
        viewModelScope.launch {
            repository.updateLanguages(languages)
        }
    }

    fun updateOpacity(opacity: Float) {
        viewModelScope.launch {
            val current = repository.current()
            repository.updateOverlay(
                current.overlay.copy(opacity = opacity.coerceIn(OverlayConfig.MIN_OPACITY, 1f)),
                current.overlayAppearance,
            )
        }
    }

    fun updateFontSize(fontSizeSp: Float) {
        viewModelScope.launch {
            val current = repository.current()
            repository.updateOverlay(
                current.overlay.copy(fontSizeSp = fontSizeSp.coerceIn(10f, 32f)),
                current.overlayAppearance,
            )
        }
    }

    fun updateAppearance(appearance: OverlayAppearance) {
        viewModelScope.launch {
            val current = repository.current()
            repository.updateOverlay(current.overlay, appearance)
        }
    }

    fun updateOverlayVisibility(mode: OverlayVisibilityMode) {
        viewModelScope.launch {
            repository.updateOverlayVisibility(mode)
        }
    }

    fun updateShortcut(shortcut: ShortcutPreset) {
        viewModelScope.launch {
            repository.updateShortcut(shortcut)
        }
    }

    fun updateStartAtBoot(enabled: Boolean) {
        viewModelScope.launch {
            repository.updateStartAtBoot(enabled)
        }
    }

    fun resetOverlayPosition() {
        viewModelScope.launch {
            repository.resetOverlayPosition()
        }
    }

    fun completeOnboarding() {
        viewModelScope.launch {
            repository.completeOnboarding()
        }
    }
}
