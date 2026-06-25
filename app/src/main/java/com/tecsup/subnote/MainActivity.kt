package com.tecsup.subnote

import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth
import com.tecsup.subnote.data.local.SubnoteDatabase
import com.tecsup.subnote.data.repository.SuscripcionRepository
import com.tecsup.subnote.notifications.NotificationHelper
import com.tecsup.subnote.ui.navigation.NavGraph
import com.tecsup.subnote.ui.theme.SubnoteTheme

class MainActivity : ComponentActivity() {

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Crear canal de notificaciones
        NotificationHelper.crearCanal(this)

        // Solicitar permiso de notificaciones en Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
            }
        }

        val database = SubnoteDatabase.getDatabase(applicationContext)
        val repository = SuscripcionRepository(database.suscripcionDao())
        val haySession = FirebaseAuth.getInstance().currentUser != null

        setContent {
            SubnoteTheme {
                NavGraph(
                    repository = repository,
                    haySession = haySession
                )
            }
        }
    }
}