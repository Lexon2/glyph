package com.langoverlay.core.model

data class AppSettings(
    val languageA: KeyboardLayout = KeyboardLayout.EN,
    val languageB: KeyboardLayout = KeyboardLayout.RU,
    val overlay: OverlayConfig = OverlayConfig(),
    val startAtBoot: Boolean = true,
    val shortcut: ShortcutPreset = ShortcutPreset.ALT_SHIFT,
    val overlayAppearance: OverlayAppearance = OverlayAppearance.SYSTEM,
    val onboardingCompleted: Boolean = false,
    val currentLayout: KeyboardLayout = KeyboardLayout.EN,
) {
    init {
        require(languageA != languageB) { "languageA and languageB must differ" }
    }
}
