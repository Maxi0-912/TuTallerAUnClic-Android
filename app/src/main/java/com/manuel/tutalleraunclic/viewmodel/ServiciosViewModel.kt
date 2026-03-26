package com.manuel.tutalleraunclic.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.manuel.tutalleraunclic.data.model.entity.Servicio
import com.manuel.tutalleraunclic.data.repository.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ServiciosViewModel @Inject constructor(
    private val repository: MainRepository
) : ViewModel() {

    private val _servicios = MutableStateFlow<List<Servicio>>(emptyList())
    val servicios: StateFlow<List<Servicio>> = _servicios

    fun cargarServicios(establecimientoId: Int) {

        viewModelScope.launch {

            try {
                val response = repository.getServicios(establecimientoId)

                if (response.isSuccessful) {
                    _servicios.value = response.body() ?: emptyList()
                }

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}