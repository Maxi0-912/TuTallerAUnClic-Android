package com.manuel.tutalleraunclic.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.manuel.tutalleraunclic.data.local.TokenManager
import com.manuel.tutalleraunclic.data.model.request.UpdateUserRequest
import com.manuel.tutalleraunclic.data.repository.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: MainRepository,
    private val tokenManager: TokenManager
) : ViewModel() {

    fun actualizarPerfil(data: UpdateUserRequest) {
        viewModelScope.launch {
            try {
                val token = tokenManager.getToken()

                if (token.isNullOrEmpty()) {
                    Log.e("MainViewModel", "Token nulo o vacío")
                    return@launch
                }

                val response = repository.actualizarPerfil(data)

                if (response.isSuccessful) {
                    Log.d("MainViewModel", "Perfil actualizado correctamente")
                } else {
                    Log.e("MainViewModel", "Error: ${response.code()}")
                }

            } catch (e: Exception) {
                Log.e("MainViewModel", "Error conexión: ${e.message}")
            }
        }
    }
}