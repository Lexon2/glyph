package com.langoverlay.app.di

import com.langoverlay.app.data.SettingsRepository
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@EntryPoint
@InstallIn(SingletonComponent::class)
interface BootHealthEntryPoint {
    fun settingsRepository(): SettingsRepository
}
