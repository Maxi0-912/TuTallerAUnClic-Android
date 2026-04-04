package com.manuel.tutalleraunclic.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.manuel.tutalleraunclic.data.model.request.LoginRequest
import com.manuel.tutalleraunclic.data.model.request.RegisterRequest
import com.manuel.tutalleraunclic.data.repository.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val repository: MainRepository
) : ViewModel() {

    var loading = mutableStateOf(false)
    var error = mutableStateOf<String?>(null)

    fun register(
        username: String,
        email: String,
        password: String,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {

            loading.value = true
            error.value = null

            try {
                // 🔥 1. REGISTRO
                val registerResponse = repository.register(
                    RegisterRequest(username, email, password)
                )

                if (!registerResponse.isSuccessful) {
                    error.value = "Error al registrar"
                    loading.value = false
                    return@launch
                }

                // 🔥 2. LOGIN AUTOMÁTICO
                val loginResponse = repository.login(
                    LoginRequest(username, password)
                )

                if (loginResponse.isSuccessful) {

                    val token = loginResponse.body()?.access

                    if (token != null) {
                        repository.saveToken(token) // 🔥 IMPORTANTE
                        onSuccess()
                    } else {
                        error.value = "Token inválido"
                    }

                } else {
                    error.value = "Error al iniciar sesión"
                }

            } catch (e: Exception) {
                error.value = "Error de conexión"
            }

            loading.value = false
        }
    }
}