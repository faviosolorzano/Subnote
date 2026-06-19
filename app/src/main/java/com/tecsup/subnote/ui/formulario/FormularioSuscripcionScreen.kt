package com.tecsup.subnote.ui.formulario

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tecsup.subnote.data.local.Suscripcion
import com.tecsup.subnote.viewmodel.FormularioViewModel
import java.util.Calendar

private val SERVICIOS_POPULARES = listOf(
    "Netflix" to "Entretenimiento",
    "Spotify" to "Entretenimiento",
    "Disney+" to "Entretenimiento",
    "YouTube Premium" to "Entretenimiento",
    "Amazon Prime" to "Entretenimiento",
    "HBO Max" to "Entretenimiento",
    "Claude" to "IA & Tecnología",
    "ChatGPT" to "IA & Tecnología",
    "Midjourney" to "IA & Tecnología",
    "GitHub Copilot" to "IA & Tecnología",
    "iCloud" to "Cloud & Almacenamiento",
    "Google One" to "Cloud & Almacenamiento",
    "PlayStation Plus" to "Entretenimiento",
    "Xbox Game Pass" to "Entretenimiento"
)

private val CATEGORIAS = listOf(
    "Entretenimiento",
    "IA & Tecnología",
    "Productividad",
    "Cloud & Almacenamiento",
    "Compras",
    "Salud & Bienestar",
    "Hogar",
    "Otros"
)

private val MONEDAS = listOf("USD", "EUR", "PEN", "GBP", "MXN")

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
    var cicloCobro by remember { mutableStateOf("Mensual") }
    var categoria by remember { mutableStateOf("Entretenimiento") }
    var notas by remember { mutableStateOf("") }
    var fechaProximoCobro by remember { mutableStateOf(Calendar.getInstance().timeInMillis) }

    var expandedServicios by remember { mutableStateOf(false) }
    var expandedMonedas by remember { mutableStateOf(false) }
    var expandedCategorias by remember { mutableStateOf(false) }

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

            // Selector de Servicio (Dropdown + Manual)
            Column {
                Text(
                    text = "Nombre del servicio",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                    modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
                )
                ExposedDropdownMenuBox(
                    expanded = expandedServicios,
                    onExpandedChange = { expandedServicios = !expandedServicios }
                ) {
                    OutlinedTextField(
                        value = nombre,
                        onValueChange = { 
                            nombre = it
                            // Opcional: Si escribe algo que coincide con popular, autoseleccionar categoria
                            val coincide = SERVICIOS_POPULARES.find { p -> p.first.equals(it, ignoreCase = true) }
                            if (coincide != null) {
                                categoria = coincide.second
                            }
                        },
                        modifier = Modifier.fillMaxWidth().menuAnchor(MenuAnchorType.PrimaryEditable),
                        placeholder = { Text("Netflix, Spotify, etc.") },
                        leadingIcon = {
                            Icon(Icons.Rounded.PlayArrow, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedServicios) },
                        shape = RoundedCornerShape(16.dp),
                        colors = TextFieldDefaults.colors(
                            focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                            unfocusedIndicatorColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
                            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                            focusedContainerColor = MaterialTheme.colorScheme.surface
                        )
                    )
                    ExposedDropdownMenu(
                        expanded = expandedServicios,
                        onDismissRequest = { expandedServicios = false },
                        modifier = Modifier.background(MaterialTheme.colorScheme.surface)
                    ) {
                        SERVICIOS_POPULARES.forEach { (servicio, cat) ->
                            DropdownMenuItem(
                                text = { Text(servicio) },
                                onClick = {
                                    nombre = servicio
                                    categoria = cat
                                    expandedServicios = false
                                }
                            )
                        }
                    }
                }
            }

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
                
                // Selector de Moneda
                Column(modifier = Modifier.weight(0.4f)) {
                    Text(
                        text = "Moneda",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                        modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
                    )
                    ExposedDropdownMenuBox(
                        expanded = expandedMonedas,
                        onExpandedChange = { expandedMonedas = !expandedMonedas }
                    ) {
                        OutlinedTextField(
                            value = moneda,
                            onValueChange = {},
                            readOnly = true,
                            modifier = Modifier.fillMaxWidth().menuAnchor(MenuAnchorType.PrimaryNotEditable),
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedMonedas) },
                            shape = RoundedCornerShape(16.dp),
                            colors = TextFieldDefaults.colors(
                                focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                                unfocusedIndicatorColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
                                unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                                focusedContainerColor = MaterialTheme.colorScheme.surface
                            )
                        )
                        ExposedDropdownMenu(
                            expanded = expandedMonedas,
                            onDismissRequest = { expandedMonedas = false },
                            modifier = Modifier.background(MaterialTheme.colorScheme.surface)
                        ) {
                            MONEDAS.forEach { m ->
                                DropdownMenuItem(
                                    text = { Text(m) },
                                    onClick = {
                                        moneda = m
                                        expandedMonedas = false
                                    }
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Ciclo de Cobro (Mensual/Anual)
            Text(
                text = "Ciclo de cobro",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
            )
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                CicloCard(
                    label = "Mensual",
                    selected = cicloCobro.lowercase() == "mensual",
                    onClick = { cicloCobro = "Mensual" },
                    modifier = Modifier.weight(1f)
                )
                CicloCard(
                    label = "Anual",
                    selected = cicloCobro.lowercase() == "anual",
                    onClick = { cicloCobro = "Anual" },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Selector de Categoría
            Column {
                Text(
                    text = "Categoría",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                    modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
                )
                ExposedDropdownMenuBox(
                    expanded = expandedCategorias,
                    onExpandedChange = { expandedCategorias = !expandedCategorias }
                ) {
                    OutlinedTextField(
                        value = categoria,
                        onValueChange = { categoria = it },
                        modifier = Modifier.fillMaxWidth().menuAnchor(MenuAnchorType.PrimaryEditable),
                        leadingIcon = {
                            Icon(Icons.AutoMirrored.Rounded.List, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedCategorias) },
                        shape = RoundedCornerShape(16.dp),
                        colors = TextFieldDefaults.colors(
                            focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                            unfocusedIndicatorColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
                            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                            focusedContainerColor = MaterialTheme.colorScheme.surface
                        )
                    )
                    ExposedDropdownMenu(
                        expanded = expandedCategorias,
                        onDismissRequest = { expandedCategorias = false },
                        modifier = Modifier.background(MaterialTheme.colorScheme.surface)
                    ) {
                        CATEGORIAS.forEach { cat ->
                            DropdownMenuItem(
                                text = { Text(cat) },
                                onClick = {
                                    categoria = cat
                                    expandedCategorias = false
                                }
                            )
                        }
                    }
                }
            }

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
                                cicloCobro = cicloCobro.lowercase(),
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
                            cicloCobro = cicloCobro.lowercase(),
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

@Composable
fun CicloCard(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        modifier = modifier.height(56.dp),
        shape = RoundedCornerShape(16.dp),
        color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
        border = if (!selected) CardDefaults.outlinedCardBorder() else null
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                color = if (selected) Color.White else MaterialTheme.colorScheme.onSurface
            )
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
