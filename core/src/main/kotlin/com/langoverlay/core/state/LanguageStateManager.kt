package com.langoverlay.core.state

import com.langoverlay.core.model.AppSettings
import com.langoverlay.core.model.LanguageEntry
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
    private val persist: suspend (String) -> Unit,
    private val debounceMs: Long = 300L,
) {
    private val _currentLanguageId = MutableStateFlow("en")
    val currentLanguageId: StateFlow<String> = _currentLanguageId.asStateFlow()

    private val _displayLabel = MutableStateFlow("EN")
    val displayLabel: StateFlow<String> = _displayLabel.asStateFlow()

    private var languages: List<LanguageEntry> = com.langoverlay.core.model.LanguageListCodec.defaultLanguages()
    private var persistJob: Job? = null

    fun updateLanguages(newLanguages: List<LanguageEntry>) {
        require(newLanguages.size >= 2) { "At least two languages are required" }
        languages = newLanguages
        val resolved = AppSettings(
            languages = newLanguages,
            currentLanguageId = _currentLanguageId.value,
        ).resolvedCurrentLanguageId()
        if (resolved != _currentLanguageId.value) {
            _currentLanguageId.value = resolved
            schedulePersist(resolved)
        }
        updateDisplayLabel()
    }

    fun onInput(input: LayoutInput) {
        val next = CycleLogic.nextLanguageId(_currentLanguageId.value, languages, input)
        if (next != _currentLanguageId.value) {
            _currentLanguageId.value = next
            updateDisplayLabel()
            schedulePersist(next)
        }
    }

    suspend fun restoreLanguageId(savedId: String, configuredLanguages: List<LanguageEntry>) {
        languages = configuredLanguages
        val resolved = AppSettings(
            languages = configuredLanguages,
            currentLanguageId = savedId,
        ).resolvedCurrentLanguageId()
        _currentLanguageId.value = resolved
        updateDisplayLabel()
    }

    suspend fun flush() {
        persistJob?.cancel()
        persistJob = null
        persist(_currentLanguageId.value)
    }

    private fun updateDisplayLabel() {
        _displayLabel.value = languages.firstOrNull { it.id == _currentLanguageId.value }?.displayLabel
            ?: _currentLanguageId.value.uppercase()
    }

    private fun schedulePersist(languageId: String) {
        persistJob?.cancel()
        persistJob = scope.launch {
            delay(debounceMs)
            persist(languageId)
        }
    }
}
