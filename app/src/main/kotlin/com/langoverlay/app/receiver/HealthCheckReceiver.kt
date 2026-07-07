package com.langoverlay.app.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

/**
 * Non-exported alarm target for post-boot health checks.
 * Only the app process can deliver [ACTION_HEALTH_CHECK] to this receiver.
 */
class HealthCheckReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent?) {
        if (intent?.action != ACTION_HEALTH_CHECK) return
        HealthCheckRunner.perform(context)
    }

    companion object {
        const val ACTION_HEALTH_CHECK = "com.langoverlay.app.action.HEALTH_CHECK"
    }
}
