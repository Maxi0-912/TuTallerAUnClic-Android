package com.manuel.tutalleraunclic.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.manuel.tutalleraunclic.data.model.request.LoginRequest
import com.manuel.tutalleraunclic.data.repository.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val repository: MainRepository
) : ViewModel() {

    val loading = MutableLiveData(false)
    val error = MutableLiveData<String?>(null)
    val success = MutableLiveData(false)

    fun login(username: String, password: String) {
        viewModelScope.launch {

            if (username.isBlank() || password.isBlank()) {
                error.value = "Campos vacíos"
                return@launch
            }

            loading.value = true
            error.value = null
            success.value = false

            try {
                val response = repository.login(LoginRequest(username, password))

                if (response.isSuccessful) {
                    val body = response.body()

                    if (body != null && !body.access.isNullOrEmpty()) {

                        // 🔥 GUARDAR TOKEN CORRECTO
                        repository.saveToken(body.access)

                        success.value = true
                    } else {
                        error.value = "Respuesta inválida"
                    }
                } else {
                    error.value = "Credenciales incorrectas"
                }

            } catch (e: Exception) {
                error.value = "Error: ${e.message}"
            } finally {
                loading.value = false
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            repository.logout() // 🔥 CORRECTO (suspend)
        }
    }
}