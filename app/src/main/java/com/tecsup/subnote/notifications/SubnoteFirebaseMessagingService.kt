package com.tecsup.subnote.notifications

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class SubnoteFirebaseMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        val titulo = remoteMessage.notification?.title ?: "Subnote"
        val mensaje = remoteMessage.notification?.body ?: "Tienes una notificación"

        NotificationHelper.mostrarNotificacion(
            context = applicationContext,
            id = System.currentTimeMillis().toInt(),
            titulo = titulo,
            mensaje = mensaje
        )
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
    }
}