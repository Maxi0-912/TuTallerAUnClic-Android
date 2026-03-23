package com.manuel.tutalleraunclic.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.manuel.tutalleraunclic.data.model.request.LoginRequest
import com.manuel.tutalleraunclic.data.model.response.LoginResponse
import com.manuel.tutalleraunclic.data.network.RetrofitClient
import com.manuel.tutalleraunclic.data.repository.MainRepository
import com.manuel.tutalleraunclic.data.network.ApiService

import com.manuel.tutalleraunclic.data.local.TokenManager
import kotlinx.coroutines.launch

class LoginViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = MainRepository(
        apiService = RetrofitClient.getApi()
    )
    val loginResult = MutableLiveData<LoginResponse?>()

    fun login(username: String, password: String) {

        viewModelScope.launch {
            try {
                val response = repository.login(LoginRequest(username, password))

                if (response.isSuccessful) {
                    val loginResponse = response.body()

                    loginResponse?.let {
                        val tokenManager = TokenManager(getApplication())
                        tokenManager.saveToken(it.access)

                        loginResult.value = it
                    }

                } else {
                    println("ERROR BACKEND: ${response.code()} - ${response.errorBody()?.string()}")
                }

            } catch (e: Exception) {
                println("ERROR CONEXION: ${e.message}")
            }
        }
    }
}