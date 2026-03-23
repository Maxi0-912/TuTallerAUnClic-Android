package com.manuel.tutalleraunclic.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.manuel.tutalleraunclic.data.model.entity.Servicio
import com.manuel.tutalleraunclic.data.network.ApiService
import com.manuel.tutalleraunclic.data.network.RetrofitClient
import com.manuel.tutalleraunclic.data.repository.MainRepository
import kotlinx.coroutines.launch

class ServiciosViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = MainRepository(
        apiService = RetrofitClient.getApi()
    )

    val servicios = MutableLiveData<List<Servicio>>()

    fun cargarServicios(establecimientoId: Int) {

        viewModelScope.launch {

            val response = repository.getServicios(establecimientoId)

            if (response.isSuccessful) {

                servicios.value = response.body()

            }

        }

    }

}