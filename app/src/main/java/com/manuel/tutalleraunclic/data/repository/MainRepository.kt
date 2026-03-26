package com.manuel.tutalleraunclic.data.repository

import com.manuel.tutalleraunclic.data.network.ApiService
import com.manuel.tutalleraunclic.data.model.entity.*
import com.manuel.tutalleraunclic.data.model.request.*
import com.manuel.tutalleraunclic.data.model.response.LoginResponse

import javax.inject.Inject
import retrofit2.Response

class MainRepository @Inject constructor(
    private val api: ApiService
) {

    suspend fun obtenerServicios(establecimientoId: Int) =
        api.getServicios(establecimientoId)



    // ==========================
    // 🔐 AUTH
    // ==========================

    suspend fun login(request: LoginRequest): Response<LoginResponse> {
        return api.login(request)
    }

    suspend fun register(request: RegisterRequest): Response<Usuario> {
        return api.register(request)
    }

    suspend fun getPerfil(): Response<Usuario> {
        return api.getPerfil()
    }

    suspend fun actualizarPerfil(
        data: UpdateUserRequest
    ): Response<Usuario> {
        return api.actualizarPerfil(data)
    }

    suspend fun eliminarCuenta(): Response<Unit> {
        return api.eliminarCuenta()
    }

    // ==========================
    // 🏢 ESTABLECIMIENTOS
    // ==========================

    suspend fun getEstablecimientos(
        lat: Double,
        lng: Double
    ): Response<List<Establecimiento>> {
        return api.getEstablecimientos(lat, lng)
    }

    suspend fun getRecomendados(
        lat: Double,
        lng: Double
    ): Response<List<Establecimiento>> {
        return api.getRecomendados(
            lat = lat.toString(),
            lng = lng.toString()
        )
    }

    suspend fun getDetalleEstablecimiento(id: Int): Response<Establecimiento> {
        return api.getDetalleEstablecimiento(id)
    }

    suspend fun crearEstablecimiento(
        request: EstablecimientoRequest
    ): Response<Establecimiento> {
        return api.crearEstablecimiento(request)
    }

    // ==========================
    // 🛠 SERVICIOS
    // ==========================

    suspend fun getServicios(establecimientoId: Int): Response<List<Servicio>> {
        return api.getServicios(establecimientoId)
    }

    // ==========================
    // 🚗 VEHÍCULOS
    // ==========================

    suspend fun misVehiculos(): Response<List<Vehiculo>> {
        return api.misVehiculos()
    }

    suspend fun crearVehiculo(
        request: VehiculoRequest
    ): Response<Vehiculo> {
        return api.crearVehiculo(request)
    }

    // ==========================
    // 📅 CITAS
    // ==========================

    suspend fun crearCita(
        request: CrearCitaRequest
    ): Response<Cita> {
        return api.crearCita(request)
    }

    suspend fun misCitas(): Response<List<Cita>> {
        return api.misCitas()
    }

    suspend fun citasEmpresa(): Response<List<Cita>> {
        return api.citasEmpresa()
    }

    suspend fun cambiarEstadoCita(
        id: Int,
        request: EstadoRequest
    ): Response<Unit> {
        return api.cambiarEstadoCita(id, request)
    }

    // ==========================
    // ⭐ CALIFICACIONES
    // ==========================

    suspend fun crearCalificacion(
        request: CalificacionRequest
    ): Response<Unit> {
        return api.crearCalificacion(request)
    }

    suspend fun calificacionesEstablecimiento(id: Int): Response<List<Calificacion>> {
        return api.calificacionesEstablecimiento(id)
    }

    // ==========================
    // 🔔 NOTIFICACIONES
    // ==========================

    suspend fun misNotificaciones(): Response<List<Notificacion>> {
        return api.misNotificaciones()
    }

    suspend fun marcarNotificacionLeida(id: Int): Response<Unit> {
        return api.marcarNotificacionLeida(id)
    }

    // ==========================
    // 📊 DASHBOARD
    // ==========================

    suspend fun dashboardEmpresa(): Response<Dashboard> {
        return api.dashboardEmpresa()
    }
}