package com.tecsup.subnote.ui.detalle

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.tecsup.subnote.viewmodel.DetalleViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalleSuscripcionScreen(
    suscripcionId: Long,
    viewModel: DetalleViewModel,
    onBack: () -> Unit,
    onEditar: (Long) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(suscripcionId) {
        viewModel.cargar(suscripcionId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalle") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                actions = {
                    IconButton(onClick = { onEditar(suscripcionId) }) {
                        Icon(Icons.Filled.Edit, contentDescription = "Editar")
                    }
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            if (uiState.cargando) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else {
                val sub = uiState.suscripcion
                if (sub == null) {
                    Text(
                        "No se encontró la suscripción",
                        modifier = Modifier.align(Alignment.Center)
                    )
                } else {
                    Column(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth()
                    ) {
                        Text(sub.nombre, style = MaterialTheme.typography.headlineSmall)
                        Spacer(modifier = Modifier.height(16.dp))

                        Text("Monto original", style = MaterialTheme.typography.labelMedium)
                        Text(
                            "${sub.moneda} ${"%.2f".format(sub.monto)}",
                            style = MaterialTheme.typography.titleLarge
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        Text("Monto convertido", style = MaterialTheme.typography.labelMedium)
                        Text("Pendiente (lo integra Daniella con Retrofit)", style = MaterialTheme.typography.bodyMedium)
                        Spacer(modifier = Modifier.height(16.dp))

                        Text("Ciclo de cobro", style = MaterialTheme.typography.labelMedium)
                        Text(sub.cicloCobro, style = MaterialTheme.typography.bodyLarge)
                        Spacer(modifier = Modifier.height(16.dp))

                        Text("Categoría", style = MaterialTheme.typography.labelMedium)
                        Text(sub.categoria, style = MaterialTheme.typography.bodyLarge)
                        Spacer(modifier = Modifier.height(16.dp))

                        Text("Estado", style = MaterialTheme.typography.labelMedium)
                        Text(sub.estado, style = MaterialTheme.typography.bodyLarge)

                        if (sub.notas.isNotBlank()) {
                            Spacer(modifier = Modifier.height(16.dp))
                            Text("Notas", style = MaterialTheme.typography.labelMedium)
                            Text(sub.notas, style = MaterialTheme.typography.bodyLarge)
                        }
                    }
                }
            }
        }
    }
}