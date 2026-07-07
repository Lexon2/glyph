package com.langoverlay.app.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.langoverlay.core.model.AppSettings
import com.langoverlay.core.model.KeyboardLayout
import com.langoverlay.core.model.OverlayAppearance
import com.langoverlay.core.model.OverlayConfig
import com.langoverlay.core.model.ShortcutPreset
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.settingsDataStore: DataStore<Preferences> by preferencesDataStore(
    name = "lang_overlay_settings",
)

class SettingsDataStore(
    private val context: Context,
) {
    val settings: Flow<AppSettings> = context.settingsDataStore.data.map { prefs ->
        AppSettings(
            languageA = prefs[Keys.LANGUAGE_A]?.let { KeyboardLayout.valueOf(it) } ?: KeyboardLayout.EN,
            languageB = prefs[Keys.LANGUAGE_B]?.let { KeyboardLayout.valueOf(it) } ?: KeyboardLayout.RU,
            overlay = OverlayConfig(
                anchorX = prefs[Keys.ANCHOR_X] ?: OverlayConfig().anchorX,
                anchorY = prefs[Keys.ANCHOR_Y] ?: OverlayConfig().anchorY,
                opacity = prefs[Keys.OPACITY] ?: OverlayConfig.DEFAULT_OPACITY,
                fontSizeSp = prefs[Keys.FONT_SIZE] ?: OverlayConfig.DEFAULT_FONT_SIZE_SP,
            ),
            startAtBoot = prefs[Keys.START_AT_BOOT] ?: true,
            shortcut = prefs[Keys.SHORTCUT]?.let { ShortcutPreset.valueOf(it) } ?: ShortcutPreset.ALT_SHIFT,
            overlayAppearance = prefs[Keys.APPEARANCE]?.let { OverlayAppearance.valueOf(it) }
                ?: OverlayAppearance.SYSTEM,
            onboardingCompleted = prefs[Keys.ONBOARDING_COMPLETED] ?: false,
            currentLayout = prefs[Keys.CURRENT_LAYOUT]?.let { KeyboardLayout.valueOf(it) } ?: KeyboardLayout.EN,
        )
    }

    suspend fun update(transform: (AppSettings) -> AppSettings) {
        context.settingsDataStore.edit { prefs ->
            val current = AppSettings(
                languageA = prefs[Keys.LANGUAGE_A]?.let { KeyboardLayout.valueOf(it) } ?: KeyboardLayout.EN,
                languageB = prefs[Keys.LANGUAGE_B]?.let { KeyboardLayout.valueOf(it) } ?: KeyboardLayout.RU,
                overlay = OverlayConfig(
                    anchorX = prefs[Keys.ANCHOR_X] ?: OverlayConfig().anchorX,
                    anchorY = prefs[Keys.ANCHOR_Y] ?: OverlayConfig().anchorY,
                    opacity = prefs[Keys.OPACITY] ?: OverlayConfig.DEFAULT_OPACITY,
                    fontSizeSp = prefs[Keys.FONT_SIZE] ?: OverlayConfig.DEFAULT_FONT_SIZE_SP,
                ),
                startAtBoot = prefs[Keys.START_AT_BOOT] ?: true,
                shortcut = prefs[Keys.SHORTCUT]?.let { ShortcutPreset.valueOf(it) } ?: ShortcutPreset.ALT_SHIFT,
                overlayAppearance = prefs[Keys.APPEARANCE]?.let { OverlayAppearance.valueOf(it) }
                    ?: OverlayAppearance.SYSTEM,
                onboardingCompleted = prefs[Keys.ONBOARDING_COMPLETED] ?: false,
                currentLayout = prefs[Keys.CURRENT_LAYOUT]?.let { KeyboardLayout.valueOf(it) } ?: KeyboardLayout.EN,
            )
            val updated = transform(current)
            prefs[Keys.LANGUAGE_A] = updated.languageA.name
            prefs[Keys.LANGUAGE_B] = updated.languageB.name
            prefs[Keys.ANCHOR_X] = updated.overlay.anchorX
            prefs[Keys.ANCHOR_Y] = updated.overlay.anchorY
            prefs[Keys.OPACITY] = updated.overlay.opacity
            prefs[Keys.FONT_SIZE] = updated.overlay.fontSizeSp
            prefs[Keys.START_AT_BOOT] = updated.startAtBoot
            prefs[Keys.SHORTCUT] = updated.shortcut.name
            prefs[Keys.APPEARANCE] = updated.overlayAppearance.name
            prefs[Keys.ONBOARDING_COMPLETED] = updated.onboardingCompleted
            prefs[Keys.CURRENT_LAYOUT] = updated.currentLayout.name
        }
    }

    suspend fun persistLayout(layout: KeyboardLayout) {
        context.settingsDataStore.edit { prefs ->
            prefs[Keys.CURRENT_LAYOUT] = layout.name
        }
    }

    private object Keys {
        val LANGUAGE_A = stringPreferencesKey("language_a")
        val LANGUAGE_B = stringPreferencesKey("language_b")
        val ANCHOR_X = floatPreferencesKey("anchor_x")
        val ANCHOR_Y = floatPreferencesKey("anchor_y")
        val OPACITY = floatPreferencesKey("opacity")
        val FONT_SIZE = floatPreferencesKey("font_size")
        val START_AT_BOOT = booleanPreferencesKey("start_at_boot")
        val SHORTCUT = stringPreferencesKey("shortcut")
        val APPEARANCE = stringPreferencesKey("appearance")
        val ONBOARDING_COMPLETED = booleanPreferencesKey("onboarding_completed")
        val CURRENT_LAYOUT = stringPreferencesKey("current_layout")
    }
}
