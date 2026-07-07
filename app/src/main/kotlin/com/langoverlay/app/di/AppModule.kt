package com.langoverlay.app.di

import android.content.Context
import com.langoverlay.app.data.SettingsDataStore
import com.langoverlay.app.data.SettingsRepository
import com.langoverlay.app.service.AppAccessibilityServiceLocator
import com.langoverlay.core.state.LanguageStateManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import javax.inject.Qualifier
import javax.inject.Singleton

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class ApplicationScope

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    @ApplicationScope
    fun provideApplicationScope(): CoroutineScope {
        return CoroutineScope(SupervisorJob() + Dispatchers.Default)
    }

    @Provides
    @Singleton
    fun provideSettingsDataStore(
        @ApplicationContext context: Context,
    ): SettingsDataStore = SettingsDataStore(context)

    @Provides
    @Singleton
    fun provideLanguageStateManager(
        @ApplicationScope scope: CoroutineScope,
        settingsRepository: SettingsRepository,
    ): LanguageStateManager {
        return LanguageStateManager(
            scope = scope,
            persist = { layout -> settingsRepository.persistLayout(layout) },
        )
    }
}
