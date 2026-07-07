package com.langoverlay.core.state

import com.langoverlay.core.model.KeyboardLayout
import com.langoverlay.core.model.LayoutInput
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LanguageStateManager(
    private val scope: CoroutineScope,
    private val persist: suspend (KeyboardLayout) -> Unit,
    private val debounceMs: Long = 300L,
) {
    private val _layout = MutableStateFlow(KeyboardLayout.EN)
    val layout: StateFlow<KeyboardLayout> = _layout.asStateFlow()

    private var languageA: KeyboardLayout = KeyboardLayout.EN
    private var languageB: KeyboardLayout = KeyboardLayout.RU
    private var persistJob: Job? = null

    fun updateLanguagePair(languageA: KeyboardLayout, languageB: KeyboardLayout) {
        this.languageA = languageA
        this.languageB = languageB
    }

    fun onInput(input: LayoutInput) {
        val next = ToggleLogic.nextLayout(_layout.value, languageA, languageB, input)
        if (next != _layout.value) {
            _layout.value = next
            schedulePersist(next)
        }
    }

    suspend fun restoreLayout(saved: KeyboardLayout) {
        _layout.value = saved
    }

    suspend fun flush() {
        persistJob?.cancel()
        persistJob = null
        persist(_layout.value)
    }

    private fun schedulePersist(layout: KeyboardLayout) {
        persistJob?.cancel()
        persistJob = scope.launch {
            delay(debounceMs)
            persist(layout)
        }
    }
}
