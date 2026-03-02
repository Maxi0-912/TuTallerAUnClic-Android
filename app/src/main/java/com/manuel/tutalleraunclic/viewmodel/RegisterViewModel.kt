package com.manuel.tutalleraunclic.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.manuel.tutalleraunclic.data.model.RegisterRequest
import com.manuel.tutalleraunclic.data.remote.RetrofitClient
import com.manuel.tutalleraunclic.data.repository.MainRepository
import kotlinx.coroutines.launch

class RegisterViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = MainRepository(RetrofitClient.api)

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
                    onResult(false)
                }

            } catch (e: Exception) {
                onResult(false)
            }
        }
    }
}