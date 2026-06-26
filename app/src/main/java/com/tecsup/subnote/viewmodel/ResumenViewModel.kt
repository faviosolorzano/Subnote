package com.tecsup.subnote.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
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

    private val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
    private val monedaBase = "PEN"

    private val _uiState = MutableStateFlow(ResumenUiState())
    val uiState: StateFlow<ResumenUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            repository.obtenerTodas(userId).collect { lista ->
                // Cada moneda se consulta una sola vez por emisión (cache).
                val cacheTasas = mutableMapOf<String, Double>()

                suspend fun aPEN(monto: Double, moneda: String): Double {
                    val tasa = cacheTasas[moneda] ?: run {
                        val t = try {
                            repository.obtenerTipoCambio(moneda, monedaBase)
                        } catch (e: Exception) {
                            1.0 // sin Internet: fallback al monto original
                        }
                        cacheTasas[moneda] = t
                        t
                    }
                    return monto * tasa
                }

                var mensual = 0.0
                var anual = 0.0
                val porCategoria = mutableMapOf<String, Double>()

                for (sub in lista) {
                    val montoPEN = aPEN(sub.monto, sub.moneda)
                    when (sub.cicloCobro) {
                        "mensual" -> mensual += montoPEN
                        "anual"   -> anual += montoPEN
                    }
                    porCategoria[sub.categoria] =
                        (porCategoria[sub.categoria] ?: 0.0) + montoPEN
                }

                _uiState.value = ResumenUiState(
                    gastoTotalMensual = mensual,
                    gastoTotalAnual = anual + (mensual * 12.0),
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
