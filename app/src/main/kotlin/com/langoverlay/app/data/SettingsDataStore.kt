package com.langoverlay.app.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.langoverlay.core.model.AppSettings
import com.langoverlay.core.model.KeyboardLayout
import com.langoverlay.core.model.LanguageListCodec
import com.langoverlay.core.model.OverlayAppearance
import com.langoverlay.core.model.OverlayConfig
import com.langoverlay.core.model.OverlayVisibilityMode
import com.langoverlay.core.model.ShortcutPreset
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.settingsDataStore: DataStore<Preferences> by preferencesDataStore(
    name = "lang_overlay_settings",
    corruptionHandler = ReplaceFileCorruptionHandler(
        produceNewData = { emptyPreferences() },
    ),
)

class SettingsDataStore(
    private val context: Context,
) {
    val settings: Flow<AppSettings> = context.settingsDataStore.data.map { prefs ->
        readAppSettings(prefs)
    }

    suspend fun update(transform: (AppSettings) -> AppSettings) {
        context.settingsDataStore.edit { prefs ->
            val current = readAppSettings(prefs)
            val updated = transform(current)
            writeAppSettings(prefs, updated)
        }
    }

    suspend fun persistLanguageId(languageId: String) {
        context.settingsDataStore.edit { prefs ->
            prefs[Keys.CURRENT_LANGUAGE_ID] = languageId
        }
    }

    private fun readAppSettings(prefs: Preferences): AppSettings {
        val languages = if (prefs.contains(Keys.LANGUAGES_JSON)) {
            LanguageListCodec.decode(prefs[Keys.LANGUAGES_JSON])
        } else {
            migrateLegacyLanguages(prefs)
        }
        val currentLanguageId = prefs[Keys.CURRENT_LANGUAGE_ID]
            ?: prefs[Keys.CURRENT_LAYOUT]?.let { legacyLayoutToId(it) }
            ?: languages.first().id

        return AppSettings(
            languages = languages,
            overlay = OverlayConfig(
                anchorX = prefs[Keys.ANCHOR_X] ?: OverlayConfig().anchorX,
                anchorY = prefs[Keys.ANCHOR_Y] ?: OverlayConfig().anchorY,
                opacity = prefs[Keys.OPACITY] ?: OverlayConfig.DEFAULT_OPACITY,
                fontSizeSp = prefs[Keys.FONT_SIZE] ?: OverlayConfig.DEFAULT_FONT_SIZE_SP,
            ),
            startAtBoot = prefs[Keys.START_AT_BOOT] ?: true,
            shortcut = parseEnum(prefs[Keys.SHORTCUT], ShortcutPreset.ALT_SHIFT),
            overlayAppearance = parseEnum(prefs[Keys.APPEARANCE], OverlayAppearance.SYSTEM),
            overlayVisibilityMode = parseEnum(prefs[Keys.OVERLAY_VISIBILITY], OverlayVisibilityMode.AUTO),
            onboardingCompleted = prefs[Keys.ONBOARDING_COMPLETED] ?: false,
            currentLanguageId = currentLanguageId,
        )
    }

    private fun writeAppSettings(prefs: androidx.datastore.preferences.core.MutablePreferences, updated: AppSettings) {
        prefs[Keys.LANGUAGES_JSON] = LanguageListCodec.encode(updated.languages)
        prefs[Keys.ANCHOR_X] = updated.overlay.anchorX
        prefs[Keys.ANCHOR_Y] = updated.overlay.anchorY
        prefs[Keys.OPACITY] = updated.overlay.opacity
        prefs[Keys.FONT_SIZE] = updated.overlay.fontSizeSp
        prefs[Keys.START_AT_BOOT] = updated.startAtBoot
        prefs[Keys.SHORTCUT] = updated.shortcut.name
        prefs[Keys.APPEARANCE] = updated.overlayAppearance.name
        prefs[Keys.OVERLAY_VISIBILITY] = updated.overlayVisibilityMode.name
        prefs[Keys.ONBOARDING_COMPLETED] = updated.onboardingCompleted
        prefs[Keys.CURRENT_LANGUAGE_ID] = updated.currentLanguageId
        prefs.remove(Keys.LANGUAGE_A)
        prefs.remove(Keys.LANGUAGE_B)
        prefs.remove(Keys.CURRENT_LAYOUT)
    }

    private fun migrateLegacyLanguages(prefs: Preferences): List<com.langoverlay.core.model.LanguageEntry> {
        val languageA = prefs[Keys.LANGUAGE_A]?.let { legacyLayoutToId(it) } ?: "en"
        val languageB = prefs[Keys.LANGUAGE_B]?.let { legacyLayoutToId(it) } ?: "ru"
        return listOf(
            com.langoverlay.core.model.LanguageEntry(languageA, languageA.uppercase()),
            com.langoverlay.core.model.LanguageEntry(languageB, languageB.uppercase()),
        )
    }

    private fun legacyLayoutToId(name: String): String = when (name) {
        KeyboardLayout.EN.name -> "en"
        KeyboardLayout.RU.name -> "ru"
        KeyboardLayout.UA.name -> "ua"
        else -> name.lowercase()
    }

    private inline fun <reified T : Enum<T>> parseEnum(value: String?, default: T): T {
        if (value == null) return default
        return enumValues<T>().find { it.name == value } ?: default
    }

    private object Keys {
        val LANGUAGE_A = stringPreferencesKey("language_a")
        val LANGUAGE_B = stringPreferencesKey("language_b")
        val LANGUAGES_JSON = stringPreferencesKey("languages_json")
        val CURRENT_LANGUAGE_ID = stringPreferencesKey("current_language_id")
        val ANCHOR_X = floatPreferencesKey("anchor_x")
        val ANCHOR_Y = floatPreferencesKey("anchor_y")
        val OPACITY = floatPreferencesKey("opacity")
        val FONT_SIZE = floatPreferencesKey("font_size")
        val START_AT_BOOT = booleanPreferencesKey("start_at_boot")
        val SHORTCUT = stringPreferencesKey("shortcut")
        val APPEARANCE = stringPreferencesKey("appearance")
        val OVERLAY_VISIBILITY = stringPreferencesKey("overlay_visibility")
        val ONBOARDING_COMPLETED = booleanPreferencesKey("onboarding_completed")
        val CURRENT_LAYOUT = stringPreferencesKey("current_layout")
    }
}
