package com.manuel.tutalleraunclic.viewmodel

import android.content.Context
import android.util.Log
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.GetCredentialException
import androidx.credentials.exceptions.NoCredentialException
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.manuel.tutalleraunclic.data.model.request.LoginRequest
import com.manuel.tutalleraunclic.data.repository.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class LoginUiState {
    object Idle              : LoginUiState()
    object Loading           : LoginUiState()
    object NeedsRolSelection : LoginUiState()
    data class Success(val rolNombre: String) : LoginUiState()
    data class Error(val message: String)     : LoginUiState()
}

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val repository: MainRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<LoginUiState>(LoginUiState.Idle)
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun login(username: String, password: String) {
        if (username.isBlank() || password.isBlank()) return
        viewModelScope.launch {
            _uiState.value = LoginUiState.Loading
            repository.login(LoginRequest(username.trim(), password.trim()))
                .onSuccess { response ->
                    _uiState.value = LoginUiState.Success(response.rolNombre)
                }
                .onFailure { e ->
                    _uiState.value = LoginUiState.Error(mapError(e.message))
                }
        }
    }

    fun signInWithGoogle(context: Context) {
        viewModelScope.launch {
            _uiState.value = LoginUiState.Loading
            try {
                val credentialManager = CredentialManager.create(context)

                val result = try {
                    val quickOption = GetGoogleIdOption.Builder()
                        .setFilterByAuthorizedAccounts(true)
                        .setServerClientId(WEB_CLIENT_ID)
                        .build()
                    credentialManager.getCredential(
                        context = context,
                        request = GetCredentialRequest.Builder()
                            .addCredentialOption(quickOption)
                            .build()
                    )
                } catch (e: NoCredentialException) {
                    Log.d(TAG, "Sin cuenta previa, abriendo selector completo")
                    val fullOption = GetGoogleIdOption.Builder()
                        .setFilterByAuthorizedAccounts(false)
                        .setServerClientId(WEB_CLIENT_ID)
                        .build()
                    credentialManager.getCredential(
                        context = context,
                        request = GetCredentialRequest.Builder()
                            .addCredentialOption(fullOption)
                            .build()
                    )
                }

                val idToken = GoogleIdTokenCredential
                    .createFrom(result.credential.data)
                    .idToken

                Log.d(TAG, "idToken obtenido, enviando al backend")

                repository.loginWithGoogle(idToken)
                    .onSuccess { response ->
                        if (response.rolNombreDirecto.isNullOrBlank()) {
                            _uiState.value = LoginUiState.NeedsRolSelection
                        } else {
                            _uiState.value = LoginUiState.Success(response.rolNombre)
                        }
                    }
                    .onFailure { e ->
                        _uiState.value = LoginUiState.Error(mapError(e.message))
                    }

            } catch (e: GetCredentialCancellationException) {
                Log.d(TAG, "Usuario canceló el selector de Google")
                _uiState.value = LoginUiState.Idle
            } catch (e: GetCredentialException) {
                Log.e(TAG, "GetCredentialException: ${e.message}")
                _uiState.value = LoginUiState.Error("Error con Google Sign-In: ${e.message}")
            } catch (e: Exception) {
                Log.e(TAG, "Exception inesperada: ${e.message}")
                _uiState.value = LoginUiState.Error(mapError(e.message))
            }
        }
    }

    fun resetState() { _uiState.value = LoginUiState.Idle }

    fun logout() {
        viewModelScope.launch {
            repository.logout()
            _uiState.value = LoginUiState.Idle
        }
    }

    private fun mapError(message: String?): String = when {
        message == null -> "Error al iniciar sesión"
        message.contains("password", ignoreCase = true) ||
                message.contains("credencial", ignoreCase = true) -> "Contraseña incorrecta"
        message.contains("user", ignoreCase = true) ||
                message.contains("username", ignoreCase = true) ||
                message.contains("encontrado", ignoreCase = true) -> "Usuario no encontrado"
        message.contains("internet", ignoreCase = true) ||
                message.contains("network", ignoreCase = true) ||
                message.contains("connect", ignoreCase = true) ||
                message.contains("timeout", ignoreCase = true) -> "Sin conexión a internet"
        else -> message
    }

    companion object {
        private const val TAG = "GoogleSignIn"
        private const val WEB_CLIENT_ID =
            "467844112079-9995fc8stij6t4unq4m0gdgl56htdrhh.apps.googleusercontent.com"
    }
}