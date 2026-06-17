package com.tecsup.subnote.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tecsup.subnote.data.local.Suscripcion
import com.tecsup.subnote.data.repository.SuscripcionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class DetalleUiState(
    val suscripcion: Suscripcion? = null,
    val cargando: Boolean = true
)

class DetalleViewModel(private val repository: SuscripcionRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(DetalleUiState())
    val uiState: StateFlow<DetalleUiState> = _uiState.asStateFlow()

    fun cargar(id: Long) {
        viewModelScope.launch {
            val resultado = repository.obtenerPorId(id)
            _uiState.value = DetalleUiState(suscripcion = resultado, cargando = false)
        }
    }
}