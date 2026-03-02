package com.manuel.tutalleraunclic.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.manuel.tutalleraunclic.data.model.Establecimiento
import com.manuel.tutalleraunclic.data.repository.MainRepository
import kotlinx.coroutines.launch
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.State
import com.manuel.tutalleraunclic.data.remote.RetrofitClient

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = MainRepository(RetrofitClient.api)

    private val _establecimientos = mutableStateOf<List<Establecimiento>>(emptyList())
    val establecimientos: State<List<Establecimiento>> = _establecimientos

    fun cargarEstablecimientos() {
        viewModelScope.launch {
            try {
                val response = repository.getEstablecimientos()

                if (response.isSuccessful) {
                    val lista = response.body()
                    _establecimientos.value = lista ?: emptyList()
                } else {
                    println("Error: ${response.code()}")
                }

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}