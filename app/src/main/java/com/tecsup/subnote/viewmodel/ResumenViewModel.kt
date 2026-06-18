package com.tecsup.subnote.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.tecsup.subnote.data.repository.SuscripcionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ResumenUiState(
    val gastoTotalMensual: Double = 0.0,
    val gastoTotalAnual: Double = 0.0,
    val gastoPorCategoria: Map<String, Double> = emptyMap()
)

class ResumenViewModel(private val repository: SuscripcionRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(ResumenUiState())
    val uiState: StateFlow<ResumenUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            repository.obtenerTodas().collect { lista ->
                // Convertimos cada monto a PEN antes de sumar.
                // Si la API falla para alguna suscripción, usamos su monto original.
                suspend fun convertirAPEN(monto: Double, moneda: String): Double {
                    return try {
                        val tasa = repository.obtenerTipoCambio(moneda, "PEN")
                        monto * tasa
                    } catch (e: Exception) {
                        monto
                    }
                }

                var mensualPEN = 0.0
                var anualPEN = 0.0
                val porCategoria = mutableMapOf<String, Double>()

                for (sub in lista) {
                    val montoPEN = convertirAPEN(sub.monto, sub.moneda)
                    when (sub.cicloCobro) {
                        "mensual" -> mensualPEN += montoPEN
                        "anual"   -> anualPEN += montoPEN
                    }
                    porCategoria[sub.categoria] =
                        (porCategoria[sub.categoria] ?: 0.0) + montoPEN
                }

                _uiState.value = ResumenUiState(
                    gastoTotalMensual = mensualPEN,
                    gastoTotalAnual = anualPEN + (mensualPEN * 12.0),
                    gastoPorCategoria = porCategoria
                )
            }
        }
    }
}

class ResumenViewModelFactory(private val repository: SuscripcionRepository) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ResumenViewModel(repository) as T
    }
}