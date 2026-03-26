package com.manuel.tutalleraunclic.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.manuel.tutalleraunclic.data.model.entity.Usuario
import com.manuel.tutalleraunclic.data.model.request.UpdateUserRequest
import com.manuel.tutalleraunclic.data.repository.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PerfilViewModel @Inject constructor(
    private val repository: MainRepository
) : ViewModel() {

    // ==========================
    // 🔹 STATE
    // ==========================

    private val _usuario = MutableStateFlow<Usuario?>(null)
    val usuario: StateFlow<Usuario?> = _usuario

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _success = MutableStateFlow(false)
    val success: StateFlow<Boolean> = _success

    private val _mensaje = MutableStateFlow("")
    val mensaje: StateFlow<String> = _mensaje

    // ==========================
    // 🔥 CARGAR PERFIL
    // ==========================
    fun cargarPerfil() {
        viewModelScope.launch {

            _loading.value = true
            _error.value = null

            try {
                val response = repository.getPerfil()

                if (response.isSuccessful) {
                    _usuario.value = response.body()
                } else {
                    _error.value = "Error perfil: ${response.code()}"
                }

            } catch (e: Exception) {
                _error.value = "Error de conexión"
                Log.e("PerfilVM", "Error cargarPerfil", e)
            }

            _loading.value = false
        }
    }

    // ==========================
    // 🔥 ACTUALIZAR PERFIL
    // ==========================
    fun actualizarPerfil(data: UpdateUserRequest) {
        viewModelScope.launch {

            _loading.value = true
            _error.value = null
            _mensaje.value = ""

            try {
                val response = repository.actualizarPerfil(data)

                if (response.isSuccessful) {
                    _mensaje.value = "Perfil actualizado correctamente"
                    _usuario.value = response.body()
                } else {
                    _error.value = "Error: ${response.code()}"
                }

            } catch (e: Exception) {
                _error.value = "Error de conexión"
                Log.e("PerfilVM", "Error actualizarPerfil", e)
            }

            _loading.value = false
        }
    }

    // ==========================
    // 🔥 ELIMINAR CUENTA
    // ==========================
    fun eliminarCuenta() {
        viewModelScope.launch {

            _loading.value = true
            _error.value = null

            try {
                val response = repository.eliminarCuenta()

                if (response.isSuccessful) {
                    _success.value = true
                } else {
                    _error.value = "Error: ${response.code()}"
                }

            } catch (e: Exception) {
                _error.value = "Error de conexión"
                Log.e("PerfilVM", "Error eliminarCuenta", e)
            }

            _loading.value = false
        }
    }
}