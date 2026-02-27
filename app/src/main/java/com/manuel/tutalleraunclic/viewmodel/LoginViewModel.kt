package com.manuel.tutalleraunclic.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.manuel.tutalleraunclic.data.model.LoginRequest
import com.manuel.tutalleraunclic.data.remote.RetrofitClient
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {

    fun login(username: String, password: String) {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.api.login(
                    LoginRequest(username, password)
                )
                println("TOKEN: ${response.access}")
            } catch (e: Exception) {
                println("ERROR: ${e.message}")
            }
        }
    }
}