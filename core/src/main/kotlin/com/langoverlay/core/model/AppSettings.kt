package com.langoverlay.core.model

data class AppSettings(
    val languages: List<LanguageEntry> = LanguageListCodec.defaultLanguages(),
    val overlay: OverlayConfig = OverlayConfig(),
    val startAtBoot: Boolean = true,
    val shortcut: ShortcutPreset = ShortcutPreset.ALT_SHIFT,
    val overlayAppearance: OverlayAppearance = OverlayAppearance.SYSTEM,
    val overlayVisibilityMode: OverlayVisibilityMode = OverlayVisibilityMode.AUTO,
    val onboardingCompleted: Boolean = false,
    val currentLanguageId: String = "en",
) {
    init {
        require(languages.size >= 2) { "At least two languages are required" }
        require(languages.map { it.id }.distinct().size == languages.size) {
            "Language ids must be unique"
        }
    }

    fun resolvedCurrentLanguageId(): String {
        if (languages.any { it.id == currentLanguageId }) return currentLanguageId
        return languages.first().id
    }

    fun displayLabelFor(languageId: String): String =
        languages.firstOrNull { it.id == languageId }?.displayLabel
            ?: LanguageCatalog.find(languageId)?.displayLabel
            ?: languageId.uppercase()
}
