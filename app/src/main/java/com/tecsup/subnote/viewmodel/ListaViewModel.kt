package com.tecsup.subnote.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tecsup.subnote.data.local.Suscripcion
import com.tecsup.subnote.data.repository.SuscripcionRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class ListaUiState(
    val suscripciones: List<Suscripcion> = emptyList(),
    val gastoTotalMensual: Double = 0.0
)

class ListaViewModel(private val repository: SuscripcionRepository) : ViewModel() {

    val uiState: StateFlow<ListaUiState> = repository.obtenerTodas()
        .map { lista ->
            val total = lista
                .filter { it.cicloCobro == "mensual" }
                .sumOf { it.monto }
            ListaUiState(suscripciones = lista, gastoTotalMensual = total)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = ListaUiState()
        )

    fun eliminar(suscripcion: Suscripcion) {
        viewModelScope.launch {
            repository.eliminar(suscripcion)
        }
    }
}