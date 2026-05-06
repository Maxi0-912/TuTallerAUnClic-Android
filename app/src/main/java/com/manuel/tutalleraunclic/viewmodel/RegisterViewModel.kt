package com.manuel.tutalleraunclic.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.manuel.tutalleraunclic.data.model.request.LoginRequest
import com.manuel.tutalleraunclic.data.model.request.RegisterRequest
import com.manuel.tutalleraunclic.data.repository.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val repository: MainRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState: StateFlow<RegisterUiState> = _uiState.asStateFlow()

    private val _uiEvent = MutableSharedFlow<UiEvent>()
    val uiEvent: SharedFlow<UiEvent> = _uiEvent.asSharedFlow()

    /** @param rol  "cliente" → 2  |  "empresa" → 3 */
    fun register(
        username: String,
        email: String,
        password: String,
        rol: String,
        firstName: String = "",
        lastName: String = "",
        telefono: String = ""
    ) {
        viewModelScope.launch {

            // ── Validaciones ────────────────────────────────────────────────
            if (username.isBlank() || email.isBlank() || password.isBlank()) {
                _uiEvent.emit(UiEvent.ShowError("Usuario, correo y contraseña son obligatorios"))
                return@launch
            }
            if (password.length < 8) {
                _uiEvent.emit(UiEvent.ShowError("La contraseña debe tener al menos 8 caracteres"))
                return@launch
            }
            if (!email.contains('@')) {
                _uiEvent.emit(UiEvent.ShowError("Ingresa un correo electrónico válido"))
                return@launch
            }

            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            val rolInt = if (rol == "empresa") 3 else 2

            val request = RegisterRequest(
                username   = username.trim(),
                email      = email.trim(),
                password   = password.trim(),
                rol        = rolInt,
                first_name = firstName.trim(),
                last_name  = lastName.trim(),
                telefono   = telefono.trim()
            )

            // ── Registro ─────────────────────────────────────────────────────
            repository.register(request)
                .onSuccess {
                    // Auto-login con las mismas credenciales
                    repository.login(LoginRequest(username.trim(), password.trim()))
                        .onSuccess {
                            _uiState.update {
                                it.copy(isLoading = false, success = true, rolRegistrado = rol)
                            }
                        }
                        .onFailure { e ->
                            val msg = e.message ?: "Error al iniciar sesión automáticamente"
                            _uiState.update { it.copy(isLoading = false, errorMessage = msg) }
                            _uiEvent.emit(UiEvent.ShowError(msg))
                        }
                }
                .onFailure { e ->
                    val msg = e.message ?: "Error al registrar"
                    _uiState.update { it.copy(isLoading = false, errorMessage = msg) }
                    _uiEvent.emit(UiEvent.ShowError(msg))
                }
        }
    }
}
