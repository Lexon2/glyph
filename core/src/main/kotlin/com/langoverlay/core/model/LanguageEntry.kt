package com.langoverlay.core.model

data class LanguageEntry(
    val id: String,
    val displayLabel: String,
) {
    init {
        require(id.isNotBlank()) { "Language id must not be blank" }
        require(displayLabel.isNotBlank()) { "Display label must not be blank" }
    }
}
