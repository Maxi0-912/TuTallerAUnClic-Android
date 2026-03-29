package com.manuel.tutalleraunclic.network

import com.manuel.tutalleraunclic.data.local.TokenManager
import com.manuel.tutalleraunclic.data.model.response.LoginResponse
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import retrofit2.Call
import javax.inject.Inject
import javax.inject.Provider

class TokenAuthenticator @Inject constructor(
    private val tokenManager: TokenManager,
    private val apiServiceProvider: Provider<AuthApiService>
) : Authenticator {

    override fun authenticate(route: Route?, response: Response): Request? {

        // ⚠️ Evitar loop infinito de reintentos
        if (responseCount(response) >= 2) return null

        val refreshToken = tokenManager.getRefreshToken() ?: return null

        val apiService = apiServiceProvider.get()

        val refreshResponse = try {
            apiService.refreshToken(
                mapOf("refresh" to refreshToken)
            ).execute()
        } catch (e: Exception) {
            return null
        }

        return if (refreshResponse.isSuccessful) {

            val body: LoginResponse = refreshResponse.body() ?: return null
            val newAccess = body.access

            // 🔥 Guardar nuevo access token
            tokenManager.saveTokens(newAccess, refreshToken)

            // 🔥 Reintentar request original con nuevo token
            response.request.newBuilder()
                .header("Authorization", "Bearer $newAccess")
                .build()

        } else {
            null
        }
    }

    // 🔁 Cuenta cuántas veces se ha intentado esta request (evita loops)
    private fun responseCount(response: Response): Int {
        var result = 1
        var prior = response.priorResponse
        while (prior != null) {
            result++
            prior = prior.priorResponse
        }
        return result
    }
}