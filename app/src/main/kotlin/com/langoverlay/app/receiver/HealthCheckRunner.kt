package com.langoverlay.app.receiver

import android.content.Context
import com.langoverlay.app.di.BootHealthEntryPoint
import com.langoverlay.app.util.NotificationHelper
import com.langoverlay.app.util.PermissionUtils
import com.langoverlay.detection.accessibility.AccessibilityServiceLocator
import dagger.hilt.android.EntryPointAccessors
import kotlinx.coroutines.runBlocking

internal object HealthCheckRunner {

    fun perform(context: Context) {
        val entryPoint = EntryPointAccessors.fromApplication(
            context.applicationContext,
            BootHealthEntryPoint::class.java,
        )
        val settingsRepository = entryPoint.settingsRepository()

        val settings = runBlocking { settingsRepository.current() }
        if (!settings.startAtBoot) return

        val a11yEnabled = PermissionUtils.isAccessibilityServiceEnabled(context)
        val overlayGranted = PermissionUtils.canDrawOverlays(context)
        val serviceRunning = AccessibilityServiceLocator.isServiceRunning()

        if (!a11yEnabled || !overlayGranted || !serviceRunning) {
            NotificationHelper.showRestoreNotification(context)
        }
    }
}
