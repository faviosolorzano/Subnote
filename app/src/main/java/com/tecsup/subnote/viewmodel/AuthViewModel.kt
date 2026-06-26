package com.tecsup.subnote.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

data class AuthUiState(
    val usuario: FirebaseUser? = null,
    val cargando: Boolean = false,
    val error: String? = null,
    val exitoso: Boolean = false
)

class AuthViewModel : ViewModel() {

    private val auth = FirebaseAuth.getInstance()

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    val usuarioActual: FirebaseUser?
        get() = auth.currentUser

    fun registrar(email: String, password: String) {
        viewModelScope.launch {
            _uiState.value = AuthUiState(cargando = true)
            try {
                auth.createUserWithEmailAndPassword(email, password).await()
                _uiState.value = AuthUiState(
                    usuario = auth.currentUser,
                    exitoso = true
                )
            } catch (e: Exception) {
                _uiState.value = AuthUiState(error = e.message ?: "Error al registrarse")
            }
        }
    }

    fun iniciarSesion(email: String, password: String) {
        viewModelScope.launch {
            _uiState.value = AuthUiState(cargando = true)
            try {
                auth.signInWithEmailAndPassword(email, password).await()
                _uiState.value = AuthUiState(
                    usuario = auth.currentUser,
                    exitoso = true
                )
            } catch (e: Exception) {
                _uiState.value = AuthUiState(error = e.message ?: "Error al iniciar sesión")
            }
        }
    }

    fun cerrarSesion() {
        auth.signOut()
        _uiState.value = AuthUiState()
    }

    fun limpiarError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}