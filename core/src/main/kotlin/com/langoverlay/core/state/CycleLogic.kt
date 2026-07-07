package com.langoverlay.core.state

import com.langoverlay.core.model.LanguageEntry
import com.langoverlay.core.model.LayoutInput

object CycleLogic {
    fun nextLanguageId(
        currentId: String,
        languages: List<LanguageEntry>,
        input: LayoutInput,
    ): String {
        require(languages.size >= 2) { "At least two languages are required" }
        return when (input) {
            is LayoutInput.SyncFromSystem -> resolveKnownId(input.languageId, languages)
            LayoutInput.ToggleManual, LayoutInput.ToggleShortcut -> cycle(currentId, languages)
        }
    }

    private fun cycle(currentId: String, languages: List<LanguageEntry>): String {
        val index = languages.indexOfFirst { it.id == currentId }
        if (index == -1) return languages.first().id
        return languages[(index + 1) % languages.size].id
    }

    private fun resolveKnownId(languageId: String, languages: List<LanguageEntry>): String {
        return if (languages.any { it.id == languageId }) {
            languageId
        } else {
            languages.first().id
        }
    }
}
