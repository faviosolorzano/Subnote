package com.tecsup.subnote.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
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

    private val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
    private val monedaBase = "PEN"

    private val _uiState = MutableStateFlow(ListaUiState())
    val uiState: StateFlow<ListaUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            repository.obtenerTodas(userId).collect { lista ->
                // Cache de tasas por moneda: cada moneda se consulta UNA sola vez por emisión,
                // así no llamamos a la API una vez por suscripción.
                val cacheTasas = mutableMapOf<String, Double>()

                suspend fun aPEN(monto: Double, moneda: String): Double {
                    val tasa = cacheTasas[moneda] ?: run {
                        val t = try {
                            repository.obtenerTipoCambio(moneda, monedaBase)
                        } catch (e: Exception) {
                            1.0 // sin Internet: usamos el monto original como fallback
                        }
                        cacheTasas[moneda] = t
                        t
                    }
                    return monto * tasa
                }

                // Sumamos solo las mensuales, convirtiendo cada una a soles ANTES de sumar.
                var total = 0.0
                for (sub in lista.filter { it.cicloCobro == "mensual" }) {
                    total += aPEN(sub.monto, sub.moneda)
                }

                _uiState.value = ListaUiState(
                    suscripciones = lista,
                    gastoTotalMensual = total
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
