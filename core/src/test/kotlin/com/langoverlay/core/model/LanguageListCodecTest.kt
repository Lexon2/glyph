package com.langoverlay.core.model

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class LanguageListCodecTest {
    @Test
    fun `encode and decode round trip`() {
        val languages = listOf(
            LanguageEntry("en", "EN"),
            LanguageEntry("ru", "RU"),
            LanguageEntry("de", "DE"),
        )
        val encoded = LanguageListCodec.encode(languages)
        assertEquals(languages, LanguageListCodec.decode(encoded))
    }

    @Test
    fun `decode blank falls back to defaults`() {
        assertEquals(LanguageListCodec.defaultLanguages(), LanguageListCodec.decode(null))
        assertEquals(LanguageListCodec.defaultLanguages(), LanguageListCodec.decode(""))
    }

    @Test
    fun `decode single entry falls back to defaults`() {
        assertEquals(LanguageListCodec.defaultLanguages(), LanguageListCodec.decode("en|EN"))
    }
}
