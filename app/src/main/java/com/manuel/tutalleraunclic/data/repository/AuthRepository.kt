package com.manuel.tutalleraunclic.data.repository

import com.manuel.tutalleraunclic.data.network.AuthApiService
import com.manuel.tutalleraunclic.data.model.request.LoginRequest
import retrofit2.Response
import javax.inject.Inject
import com.manuel.tutalleraunclic.data.local.TokenManager

class AuthRepository @Inject constructor(
    private val authApi: AuthApiService,
    private val tokenManager: TokenManager
) {

    suspend fun login(email: String, password: String): Result<Unit> {
        return try {

            val response = authApi.login(
                request = LoginRequest(username = email, password = password)
            )

            if (response.isSuccessful) {
                val body = response.body()!!
                val access = body.access
                val refresh = body.refresh
                if (access == null || refresh == null) {
                    return Result.failure(Exception("Respuesta de login inválida"))
                }

                // 🔥 Guardar tokens correctamente
                tokenManager.saveTokens(access, refresh)

                Result.success(Unit)

            } else {
                Result.failure(Exception("Error login: ${response.code()}"))
            }

        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}