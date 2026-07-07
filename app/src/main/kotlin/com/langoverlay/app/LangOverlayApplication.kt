package com.langoverlay.app

import android.app.Application
import com.langoverlay.app.service.AppAccessibilityServiceLocator
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.langoverlay.app.data.SettingsRepository
import com.langoverlay.app.di.ApplicationScope

@HiltAndroidApp
class LangOverlayApplication : Application() {

    @Inject
    lateinit var accessibilityServiceLocator: AppAccessibilityServiceLocator

    @Inject
    lateinit var settingsRepository: SettingsRepository

    @Inject
    @ApplicationScope
    lateinit var applicationScope: CoroutineScope

    override fun onCreate() {
        super.onCreate()
        accessibilityServiceLocator
        applicationScope.launch {
            settingsRepository.current()
        }
    }
}
