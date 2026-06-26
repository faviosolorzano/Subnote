package com.tecsup.subnote.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            // Al reiniciar el dispositivo, aquí se reprogramarían las notificaciones
            // Por ahora dejamos la base lista para cuando agreguemos el scheduler
        }
    }
}