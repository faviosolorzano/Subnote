package com.tecsup.subnote.ui.resumen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.tecsup.subnote.viewmodel.ResumenViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResumenScreen(
    viewModel: ResumenViewModel,
    onBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    // El máximo se usa para que la barra más grande llene el 100% del ancho.
    val maximo = uiState.gastoPorCategoria.values.maxOrNull() ?: 1.0

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Resumen") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                "Gasto mensual: S/ ${"%.2f".format(uiState.gastoTotalMensual)}",
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "Gasto anual estimado: S/ ${"%.2f".format(uiState.gastoTotalAnual)}",
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(24.dp))

            Text("Gasto por categoría", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(12.dp))

            if (uiState.gastoPorCategoria.isEmpty()) {
                Text("Aún no hay suscripciones registradas.")
            } else {
                uiState.gastoPorCategoria.forEach { (categoria, monto) ->
                    Column(modifier = Modifier.padding(bottom = 12.dp)) {
                        Text("$categoria — S/ ${"%.2f".format(monto)}")
                        Spacer(modifier = Modifier.height(4.dp))
                        LinearProgressIndicator(
                            progress = { (monto / maximo).toFloat() },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                        )
                    }
                }
            }
        }
    }
}
