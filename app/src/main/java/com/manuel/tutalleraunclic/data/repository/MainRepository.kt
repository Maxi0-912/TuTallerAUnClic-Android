package com.manuel.tutalleraunclic.data.repository


import com.manuel.tutalleraunclic.data.remote.ApiService
import com.manuel.tutalleraunclic.data.model.*

import retrofit2.Response

class MainRepository(private val apiService: ApiService) {

    suspend fun login(request: LoginRequest): Response<LoginResponse> {
        return apiService.login(request)
    }

    suspend fun register(request: RegisterRequest): Response<Unit> {
        return apiService.register(request)
    }

    suspend fun getEstablecimientos() =
        apiService.getEstablecimientos()
}