package com.manuel.tutalleraunclic.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.manuel.tutalleraunclic.data.model.request.LoginRequest
import com.manuel.tutalleraunclic.data.repository.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val repository: MainRepository
) : ViewModel() {

    // ── UI state ─────────────────────────────────────────────────────────────

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading.asStateFlow()

    private val _error = MutableSharedFlow<String>()
    val error: SharedFlow<String> = _error.asSharedFlow()

    private val _success = MutableStateFlow(false)
    val success: StateFlow<Boolean> = _success.asStateFlow()

    /**
     * Set after a successful login. Values: "cliente", "empresa", or null (treated as "cliente").
     * Guaranteed to be set before [success] becomes true.
     */
    private val _rolNombre = MutableStateFlow<String?>(null)
    val rolNombre: StateFlow<String?> = _rolNombre.asStateFlow()

    // ── Login ─────────────────────────────────────────────────────────────────

    fun login(username: String, password: String) {
        if (username.isBlank() || password.isBlank()) return

        viewModelScope.launch {
            _loading.value = true

            repository.login(LoginRequest(username.trim(), password.trim()))
                .onSuccess { loginResponse ->
                    // rolNombre is set BEFORE success so observers read it correctly
                    _rolNombre.value = loginResponse.rolNombre
                    _success.value = true
                }
                .onFailure { e ->
                    _error.emit(e.message ?: "Credenciales incorrectas")
                }

            _loading.value = false
        }
    }

    // ── Logout ────────────────────────────────────────────────────────────────

    fun logout() {
        viewModelScope.launch {
            repository.logout()
            _success.value = false
            _rolNombre.value = null
        }
    }
}
