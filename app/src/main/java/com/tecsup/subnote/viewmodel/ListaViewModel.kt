package com.tecsup.subnote.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tecsup.subnote.data.local.Suscripcion
import com.tecsup.subnote.data.repository.SuscripcionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ListaUiState(
    val suscripciones: List<Suscripcion> = emptyList(),
    val gastoTotalMensual: Double = 0.0
)

class ListaViewModel(private val repository: SuscripcionRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(ListaUiState())
    val uiState: StateFlow<ListaUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            repository.obtenerTodas().collect { lista ->
                // Para cada suscripción mensual, convertimos su monto a PEN antes de sumar.
                // Si la API falla (sin Internet), usamos el monto original como fallback.
                val totalPEN = lista
                    .filter { it.cicloCobro == "mensual" }
                    .sumOf { sub ->
                        try {
                            val tasa = repository.obtenerTipoCambio(sub.moneda, "PEN")
                            sub.monto * tasa
                        } catch (e: Exception) {
                            sub.monto // fallback sin red
                        }
                    }
                _uiState.value = ListaUiState(
                    suscripciones = lista,
                    gastoTotalMensual = totalPEN
                )
            }
        }
    }

    fun eliminar(suscripcion: Suscripcion) {
        viewModelScope.launch {
            repository.eliminar(suscripcion)
        }
    }
}