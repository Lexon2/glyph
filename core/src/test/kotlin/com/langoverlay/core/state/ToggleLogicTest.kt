package com.langoverlay.core.state

import com.langoverlay.core.model.KeyboardLayout
import com.langoverlay.core.model.LayoutInput
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class ToggleLogicTest {
    @Test
    fun `toggle EN to RU`() {
        val result = ToggleLogic.nextLayout(
            current = KeyboardLayout.EN,
            languageA = KeyboardLayout.EN,
            languageB = KeyboardLayout.RU,
            input = LayoutInput.ToggleShortcut,
        )
        assertEquals(KeyboardLayout.RU, result)
    }

    @Test
    fun `toggle RU to EN`() {
        val result = ToggleLogic.nextLayout(
            current = KeyboardLayout.RU,
            languageA = KeyboardLayout.EN,
            languageB = KeyboardLayout.RU,
            input = LayoutInput.ToggleManual,
        )
        assertEquals(KeyboardLayout.EN, result)
    }

    @Test
    fun `toggle EN to UA pair`() {
        val result = ToggleLogic.nextLayout(
            current = KeyboardLayout.EN,
            languageA = KeyboardLayout.EN,
            languageB = KeyboardLayout.UA,
            input = LayoutInput.ToggleShortcut,
        )
        assertEquals(KeyboardLayout.UA, result)
    }

    @Test
    fun `sync from system overrides toggle`() {
        val result = ToggleLogic.nextLayout(
            current = KeyboardLayout.EN,
            languageA = KeyboardLayout.EN,
            languageB = KeyboardLayout.RU,
            input = LayoutInput.SyncFromSystem(KeyboardLayout.UA),
        )
        assertEquals(KeyboardLayout.UA, result)
    }

    @Test
    fun `unknown current snaps to languageA on toggle`() {
        val result = ToggleLogic.nextLayout(
            current = KeyboardLayout.UA,
            languageA = KeyboardLayout.EN,
            languageB = KeyboardLayout.RU,
            input = LayoutInput.ToggleShortcut,
        )
        assertEquals(KeyboardLayout.EN, result)
    }
}
