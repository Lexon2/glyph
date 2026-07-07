package com.langoverlay.app.receiver

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.SystemClock
import com.langoverlay.app.di.BootHealthEntryPoint
import dagger.hilt.android.EntryPointAccessors
import kotlinx.coroutines.runBlocking

class BootHealthReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent?) {
        if (intent?.action != Intent.ACTION_BOOT_COMPLETED) return
        if (!shouldScheduleHealthCheck(context)) return
        scheduleHealthCheck(context)
    }

    private fun shouldScheduleHealthCheck(context: Context): Boolean {
        val entryPoint = EntryPointAccessors.fromApplication(
            context.applicationContext,
            BootHealthEntryPoint::class.java,
        )
        return runBlocking { entryPoint.settingsRepository().current().startAtBoot }
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

    private fun healthCheckPendingIntent(context: Context): PendingIntent {
        val intent = Intent(context, HealthCheckReceiver::class.java).apply {
            action = HealthCheckReceiver.ACTION_HEALTH_CHECK
            setPackage(context.packageName)
        }
        return PendingIntent.getBroadcast(
            context,
            HEALTH_CHECK_REQUEST_CODE,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
    }

    companion object {
        private const val HEALTH_CHECK_REQUEST_CODE = 2001
        private const val BOOT_CHECK_DELAY_MS = 60_000L
    }
}
