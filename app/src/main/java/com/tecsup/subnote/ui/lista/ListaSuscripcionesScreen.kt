package com.tecsup.subnote.ui.lista

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.tecsup.subnote.viewmodel.ListaViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListaSuscripcionesScreen(
    viewModel: ListaViewModel,
    onSuscripcionClick: (Long) -> Unit,
    onNuevaClick: () -> Unit,
    onResumenClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Subnote") })
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onNuevaClick) {
                Icon(Icons.Filled.Add, contentDescription = "Agregar suscripción")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Gasto mensual total", style = MaterialTheme.typography.bodyMedium)
                    Text(
                        "S/ ${"%.2f".format(uiState.gastoTotalMensual)}",
                        style = MaterialTheme.typography.headlineMedium
                    )
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = onResumenClick) {
                    Text("Ver resumen")
                }
            }

            if (uiState.suscripciones.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No tienes suscripciones registradas")
                }
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(uiState.suscripciones) { suscripcion ->
                        ListItem(
                            headlineContent = { Text(suscripcion.nombre) },
                            supportingContent = {
                                Text("${suscripcion.moneda} ${"%.2f".format(suscripcion.monto)} · ${suscripcion.cicloCobro}")
                            },
                            trailingContent = {
                                IconButton(onClick = { viewModel.eliminar(suscripcion) }) {
                                    Icon(Icons.Filled.Delete, contentDescription = "Eliminar")
                                }
                            },
                            modifier = Modifier.clickable { onSuscripcionClick(suscripcion.id) }
                        )
                        Divider()
                    }
                }
            }
        }
    }
}