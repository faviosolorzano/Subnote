package com.tecsup.subnote.ui.formulario

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.List
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
    var idActual by remember { mutableStateOf<Long?>(null) }
    var nombre by remember { mutableStateOf("") }
    var monto by remember { mutableStateOf("") }
    var moneda by remember { mutableStateOf("USD") }
    var cicloCobro by remember { mutableStateOf("mensual") }
    var categoria by remember { mutableStateOf("Entretenimiento") }
    var notas by remember { mutableStateOf("") }
    var fechaProximoCobro by remember { mutableStateOf(Calendar.getInstance().timeInMillis) }

    val esEdicion = suscripcionId != null

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
                title = { 
                    Text(
                        if (esEdicion) "Editar suscripción" else "Nueva suscripción",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    ) 
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp)
        ) {
            Text(
                "Detalles del servicio",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            CustomTextField(
                value = nombre,
                onValueChange = { nombre = it },
                label = "Nombre del servicio",
                placeholder = "Netflix, Spotify, etc.",
                icon = Icons.Rounded.PlayArrow
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(modifier = Modifier.fillMaxWidth()) {
                Box(modifier = Modifier.weight(0.6f)) {
                    CustomTextField(
                        value = monto,
                        onValueChange = { monto = it },
                        label = "Monto",
                        placeholder = "0.00",
                        icon = Icons.Rounded.ShoppingCart,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Box(modifier = Modifier.weight(0.4f)) {
                    CustomTextField(
                        value = moneda,
                        onValueChange = { moneda = it.uppercase() },
                        label = "Moneda",
                        placeholder = "USD",
                        icon = Icons.Rounded.Settings
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            CustomTextField(
                value = cicloCobro,
                onValueChange = { cicloCobro = it },
                label = "Ciclo de cobro",
                placeholder = "mensual o anual",
                icon = Icons.Rounded.Refresh
            )

            Spacer(modifier = Modifier.height(16.dp))

            CustomTextField(
                value = categoria,
                onValueChange = { categoria = it },
                label = "Categoría",
                placeholder = "Entretenimiento, Hogar, etc.",
                icon = Icons.AutoMirrored.Rounded.List
            )

            Spacer(modifier = Modifier.height(16.dp))

            CustomTextField(
                value = notas,
                onValueChange = { notas = it },
                label = "Notas adicionales",
                placeholder = "Agrega algún detalle...",
                icon = Icons.Rounded.Info,
                singleLine = false,
                minLines = 3
            )

            Spacer(modifier = Modifier.height(40.dp))

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
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text(
                    if (esEdicion) "Actualizar Suscripción" else "Guardar Suscripción",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
            
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    singleLine: Boolean = true,
    minLines: Int = 1
) {
    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
            modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
        )
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text(placeholder) },
            leadingIcon = {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
            },
            shape = RoundedCornerShape(16.dp),
            colors = TextFieldDefaults.colors(
                focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                unfocusedIndicatorColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
                unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                focusedContainerColor = MaterialTheme.colorScheme.surface
            ),
            keyboardOptions = keyboardOptions,
            singleLine = singleLine,
            minLines = minLines
        )
    }
}
