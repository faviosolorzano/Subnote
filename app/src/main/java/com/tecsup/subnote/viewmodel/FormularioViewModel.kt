package com.tecsup.subnote.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.tecsup.subnote.data.local.Suscripcion
import com.tecsup.subnote.data.repository.SuscripcionRepository
import kotlinx.coroutines.launch

/**
 * ViewModel del formulario. Sirve para CREAR una suscripción nueva y para
 * EDITAR una existente, según si la pantalla recibe un id o no.
 */
class FormularioViewModel(private val repository: SuscripcionRepository) : ViewModel() {

    /** Trae la suscripción a editar (o null si no existe). La usa la pantalla al abrir en modo edición. */
    suspend fun obtenerParaEditar(id: Long): Suscripcion? {
        return repository.obtenerPorId(id)
    }

    /** Inserta una suscripción nueva y luego ejecuta [onGuardado] (normalmente: volver atrás). */
    fun guardarNueva(
        nombre: String,
        monto: Double,
        moneda: String,
        cicloCobro: String,
        fechaProximoCobro: Long,
        categoria: String,
        notas: String,
        onGuardado: () -> Unit
    ) {
        viewModelScope.launch {
            repository.insertar(
                Suscripcion(
                    nombre = nombre,
                    monto = monto,
                    moneda = moneda,
                    cicloCobro = cicloCobro,
                    fechaProximoCobro = fechaProximoCobro,
                    categoria = categoria,
                    notas = notas
                )
            )
            onGuardado()
        }
    }

    /** Actualiza una suscripción existente y luego ejecuta [onGuardado]. */
    fun actualizar(suscripcion: Suscripcion, onGuardado: () -> Unit) {
        viewModelScope.launch {
            repository.actualizar(suscripcion)
            onGuardado()
        }
    }
}

class FormularioViewModelFactory(private val repository: SuscripcionRepository) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return FormularioViewModel(repository) as T
    }
}
