package com.langoverlay.app.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.langoverlay.app.MainActivity
import com.langoverlay.app.R

object NotificationHelper {

    private const val CHANNEL_ALERTS = "glyph_alerts"

    fun ensureChannels(context: Context) {
        val manager = context.getSystemService(NotificationManager::class.java)
        val channel = NotificationChannel(
            CHANNEL_ALERTS,
            context.getString(R.string.notification_channel_alerts),
            NotificationManager.IMPORTANCE_DEFAULT,
        )
        manager.createNotificationChannel(channel)
    }

    fun showRestoreNotification(context: Context) {
        ensureChannels(context)
        val intent = Intent(context, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
        val notification = NotificationCompat.Builder(context, CHANNEL_ALERTS)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(context.getString(R.string.notification_restore_title))
            .setContentText(context.getString(R.string.notification_restore_body))
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()
        NotificationManagerCompat.from(context).notify(RESTORE_NOTIFICATION_ID, notification)
    }

    fun showAccessibilityDisabledNotification(context: Context) {
        ensureChannels(context)
        val intent = PermissionUtils.accessibilitySettingsIntent()
        val pendingIntent = PendingIntent.getActivity(
            context,
            1,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
        val notification = NotificationCompat.Builder(context, CHANNEL_ALERTS)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(context.getString(R.string.notification_a11y_title))
            .setContentText(context.getString(R.string.notification_a11y_body))
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()
        NotificationManagerCompat.from(context).notify(A11Y_NOTIFICATION_ID, notification)
    }

    private const val RESTORE_NOTIFICATION_ID = 1001
    private const val A11Y_NOTIFICATION_ID = 1002
}
