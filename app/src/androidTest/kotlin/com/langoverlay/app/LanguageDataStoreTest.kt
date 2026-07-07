package com.langoverlay.app

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.langoverlay.app.data.SettingsDataStore
import com.langoverlay.core.model.LanguageListCodec
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LanguageDataStoreTest {

    @Test
    fun defaultSettingsContainOrderedLanguages() = runBlocking {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val dataStore = SettingsDataStore(context.applicationContext)
        val settings = dataStore.settings.first()
        assertTrue(settings.languages.size >= 2)
        assertEquals("en", settings.languages.first().id)
        assertEquals("ru", settings.languages[1].id)
    }

    @Test
    fun languageListCodecRoundTrip() {
        val languages = LanguageListCodec.defaultLanguages() + listOf(
            com.langoverlay.core.model.LanguageEntry("de", "DE"),
        )
        val encoded = LanguageListCodec.encode(languages)
        val decoded = LanguageListCodec.decode(encoded)
        assertEquals(languages, decoded)
    }
}
