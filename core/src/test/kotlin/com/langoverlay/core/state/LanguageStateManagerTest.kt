package com.langoverlay.core.state

import com.langoverlay.core.model.KeyboardLayout
import com.langoverlay.core.model.LayoutInput
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

@OptIn(ExperimentalCoroutinesApi::class)
class LanguageStateManagerTest {

    @Test
    fun `debounced persist fires after delay`() = runTest {
        var persisted: KeyboardLayout? = null
        val manager = LanguageStateManager(
            scope = this,
            persist = { persisted = it },
            debounceMs = 300L,
        )
        manager.updateLanguagePair(KeyboardLayout.EN, KeyboardLayout.RU)
        manager.onInput(LayoutInput.ToggleManual)
        assertEquals(null, persisted)
        advanceTimeBy(300L)
        advanceUntilIdle()
        assertEquals(KeyboardLayout.RU, persisted)
    }

    @Test
    fun `flush persists immediately`() = runTest {
        var persisted: KeyboardLayout? = null
        val manager = LanguageStateManager(
            scope = this,
            persist = { persisted = it },
        )
        manager.updateLanguagePair(KeyboardLayout.EN, KeyboardLayout.UA)
        manager.onInput(LayoutInput.ToggleShortcut)
        manager.flush()
        assertEquals(KeyboardLayout.UA, persisted)
    }
}
