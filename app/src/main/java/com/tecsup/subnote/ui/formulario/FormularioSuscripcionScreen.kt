package com.tecsup.subnote.ui.formulario

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
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.tecsup.subnote.data.local.Suscripcion
import com.tecsup.subnote.viewmodel.FormularioViewModel
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormularioSuscripcionScreen(
    suscripcionId: Long?,
    viewModel: FormularioViewModel,
    onBack: () -> Unit,
    onGuardado: () -> Unit
) {
    // Estado de cada campo del formulario.
    var idActual by remember { mutableStateOf<Long?>(null) }
    var nombre by remember { mutableStateOf("") }
    var monto by remember { mutableStateOf("") }
    var moneda by remember { mutableStateOf("USD") }
    var cicloCobro by remember { mutableStateOf("mensual") }
    var categoria by remember { mutableStateOf("Entretenimiento") }
    var notas by remember { mutableStateOf("") }
    var fechaProximoCobro by remember { mutableStateOf(Calendar.getInstance().timeInMillis) }

    val esEdicion = suscripcionId != null

    // Si entramos en modo edición, cargamos los datos existentes una sola vez.
    LaunchedEffect(suscripcionId) {
        if (suscripcionId != null) {
            val existente = viewModel.obtenerParaEditar(suscripcionId)
            existente?.let { sub ->
                idActual = sub.id
                nombre = sub.nombre
                monto = sub.monto.toString()
                moneda = sub.moneda
                cicloCobro = sub.cicloCobro
                categoria = sub.categoria
                notas = sub.notas
                fechaProximoCobro = sub.fechaProximoCobro
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (esEdicion) "Editar suscripción" else "Nueva suscripción") },
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
            OutlinedTextField(
                value = nombre,
                onValueChange = { nombre = it },
                label = { Text("Nombre del servicio") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = monto,
                onValueChange = { monto = it },
                label = { Text("Monto") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = moneda,
                onValueChange = { moneda = it.uppercase() },
                label = { Text("Moneda (USD, PEN, EUR)") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = cicloCobro,
                onValueChange = { cicloCobro = it },
                label = { Text("Ciclo de cobro (mensual/anual)") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = categoria,
                onValueChange = { categoria = it },
                label = { Text("Categoría") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = notas,
                onValueChange = { notas = it },
                label = { Text("Notas (opcional)") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    val montoDouble = monto.toDoubleOrNull() ?: 0.0
                    if (esEdicion && idActual != null) {
                        viewModel.actualizar(
                            Suscripcion(
                                id = idActual!!,
                                nombre = nombre,
                                monto = montoDouble,
                                moneda = moneda,
                                cicloCobro = cicloCobro,
                                fechaProximoCobro = fechaProximoCobro,
                                categoria = categoria,
                                notas = notas
                            ),
                            onGuardado = onGuardado
                        )
                    } else {
                        viewModel.guardarNueva(
                            nombre = nombre,
                            monto = montoDouble,
                            moneda = moneda,
                            cicloCobro = cicloCobro,
                            fechaProximoCobro = fechaProximoCobro,
                            categoria = categoria,
                            notas = notas,
                            onGuardado = onGuardado
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (esEdicion) "Actualizar" else "Guardar")
            }
        }
    }
}
