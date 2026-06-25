package com.tecsup.subnote.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class NotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val nombre = intent.getStringExtra("nombre") ?: "Suscripción"
        val monto = intent.getDoubleExtra("monto", 0.0)
        val moneda = intent.getStringExtra("moneda") ?: ""
        val notifId = intent.getIntExtra("notifId", 0)

        NotificationHelper.mostrarNotificacion(
            context = context,
            id = notifId,
            titulo = "Recordatorio de cobro — $nombre",
            mensaje = "Tu suscripción de $moneda ${"%.2f".format(monto)} se cobra mañana."
        )
    }
}