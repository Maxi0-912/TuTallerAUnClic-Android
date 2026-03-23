
package com.manuel.tutalleraunclic.data.repository

import com.manuel.tutalleraunclic.data.network.ApiService
import com.manuel.tutalleraunclic.data.model.entity.Cita
import com.manuel.tutalleraunclic.data.model.entity.Establecimiento
import com.manuel.tutalleraunclic.data.model.entity.Servicio
import com.manuel.tutalleraunclic.data.model.request.CalificacionRequest
import com.manuel.tutalleraunclic.data.model.request.CitaRequest
import com.manuel.tutalleraunclic.data.model.request.LoginRequest
import com.manuel.tutalleraunclic.data.model.request.RegisterRequest
import com.manuel.tutalleraunclic.data.model.response.LoginResponse
import retrofit2.Response

class MainRepository(private val apiService: ApiService) {

    // ==========================
    // 🔐 AUTH
    // ==========================

    suspend fun login(request: LoginRequest): Response<LoginResponse> {
        return apiService.login(request)
    }

    suspend fun register(request: RegisterRequest): Response<Void> {
        return apiService.register(request)
    }

    // ==========================
    // 🏢 ESTABLECIMIENTOS
    // ==========================

    suspend fun getEstablecimientos(): Response<List<Establecimiento>> {
        return apiService.getEstablecimientos()
    }

    suspend fun getServicios(establecimientoId: Int): Response<List<Servicio>> {
        return apiService.getServicios(establecimientoId)
    }

    // ==========================
    // 📅 CITAS
    // ==========================

    suspend fun crearCita(
        token: String,
        request: CitaRequest
    ): Response<Void> {

        return apiService.crearCita(
            token = "Bearer $token",
            cita = request
        )
    }

    suspend fun misCitas(token: String): Response<List<Cita>> {
        return apiService.misCitas("Bearer $token")
    }

    // ==========================
    // ⭐ CALIFICACIONES
    // ==========================

    suspend fun crearCalificacion(
        token: String,
        request: CalificacionRequest
    ): Response<Void> {

        return apiService.crearCalificacion(
            token = "Bearer $token",
            calificacion = request
        )
    }
}