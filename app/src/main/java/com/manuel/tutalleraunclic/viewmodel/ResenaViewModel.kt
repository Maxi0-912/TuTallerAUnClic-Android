package com.manuel.tutalleraunclic.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.manuel.tutalleraunclic.data.model.request.CalificacionRequest
import com.manuel.tutalleraunclic.data.repository.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ResenaViewModel @Inject constructor(
    private val repository: MainRepository
) : ViewModel() {

    var isLoading by mutableStateOf(false)
    var success by mutableStateOf(false)
    var error by mutableStateOf<String?>(null)

    fun enviarResena(citaId: Int, puntuacion: Int, comentario: String) {
        if (puntuacion < 1 || puntuacion > 5) {
            error = "Selecciona una calificación de 1 a 5 estrellas"
            return
        }
        viewModelScope.launch {
            isLoading = true
            error = null
            repository.crearCalificacion(
                CalificacionRequest(
                    prestacion = citaId,
                    puntuacion = puntuacion,
                    comentario = comentario
                )
            )
                .onSuccess {
                    isLoading = false
                    success = true
                }
                .onFailure { e ->
                    isLoading = false
                    error = e.message ?: "Error al enviar la reseña"
                }
        }
    }

    fun resetError() {
        error = null
    }
}
