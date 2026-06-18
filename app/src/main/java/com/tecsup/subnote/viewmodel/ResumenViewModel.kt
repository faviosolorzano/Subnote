package com.tecsup.subnote.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.tecsup.subnote.data.repository.SuscripcionRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

/**
 * Estado de la pantalla de resumen.
 * - gastoTotalMensual: suma de las suscripciones con ciclo "mensual".
 * - gastoTotalAnual: equivalente anual = anuales + (mensuales * 12).
 * - gastoPorCategoria: cuánto se gasta en cada categoría.
 */
data class ResumenUiState(
    val gastoTotalMensual: Double = 0.0,
    val gastoTotalAnual: Double = 0.0,
    val gastoPorCategoria: Map<String, Double> = emptyMap()
)

class ResumenViewModel(private val repository: SuscripcionRepository) : ViewModel() {

    // Se recalcula automáticamente cada vez que cambia la lista de suscripciones en Room.
    val uiState: StateFlow<ResumenUiState> = repository.obtenerTodas()
        .map { lista ->
            val mensual = lista.filter { it.cicloCobro == "mensual" }.sumOf { it.monto }
            val anual = lista.filter { it.cicloCobro == "anual" }.sumOf { it.monto }
            val totalAnualEquivalente = anual + (mensual * 12.0)

            val porCategoria = lista.groupBy { it.categoria }
                .mapValues { (_, subs) -> subs.sumOf { it.monto } }

            ResumenUiState(
                gastoTotalMensual = mensual,
                gastoTotalAnual = totalAnualEquivalente,
                gastoPorCategoria = porCategoria
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = ResumenUiState()
        )
}

class ResumenViewModelFactory(private val repository: SuscripcionRepository) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ResumenViewModel(repository) as T
    }
}
