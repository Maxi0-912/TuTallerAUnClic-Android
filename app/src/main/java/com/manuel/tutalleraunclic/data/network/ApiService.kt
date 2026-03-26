package com.manuel.tutalleraunclic.data.network

import com.manuel.tutalleraunclic.data.model.entity.*
import com.manuel.tutalleraunclic.data.model.request.*
import com.manuel.tutalleraunclic.data.model.response.LoginResponse
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    // ==========================
    // 🔐 AUTH
    // ==========================

    @POST("auth/login/")
    suspend fun login(
        @Body request: LoginRequest
    ): Response<LoginResponse>

    @POST("auth/register/")
    suspend fun register(
        @Body request: RegisterRequest
    ): Response<Usuario>

    @GET("auth/perfil/")
    suspend fun getPerfil(): Response<Usuario>

    @PUT("auth/perfil/")
    suspend fun actualizarPerfil(
        @Body data: UpdateUserRequest
    ): Response<Usuario>

    @DELETE("auth/eliminar/")
    suspend fun eliminarCuenta(): Response<Unit>

    // ==========================
    // 🏢 ESTABLECIMIENTOS + GEO
    // ==========================
    @GET("establecimientos/{id}/")
    suspend fun getDetalleEstablecimiento(
        @Path("id") id: Int
    ): Response<Establecimiento>



    @GET("recomendados/")
    suspend fun getRecomendados(
        @Query("lat") lat: String,
        @Query("lng") lng: String
    ): Response<List<Establecimiento>>

    @GET("establecimientos/")
    suspend fun getEstablecimientos(
        @Query("lat") lat: Double,
        @Query("lng") lng: Double
    ): Response<List<Establecimiento>>

    @POST("establecimientos/")
    suspend fun crearEstablecimiento(
        @Body request: EstablecimientoRequest
    ): Response<Establecimiento>

    // ==========================
    // 🛠 SERVICIOS
    // ==========================

    @GET("servicios/establecimiento/{id}/")
    suspend fun getServicios(
        @Path("id") establecimientoId: Int
    ): Response<List<Servicio>>

    // ==========================
    // 🚗 VEHÍCULOS
    // ==========================

    @GET("vehiculos/")
    suspend fun misVehiculos(): Response<List<Vehiculo>>

    @POST("vehiculos/crear/")
    suspend fun crearVehiculo(
        @Body request: VehiculoRequest
    ): Response<Vehiculo>

    // ==========================
    // 📅 CITAS
    // ==========================

    @POST("citas/")
    suspend fun crearCita(
        @Body request: CrearCitaRequest
    ): Response<Cita>

    @GET("citas/mis/")
    suspend fun misCitas(): Response<List<Cita>>

    @GET("citas/empresa/")
    suspend fun citasEmpresa(): Response<List<Cita>>

    @PATCH("citas/{id}/estado/")
    suspend fun cambiarEstadoCita(
        @Path("id") id: Int,
        @Body request: EstadoRequest
    ): Response<Unit>

    // ==========================
    // ⭐ CALIFICACIONES
    // ==========================

    @POST("calificaciones/crear/")
    suspend fun crearCalificacion(
        @Body request: CalificacionRequest
    ): Response<Unit>

    @GET("calificaciones/establecimiento/{id}/")
    suspend fun calificacionesEstablecimiento(
        @Path("id") id: Int
    ): Response<List<Calificacion>>

    // ==========================
    // 🔔 NOTIFICACIONES
    // ==========================

    @GET("notificaciones/")
    suspend fun misNotificaciones(): Response<List<Notificacion>>

    @PATCH("notificaciones/{id}/leida/")
    suspend fun marcarNotificacionLeida(
        @Path("id") id: Int
    ): Response<Unit>

    // ==========================
    // 📊 DASHBOARD
    // ==========================

    @GET("dashboard/empresa/")
    suspend fun dashboardEmpresa(): Response<Dashboard>
}