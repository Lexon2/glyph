package com.langoverlay.app.receiver

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.SystemClock
import com.langoverlay.app.di.BootHealthEntryPoint
import com.langoverlay.app.util.NotificationHelper
import com.langoverlay.app.util.PermissionUtils
import com.langoverlay.detection.accessibility.AccessibilityServiceLocator
import dagger.hilt.android.EntryPointAccessors
import kotlinx.coroutines.runBlocking

class BootHealthReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent?) {
        when (intent?.action) {
            Intent.ACTION_BOOT_COMPLETED -> scheduleHealthCheck(context)
            ACTION_HEALTH_CHECK -> performHealthCheck(context)
        }
    }

    private fun scheduleHealthCheck(context: Context) {
        val alarmManager = context.getSystemService(AlarmManager::class.java) ?: return
        val pendingIntent = healthCheckPendingIntent(context)
        alarmManager.setAndAllowWhileIdle(
            AlarmManager.ELAPSED_REALTIME_WAKEUP,
            SystemClock.elapsedRealtime() + BOOT_CHECK_DELAY_MS,
            pendingIntent,
        )
    }

    private fun performHealthCheck(context: Context) {
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

    private fun healthCheckPendingIntent(context: Context): PendingIntent {
        val intent = Intent(context, BootHealthReceiver::class.java).apply {
            action = ACTION_HEALTH_CHECK
        }
        return PendingIntent.getBroadcast(
            context,
            HEALTH_CHECK_REQUEST_CODE,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
    }

    companion object {
        const val ACTION_HEALTH_CHECK = "com.langoverlay.app.action.HEALTH_CHECK"
        private const val HEALTH_CHECK_REQUEST_CODE = 2001
        private const val BOOT_CHECK_DELAY_MS = 60_000L
    }
}
