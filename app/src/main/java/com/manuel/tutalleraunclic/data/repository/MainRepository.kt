package com.manuel.tutalleraunclic.data.repository

import android.util.Log
import com.google.firebase.messaging.FirebaseMessaging
import com.manuel.tutalleraunclic.data.local.TokenManager
import com.manuel.tutalleraunclic.data.model.entity.Calificacion
import com.manuel.tutalleraunclic.data.model.entity.Establecimiento
import com.manuel.tutalleraunclic.data.model.entity.Notificacion
import com.manuel.tutalleraunclic.data.model.entity.Usuario
import com.manuel.tutalleraunclic.data.model.request.*
import com.manuel.tutalleraunclic.data.model.response.DashboardEmpresaResponse
import com.manuel.tutalleraunclic.data.model.response.LoginResponse
import com.manuel.tutalleraunclic.data.network.ApiService
import kotlinx.coroutines.suspendCancellableCoroutine
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Response
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import com.manuel.tutalleraunclic.data.model.entity.Servicio

class MainRepository @Inject constructor(
    private val api: ApiService,
    private val tokenManager: TokenManager
) {

    // ==========================
    // 🔐 AUTH
    // ==========================

    suspend fun login(request: LoginRequest): Result<LoginResponse> {
        return try {
            val response = api.login(request)

            if (response.isSuccessful) {
                val body = response.body()!!
                val access = body.access
                val refresh = body.refresh
                if (access == null || refresh == null) {
                    return Result.failure(Exception("Respuesta de login inválida"))
                }

                // Tokens primero — AuthInterceptor los necesita para llamar a /perfil/
                tokenManager.saveTokens(access, refresh)

                // El backend solo devuelve access y refresh, sin rol.
                // Llamamos a /perfil/ para obtener el rol real del usuario.
                val rolFinal: String = try {
                    val perfilResponse = api.getPerfil()
                    if (perfilResponse.isSuccessful) {
                        perfilResponse.body()?.rol_nombre?.lowercase()?.trim()?.takeIf { it.isNotBlank() } ?: "cliente"
                    } else {
                        "cliente"
                    }
                } catch (e: Exception) {
                    "cliente"
                }

                tokenManager.saveRolNombre(rolFinal)

                if (rolFinal == "empresa") {
                    tokenManager.savePendingMessage("Modo empresa aún no disponible en la app")
                }

                // Devolvemos un copy con rolNombreDirecto relleno para que
                // LoginViewModel lea loginResponse.rolNombre correctamente.
                Result.success(body.copy(rolNombreDirecto = rolFinal))
            } else {
                val errorBody = response.errorBody()?.string()
                val mensaje = parseDjangoError(errorBody) ?: "Credenciales incorrectas"
                Result.failure(Exception(mensaje))
            }

        } catch (e: Exception) {
            Result.failure(Exception("Error de conexión"))
        }
    }

    /**
     * Mismo endpoint para las dos llamadas del flujo Google:
     * [rol] null → si el email no existe, el backend responde requiere_rol=true
     * sin crear la cuenta ni emitir tokens. [rol] no-null → reenvía el mismo
     * idToken junto con el rol elegido; ahí sí crea la cuenta y devuelve JWT.
     */
    suspend fun loginWithGoogle(idToken: String, rol: Int? = null): Result<LoginResponse> {
        return try {
            val response = api.loginWithGoogle(GoogleAuthRequest(idToken, rol))
            if (response.isSuccessful) {
                val body = response.body()!!

                if (body.requiereRol || body.access == null || body.refresh == null) {
                    // Todavía no hay sesión: no guardar nada, dejar que el ViewModel
                    // muestre el selector de rol (o el error si esto no era esperado).
                    return Result.success(body)
                }

                tokenManager.saveTokens(body.access, body.refresh)
                val rolFinal: String = try {
                    val perfilResponse = api.getPerfil()
                    if (perfilResponse.isSuccessful)
                        perfilResponse.body()?.rol_nombre?.lowercase()?.trim().orEmpty()
                    else ""
                } catch (e: Exception) { "" }
                tokenManager.saveRolNombre(rolFinal)
                Result.success(body.copy(rolNombreDirecto = rolFinal))
            } else {
                val errorBody = response.errorBody()?.string()
                Result.failure(Exception(parseDjangoError(errorBody) ?: "Error al iniciar con Google"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Error de conexión"))
        }
    }

    suspend fun register(request: RegisterRequest): Result<Usuario> {
        return try {
            val response = api.register(request)
            if (response.isSuccessful) {
                Result.success(response.body()!!)
            } else {
                val errorBody = response.errorBody()?.string()
                val mensaje = parseDjangoError(errorBody) ?: "Error ${response.code()}"
                Result.failure(Exception(mensaje))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Error de conexión"))
        }
    }

    /**
     * Django devuelve los errores como JSON con una de estas formas:
     *   {"username": ["A user with that username already exists."]}
     *   {"password": ["This password is too common."]}
     *   {"detail": "..."}
     *   {"non_field_errors": ["..."]}
     * Extrae el primer mensaje legible para mostrarlo al usuario.
     */
    private fun parseDjangoError(errorBody: String?): String? {
        if (errorBody.isNullOrBlank()) return null
        return try {
            val json = org.json.JSONObject(errorBody)
            val firstKey = json.keys().next()
            val value = json.get(firstKey)
            when (value) {
                is org.json.JSONArray -> value.getString(0)
                else -> value.toString()
            }
        } catch (e: Exception) {
            errorBody.take(200)
        }
    }


    suspend fun logout() {
        // Antes de limpiar: desactivar el token FCM requiere el JWT todavía vigente.
        desactivarTokenFcm()
        tokenManager.clearAll()
    }

    // ==========================
    // 🔔 PUSH (FCM)
    // ==========================

    private suspend fun obtenerTokenFcm(): String = suspendCancellableCoroutine { cont ->
        FirebaseMessaging.getInstance().token
            .addOnSuccessListener { cont.resume(it) }
            .addOnFailureListener { cont.resumeWithException(it) }
    }

    /**
     * Registra el token FCM del dispositivo contra el backend. [token] se pasa
     * explícito desde MyFirebaseService.onNewToken (ya lo tiene, no hace falta
     * volver a pedírselo a Firebase); en el resto de los casos (login, apertura
     * con sesión activa) se obtiene acá. Idempotente en el backend — se puede
     * llamar todas las veces que haga falta. Nunca propaga: solo logea.
     */
    suspend fun registrarTokenFcm(token: String? = null) {
        try {
            val fcmToken = token ?: obtenerTokenFcm()
            val response = api.registrarDispositivo(RegistrarDispositivoRequest(fcmToken))
            Log.d("FCM", "Registro de token: ${response.code()}")
        } catch (e: Exception) {
            Log.e("FCM", "No se pudo registrar el token FCM", e)
        }
    }

    suspend fun desactivarTokenFcm() {
        try {
            val fcmToken = obtenerTokenFcm()
            val response = api.desactivarDispositivo(DesactivarDispositivoRequest(fcmToken))
            Log.d("FCM", "Desactivación de token: ${response.code()}")
        } catch (e: Exception) {
            Log.e("FCM", "No se pudo desactivar el token FCM", e)
        }
    }







    // ==========================
    // 👤 PERFIL
    // ==========================

    suspend fun getPerfil(): Result<Usuario> {
        return try {
            val response = api.getPerfil()

            if (response.isSuccessful) {
                val body = response.body()

                if (body != null) {
                    Result.success(body)
                } else {
                    Result.failure(Exception("El backend respondió vacío"))
                }

            } else {
                Result.failure(
                    Exception("Error ${response.code()} - ${response.errorBody()?.string()}")
                )
            }

        } catch (e: Exception) {
            Result.failure(e) // 👈 muestra el error real
        }
    }

    suspend fun actualizarPerfil(data: UpdateUserRequest): Result<Usuario> {
        return try {
            val response = api.actualizarPerfil(data)
            if (response.isSuccessful) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Error ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Error de conexión"))
        }
    }

    suspend fun actualizarPerfilConFoto(
        data: UpdateUserRequest,
        fotoPart: MultipartBody.Part?
    ): Result<Usuario> {
        return try {
            fun String.asTextBody(): RequestBody =
                toRequestBody("text/plain".toMediaType())

            val response = api.actualizarPerfilConFoto(
                username  = data.username?.asTextBody(),
                firstName = data.first_name?.asTextBody(),
                lastName  = data.last_name?.asTextBody(),
                email     = data.email?.asTextBody(),
                telefono  = data.telefono?.asTextBody(),
                foto      = fotoPart
            )
            if (response.isSuccessful) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Error ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Error de conexión"))
        }
    }

    suspend fun eliminarCuenta(): Result<Unit> {
        return try {
            val response = api.eliminarCuenta()
            android.util.Log.d("ELIMINAR_CUENTA", "URL: ${response.raw().request.url}")
            android.util.Log.d("ELIMINAR_CUENTA", "Método: ${response.raw().request.method}")
            android.util.Log.d("ELIMINAR_CUENTA", "Auth header presente: ${response.raw().request.header("Authorization") != null}")
            if (response.isSuccessful) {
                tokenManager.clearAll()          // limpia tokens tras borrar la cuenta
                Result.success(Unit)
            } else {
                val errorBody = response.errorBody()?.string()
                android.util.Log.e("ELIMINAR_CUENTA", "HTTP ${response.code()} → $errorBody")
                val msg = parseDjangoError(errorBody) ?: "Error ${response.code()}"
                Result.failure(Exception(msg))
            }
        } catch (e: Exception) {
            android.util.Log.e("ELIMINAR_CUENTA", "Excepción: ${e.message}")
            Result.failure(Exception("Error de conexión: ${e.message}"))
        }
    }

    suspend fun getServicios(establecimientoId: Int): List<Servicio> {
        val response = api.getServicios(establecimientoId)
        if (response.isSuccessful) {
            return response.body() ?: emptyList()
        } else {
            throw Exception("Error al obtener servicios")
        }
    }

    // ==========================
    // 🏢 ESTABLECIMIENTOS
    // ==========================

    suspend fun getDetalleEstablecimiento(id: Int): Result<Establecimiento> {
        return try {
            val response = api.getDetalleEstablecimiento(id)
            if (response.isSuccessful) Result.success(response.body()!!)
            else Result.failure(Exception("Error ${response.code()}"))
        } catch (e: Exception) {
            Result.failure(Exception("Error de conexión"))
        }
    }

    suspend fun getResenasEstablecimiento(id: Int): Result<List<Calificacion>> {
        return try {
            val response = api.getResenasEstablecimiento(id)
            if (response.isSuccessful) Result.success(response.body() ?: emptyList())
            else Result.failure(Exception("Error ${response.code()}"))
        } catch (e: Exception) {
            Result.failure(Exception("Error de conexión"))
        }
    }

    suspend fun getEstablecimientos(): Result<List<Establecimiento>> {
        return try {
            val response = api.getEstablecimientos()
            if (response.isSuccessful) {
                Result.success(response.body() ?: emptyList())
            } else {
                Result.failure(Exception("Error ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Error de conexión"))
        }
    }

    // ==========================
    // 🔔 NOTIFICACIONES
    // ==========================

    suspend fun misNotificaciones(): Result<List<Notificacion>> {
        return try {
            val response = api.misNotificaciones()
            if (response.isSuccessful) {
                Result.success(response.body() ?: emptyList())
            } else {
                Result.failure(Exception("Error ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Error de conexión"))
        }
    }

    suspend fun marcarNotificacionLeida(id: Int): Result<Unit> {
        return try {
            val response = api.marcarNotificacionLeida(id)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Error ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Error de conexión"))
        }
    }

    // ==========================
    // ⭐ CALIFICACIONES
    // ==========================

    suspend fun crearCalificacion(request: CalificacionRequest): Result<Unit> {
        return try {
            val response = api.crearCalificacion(request)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Error ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Error de conexión"))
        }
    }

    // ==========================
    // 📊 DASHBOARD EMPRESA
    // ==========================

    suspend fun getDashboardEmpresa(): Result<DashboardEmpresaResponse> {
        return try {
            val response = api.dashboardEmpresa()
            if (response.isSuccessful) {
                response.body()?.let { Result.success(it) }
                    ?: Result.failure(Exception("El backend respondió vacío"))
            } else {
                Result.failure(Exception("Error ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Error de conexión"))
        }
    }
}