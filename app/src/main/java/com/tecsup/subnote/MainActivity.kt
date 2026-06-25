package com.tecsup.subnote

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.google.firebase.auth.FirebaseAuth
import com.tecsup.subnote.data.local.SubnoteDatabase
import com.tecsup.subnote.data.repository.SuscripcionRepository
import com.tecsup.subnote.ui.navigation.NavGraph
import com.tecsup.subnote.ui.theme.SubnoteTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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