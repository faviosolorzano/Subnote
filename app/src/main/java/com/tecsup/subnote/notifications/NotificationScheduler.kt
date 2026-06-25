package com.tecsup.subnote.notifications

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.tecsup.subnote.data.local.Suscripcion

object NotificationScheduler {

    fun programarRecordatorio(context: Context, suscripcion: Suscripcion) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val intent = Intent(context, NotificationReceiver::class.java).apply {
            putExtra("nombre", suscripcion.nombre)
            putExtra("monto", suscripcion.monto)
            putExtra("moneda", suscripcion.moneda)
            putExtra("notifId", suscripcion.id.toInt())
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            suscripcion.id.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val tiempoDisparo = suscripcion.fechaProximoCobro - (24 * 60 * 60 * 1000)

        if (tiempoDisparo > System.currentTimeMillis()) {
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    if (alarmManager.canScheduleExactAlarms()) {
                        alarmManager.setExactAndAllowWhileIdle(
                            AlarmManager.RTC_WAKEUP,
                            tiempoDisparo,
                            pendingIntent
                        )
                    } else {
                        // Si no tiene permiso de alarmas exactas, usa alarma inexacta
                        alarmManager.setAndAllowWhileIdle(
                            AlarmManager.RTC_WAKEUP,
                            tiempoDisparo,
                            pendingIntent
                        )
                    }
                } else {
                    alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        tiempoDisparo,
                        pendingIntent
                    )
                }
            } catch (e: SecurityException) {
                // Si lanza excepción, usa alarma inexacta como fallback
                alarmManager.setAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    tiempoDisparo,
                    pendingIntent
                )
            }
        }
    }

    fun cancelarRecordatorio(context: Context, suscripcionId: Long) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, NotificationReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            suscripcionId.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pendingIntent)
    }
}