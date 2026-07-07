package com.langoverlay.core.state

import com.langoverlay.core.model.KeyboardLayout
import com.langoverlay.core.model.LayoutInput

object ToggleLogic {
    fun nextLayout(
        current: KeyboardLayout,
        languageA: KeyboardLayout,
        languageB: KeyboardLayout,
        input: LayoutInput,
    ): KeyboardLayout {
        return when (input) {
            is LayoutInput.SyncFromSystem -> input.layout
            LayoutInput.ToggleManual, LayoutInput.ToggleShortcut -> toggle(current, languageA, languageB)
        }
    }

    private fun toggle(
        current: KeyboardLayout,
        languageA: KeyboardLayout,
        languageB: KeyboardLayout,
    ): KeyboardLayout {
        return when (current) {
            languageA -> languageB
            languageB -> languageA
            else -> languageA
        }
    }
}
