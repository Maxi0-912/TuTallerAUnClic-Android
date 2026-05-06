package com.manuel.tutalleraunclic.data.network

import com.manuel.tutalleraunclic.data.model.entity.*
import com.manuel.tutalleraunclic.data.model.request.*
import retrofit2.Response
import retrofit2.http.*
import com.manuel.tutalleraunclic.data.model.response.CitaResponse
import com.manuel.tutalleraunclic.data.model.response.LoginResponse

interface ApiService {

    // ==========================
    // AUTH
    // ==========================

    @POST("usuarios/login/")
    suspend fun login(
        @Body request: LoginRequest
    ): Response<LoginResponse>

    @POST("usuarios/register/")
    suspend fun register(
        @Body request: RegisterRequest
    ): Response<Usuario>

    @GET("usuarios/perfil/")
    suspend fun getPerfil(): Response<Usuario>

    @PATCH("usuarios/perfil/update/")
    suspend fun actualizarPerfil(
        @Body data: UpdateUserRequest
    ): Response<Usuario>

    @DELETE("auth/eliminar/")
    suspend fun eliminarCuenta(): Response<Unit>

    @Multipart
    @PATCH("usuarios/perfil/update/")
    suspend fun actualizarPerfilConFoto(
        @Part("username")   username:  okhttp3.RequestBody?,
        @Part("first_name") firstName: okhttp3.RequestBody?,
        @Part("last_name")  lastName:  okhttp3.RequestBody?,
        @Part("email")      email:     okhttp3.RequestBody?,
        @Part("telefono")   telefono:  okhttp3.RequestBody?,
        @Part foto: okhttp3.MultipartBody.Part?
    ): Response<Usuario>

    // ==========================
    // ESTABLECIMIENTOS
    // ==========================

    @GET("establecimientos/")
    suspend fun getEstablecimientos(): Response<List<Establecimiento>>

    @GET("establecimientos/{id}/")
    suspend fun getDetalleEstablecimiento(
        @Path("id") id: Int
    ): Response<Establecimiento>

    @GET("establecimientos/{id}/resenas/")
    suspend fun getResenasEstablecimiento(
        @Path("id") id: Int
    ): Response<List<Calificacion>>

    @GET("establecimientos/{id}/citas-ocupadas/")
    suspend fun getCitasOcupadas(
        @Path("id") establecimientoId: Int,
        @Query("fecha") fecha: String
    ): Response<List<String>>

    @POST("establecimientos/crear/")
    suspend fun crearEstablecimiento(
        @Body request: EstablecimientoRequest
    ): Response<Establecimiento>

    // ==========================
    // SERVICIOS
    // ==========================

    @GET("servicios/establecimiento/{id}/")
    suspend fun getServicios(
        @Path("id") establecimientoId: Int
    ): Response<List<Servicio>>

    // ==========================
    // VEHICULOS
    // ==========================

    @GET("usuarios/mis-vehiculos/")
    suspend fun misVehiculos(): Response<List<Vehiculo>>

    @POST("usuarios/mis-vehiculos/crear/")
    suspend fun crearVehiculo(
        @Body request: VehiculoRequest
    ): Response<Vehiculo>

    @DELETE("usuarios/mis-vehiculos/{placa}/")
    suspend fun eliminarVehiculo(
        @Path("placa") placa: String
    ): Response<Unit>

    // ==========================
    // CITAS
    // ==========================

    @GET("citas/mis-citas/")
    suspend fun getMisCitas(): Response<List<CitaResponse>>

    @POST("citas/crear/")
    suspend fun crearCita(
        @Body request: CrearCitaRequest
    ): Response<CitaResponse>

    @PATCH("citas/{id}/editar/")
    suspend fun editarCita(
        @Path("id") id: Int,
        @Body request: ActualizarCitaRequest
    ): Response<CitaResponse>

    @DELETE("citas/{id}/")
    suspend fun eliminarCita(
        @Path("id") id: Int
    ): Response<Unit>

    @GET("citas/{id}/")
    suspend fun getCita(
        @Path("id") id: Int
    ): Response<CitaResponse>

    // ==========================
    // CALIFICACIONES
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
    // NOTIFICACIONES
    // ==========================

    @GET("notificaciones/")
    suspend fun misNotificaciones(): Response<List<Notificacion>>

    @PATCH("notificaciones/{id}/leida/")
    suspend fun marcarNotificacionLeida(
        @Path("id") id: Int
    ): Response<Unit>

    // ==========================
    // DASHBOARD
    // ==========================

    @GET("empresa/dashboard/")
    suspend fun dashboardEmpresa(): Response<Dashboard>
}
