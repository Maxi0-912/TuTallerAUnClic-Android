package com.manuel.tutalleraunclic.data.network

import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Route
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Singleton
import com.manuel.tutalleraunclic.data.local.TokenManager

@Singleton
class TokenAuthenticator @Inject constructor(
    private val tokenManager: TokenManager,
    private val authApiService: AuthApiService
) : Authenticator {

    // 🔥 Control de concurrencia
    private val lock = Any()

    override fun authenticate(route: Route?, response: Response): Request? {

        // 🚫 Evitar loops infinitos
        if (responseCount(response) >= 2) return null

        synchronized(lock) {

            val currentToken = tokenManager.getAccessToken()
            val requestToken = response.request.header("Authorization")

            // 🔥 Si otro hilo ya refrescó el token, reutilízalo
            if (requestToken != null && currentToken != null &&
                requestToken != "Bearer $currentToken"
            ) {
                return response.request.newBuilder()
                    .header("Authorization", "Bearer $currentToken")
                    .build()
            }

            val refreshToken = tokenManager.getRefreshToken()
                ?: return logout()

            return try {

                val refreshResponse = authApiService.refreshToken(
                    mapOf("refresh" to refreshToken)
                ).execute()

                if (!refreshResponse.isSuccessful) {
                    return logout()
                }

                val body = refreshResponse.body() ?: return logout()

                val newAccess = body.access ?: return logout()

// ⚠️ Django normalmente NO devuelve refresh aquí
                val newRefresh = refreshToken

                tokenManager.saveTokens(newAccess, newRefresh)

                // 🔥 Reintentar request original
                response.request.newBuilder()
                    .header("Authorization", "Bearer $newAccess")
                    .build()

            } catch (e: Exception) {
                e.printStackTrace()
                logout()
            }
        }
    }

    private fun logout(): Request? {
        tokenManager.clearAll()
        return null
    }

    private fun responseCount(response: Response): Int {
        var count = 1
        var res = response.priorResponse
        while (res != null) {
            count++
            res = res.priorResponse
        }
        return count
    }
}