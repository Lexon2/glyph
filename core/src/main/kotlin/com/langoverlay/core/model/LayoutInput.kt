package com.langoverlay.core.model

sealed interface LayoutInput {
    data object ToggleShortcut : LayoutInput
    data object ToggleManual : LayoutInput
    data class SyncFromSystem(val layout: KeyboardLayout) : LayoutInput
}
