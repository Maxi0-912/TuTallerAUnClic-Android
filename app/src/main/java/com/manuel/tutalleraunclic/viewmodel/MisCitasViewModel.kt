package com.manuel.tutalleraunclic.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.manuel.tutalleraunclic.data.model.entity.Cita
import com.manuel.tutalleraunclic.data.network.RetrofitClient
import com.manuel.tutalleraunclic.data.repository.MainRepository
import com.manuel.tutalleraunclic.data.local.TokenManager
import com.manuel.tutalleraunclic.data.network.ApiService
import kotlinx.coroutines.launch

class MisCitasViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = MainRepository(
        apiService = RetrofitClient.getApi()
    )
    private val tokenManager = TokenManager(application)

    val citas = MutableLiveData<List<Cita>>()

    fun cargarCitas() {

        viewModelScope.launch {

            val token = tokenManager.getToken() ?: return@launch

            val response = repository.misCitas(token)

            if (response.isSuccessful) {
                citas.value = response.body()
            }
        }
    }
}