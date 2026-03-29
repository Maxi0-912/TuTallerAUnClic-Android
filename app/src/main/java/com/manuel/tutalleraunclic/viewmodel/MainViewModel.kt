package com.manuel.tutalleraunclic.viewmodel

import android.util.Log
import androidx.lifecycle.*
import com.manuel.tutalleraunclic.data.model.request.UpdateUserRequest
import com.manuel.tutalleraunclic.data.repository.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: MainRepository
) : ViewModel() {

    // 🔄 Estado de carga
    private val _loading = MutableLiveData(false)
    val loading: LiveData<Boolean> = _loading

    // ✅ Éxito
    private val _success = MutableLiveData<Boolean>()
    val success: LiveData<Boolean> = _success

    // ❌ Error
    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    // 🚀 ACTUALIZAR PERFIL
    fun actualizarPerfil(data: UpdateUserRequest) {
        viewModelScope.launch {

            _error.value = null

            try {
                _loading.value = true

                // 🔥 SIN token manual → interceptor lo maneja
                val response = repository.actualizarPerfil(data)

                if (response.isSuccessful) {
                    _success.value = true
                } else {
                    _error.value = "Error ${response.code()}"
                }

            } catch (e: Exception) {
                _error.value = "Error de red: ${e.message}"
                Log.e("MainViewModel", "actualizarPerfil", e)
            } finally {
                _loading.value = false
            }
        }
    }

    // 🔄 Resetear estados (útil en UI)
    fun resetState() {
        _success.value = false
        _error.value = null
    }
}