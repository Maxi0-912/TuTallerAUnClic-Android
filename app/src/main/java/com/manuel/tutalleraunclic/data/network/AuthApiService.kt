package com.manuel.tutalleraunclic.network

import com.manuel.tutalleraunclic.data.model.response.LoginResponse
import com.manuel.tutalleraunclic.data.model.request.LoginRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApiService {

    @POST("auth/login/")
    suspend fun login(
        @Body request: LoginRequest
    ): Response<LoginResponse> // 🔥 AQUÍ ESTABA EL ERROR

    @POST("auth/refresh/")
    fun refreshToken(
        @Body body: Map<String, String>
    ): retrofit2.Call<LoginResponse>
}