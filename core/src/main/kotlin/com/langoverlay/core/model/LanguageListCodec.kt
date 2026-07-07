package com.langoverlay.core.model

object LanguageListCodec {
    private const val ENTRY_SEPARATOR = ";"
    private const val FIELD_SEPARATOR = "|"
    const val MAX_LANGUAGES = 20

    fun encode(languages: List<LanguageEntry>): String {
        require(languages.size in 2..MAX_LANGUAGES) {
            "Language list must contain between 2 and $MAX_LANGUAGES entries"
        }
        return languages.joinToString(ENTRY_SEPARATOR) { entry ->
            "${sanitize(entry.id)}$FIELD_SEPARATOR${sanitize(entry.displayLabel)}"
        }
    }

    fun decode(raw: String?): List<LanguageEntry> {
        if (raw.isNullOrBlank()) return defaultLanguages()
        val parsed = raw.split(ENTRY_SEPARATOR)
            .mapNotNull { token ->
                val parts = token.split(FIELD_SEPARATOR, limit = 2)
                if (parts.size != 2) return@mapNotNull null
                val id = parts[0].trim()
                val label = parts[1].trim()
                if (id.isEmpty() || label.isEmpty()) return@mapNotNull null
                LanguageEntry(id = id, displayLabel = label)
            }
            .distinctBy { it.id }
        return if (parsed.size >= 2) parsed.take(MAX_LANGUAGES) else defaultLanguages()
    }

    fun defaultLanguages(): List<LanguageEntry> = listOf(
        LanguageEntry("en", "EN"),
        LanguageEntry("ru", "RU"),
    )

    private fun sanitize(value: String): String =
        value.replace(FIELD_SEPARATOR, "").replace(ENTRY_SEPARATOR, "").trim()
}
