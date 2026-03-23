package com.manuel.tutalleraunclic.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.manuel.tutalleraunclic.data.model.entity.Establecimiento
import com.manuel.tutalleraunclic.data.network.RetrofitClient
import com.manuel.tutalleraunclic.data.network.ApiService
import kotlinx.coroutines.launch
import androidx.compose.runtime.mutableStateListOf

class EstablecimientosViewModel : ViewModel() {

    val lista = mutableStateListOf<Establecimiento>()

    private val api = RetrofitClient.getApi()

    fun cargarEstablecimientos() {

        viewModelScope.launch {

            try {
                val response = api.getEstablecimientos()

                if (response.isSuccessful) {
                    lista.clear()
                    lista.addAll(response.body() ?: emptyList())
                } else {
                    println("ERROR API: ${response.code()}")
                }

            } catch (e: Exception) {
                println("ERROR CONEXION: ${e.message}")
            }
        }
    }
}