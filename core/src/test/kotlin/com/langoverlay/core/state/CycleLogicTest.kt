package com.langoverlay.core.state

import com.langoverlay.core.model.LanguageEntry
import com.langoverlay.core.model.LayoutInput
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class CycleLogicTest {
    private val enRu = listOf(
        LanguageEntry("en", "EN"),
        LanguageEntry("ru", "RU"),
    )

  private val enRuUa = listOf(
        LanguageEntry("en", "EN"),
        LanguageEntry("ru", "RU"),
        LanguageEntry("ua", "UA"),
    )

    @Test
    fun `cycle EN to RU`() {
        val result = CycleLogic.nextLanguageId(
            currentId = "en",
            languages = enRu,
            input = LayoutInput.ToggleShortcut,
        )
        assertEquals("ru", result)
    }

    @Test
    fun `cycle RU to EN`() {
        val result = CycleLogic.nextLanguageId(
            currentId = "ru",
            languages = enRu,
            input = LayoutInput.ToggleManual,
        )
        assertEquals("en", result)
    }

    @Test
    fun `cycle through three languages`() {
        val first = CycleLogic.nextLanguageId("en", enRuUa, LayoutInput.ToggleShortcut)
        val second = CycleLogic.nextLanguageId(first, enRuUa, LayoutInput.ToggleShortcut)
        val third = CycleLogic.nextLanguageId(second, enRuUa, LayoutInput.ToggleShortcut)
        assertEquals("ru", first)
        assertEquals("ua", second)
        assertEquals("en", third)
    }

    @Test
    fun `sync from system overrides toggle`() {
        val result = CycleLogic.nextLanguageId(
            currentId = "en",
            languages = enRu,
            input = LayoutInput.SyncFromSystem("ru"),
        )
        assertEquals("ru", result)
    }

    @Test
    fun `unknown current snaps to first on toggle`() {
        val result = CycleLogic.nextLanguageId(
            currentId = "ua",
            languages = enRu,
            input = LayoutInput.ToggleShortcut,
        )
        assertEquals("en", result)
    }
}
