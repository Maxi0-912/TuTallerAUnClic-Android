package com.manuel.tutalleraunclic.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.manuel.tutalleraunclic.data.model.request.RegisterRequest
import com.manuel.tutalleraunclic.data.network.ApiService
import com.manuel.tutalleraunclic.data.network.RetrofitClient
import com.manuel.tutalleraunclic.data.repository.MainRepository
import kotlinx.coroutines.launch

class RegisterViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = MainRepository(
        apiService = RetrofitClient.getApi()
    )

    fun register(
        username: String,
        email: String,
        password: String,
        onResult: (Boolean) -> Unit
    ) {
        viewModelScope.launch {
            try {

                val request = RegisterRequest(
                    username = username,
                    email = email,
                    password = password
                )

                val response = repository.register(request)

                if (response.isSuccessful) {
                    onResult(true)
                } else {
                    println("Error: ${response.errorBody()?.string()}")
                    onResult(false)
                }

            } catch (e: Exception) {
                e.printStackTrace()
                onResult(false)
            }
        }
    }
}