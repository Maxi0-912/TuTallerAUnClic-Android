package com.manuel.tutalleraunclic.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.manuel.tutalleraunclic.data.model.request.CitaRequest
import com.manuel.tutalleraunclic.data.network.RetrofitClient
import com.manuel.tutalleraunclic.data.network.ApiService // 👈 ESTE ES CLAVE
import com.manuel.tutalleraunclic.data.repository.MainRepository
import com.manuel.tutalleraunclic.data.local.TokenManager
import kotlinx.coroutines.launch

class CitasViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = MainRepository(
        apiService = RetrofitClient.getApi()
    )

    private val tokenManager = TokenManager(application)

    fun crearCita(
        establecimiento: Int,
        agenda: Int,
        vehiculo: String,
        servicio: Int,
        fecha: String
    ) {
        viewModelScope.launch {

            val request = CitaRequest(
                establecimiento = establecimiento,
                agenda = agenda,
                vehiculo = vehiculo,
                servicio = servicio,
                fecha = fecha
            )

            val token = tokenManager.getToken() ?: return@launch

            repository.crearCita(token, request)
        }
    }
}