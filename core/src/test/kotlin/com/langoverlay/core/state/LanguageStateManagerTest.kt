package com.langoverlay.core.state

import com.langoverlay.core.model.LanguageEntry
import com.langoverlay.core.model.LayoutInput
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

@OptIn(ExperimentalCoroutinesApi::class)
class LanguageStateManagerTest {

    private val enRu = listOf(
        LanguageEntry("en", "EN"),
        LanguageEntry("ru", "RU"),
    )

    @Test
    fun `debounced persist fires after delay`() = runTest {
        var persisted: String? = null
        val manager = LanguageStateManager(
            scope = this,
            persist = { persisted = it },
            debounceMs = 300L,
        )
        manager.updateLanguages(enRu)
        manager.onInput(LayoutInput.ToggleManual)
        assertEquals(null, persisted)
        advanceTimeBy(300L)
        advanceUntilIdle()
        assertEquals("ru", persisted)
    }

    @Test
    fun `flush persists immediately`() = runTest {
        var persisted: String? = null
        val manager = LanguageStateManager(
            scope = this,
            persist = { persisted = it },
        )
        manager.updateLanguages(
            listOf(
                LanguageEntry("en", "EN"),
                LanguageEntry("ua", "UA"),
            ),
        )
        manager.onInput(LayoutInput.ToggleShortcut)
        manager.flush()
        assertEquals("ua", persisted)
    }
}
