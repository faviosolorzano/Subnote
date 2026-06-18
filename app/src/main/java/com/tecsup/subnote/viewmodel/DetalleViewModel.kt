package com.tecsup.subnote.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tecsup.subnote.data.local.Suscripcion
import com.tecsup.subnote.data.repository.SuscripcionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Estado del detalle.
 *
 * Campos originales de Favio: suscripcion, cargando.
 * Campos AGREGADOS por Daniella para la conversión de moneda:
 *  - monedaBase: la moneda a la que se convierte (por ahora "PEN").
 *  - montoConvertido: resultado de la conversión (null mientras no se calcula o si falla).
 *  - convirtiendo: estado de CARGA mientras se llama a la API.
 *  - errorConversion: true si no se pudo convertir (p. ej. sin Internet).
 */
data class DetalleUiState(
    val suscripcion: Suscripcion? = null,
    val cargando: Boolean = true,
    val monedaBase: String = "PEN",
    val montoConvertido: Double? = null,
    val convirtiendo: Boolean = false,
    val errorConversion: Boolean = false
)

class DetalleViewModel(private val repository: SuscripcionRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(DetalleUiState())
    val uiState: StateFlow<DetalleUiState> = _uiState.asStateFlow()

    fun cargar(id: Long) {
        viewModelScope.launch {
            val resultado = repository.obtenerPorId(id)
            _uiState.value = _uiState.value.copy(
                suscripcion = resultado,
                cargando = false
            )
            // Una vez cargada la suscripción, calculamos su monto convertido.
            if (resultado != null) {
                calcularConversion(resultado)
            }
        }
    }

    /**
     * Llama al Repository para obtener el tipo de cambio y calcula el monto convertido.
     * Maneja el estado de carga (convirtiendo) y el de error (errorConversion) para que,
     * si no hay Internet, la app NO se caiga y muestre algo razonable.
     */
    private fun calcularConversion(suscripcion: Suscripcion) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(convirtiendo = true, errorConversion = false)
            try {
                val tasa = repository.obtenerTipoCambio(
                    monedaOrigen = suscripcion.moneda,
                    monedaDestino = _uiState.value.monedaBase
                )
                _uiState.value = _uiState.value.copy(
                    montoConvertido = suscripcion.monto * tasa,
                    convirtiendo = false,
                    errorConversion = false
                )
            } catch (e: Exception) {
                // Sin conexión u otro error: dejamos montoConvertido en null y marcamos el error.
                _uiState.value = _uiState.value.copy(
                    montoConvertido = null,
                    convirtiendo = false,
                    errorConversion = true
                )
            }
        }
    }
}
