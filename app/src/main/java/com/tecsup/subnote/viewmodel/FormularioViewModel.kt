package com.tecsup.subnote.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.tecsup.subnote.data.local.Suscripcion
import com.tecsup.subnote.data.repository.SuscripcionRepository
import kotlinx.coroutines.launch

class FormularioViewModel(private val repository: SuscripcionRepository) : ViewModel() {

    private val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

    suspend fun obtenerParaEditar(id: Long): Suscripcion? {
        return repository.obtenerPorId(id)
    }

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
                    userId = userId,
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