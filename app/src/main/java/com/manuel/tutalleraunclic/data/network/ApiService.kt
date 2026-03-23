package com.manuel.tutalleraunclic.data.network

import com.manuel.tutalleraunclic.data.model.entity.Calificacion
import com.manuel.tutalleraunclic.data.model.request.CalificacionRequest
import com.manuel.tutalleraunclic.data.model.entity.Cita
import com.manuel.tutalleraunclic.data.model.request.CitaRequest
import com.manuel.tutalleraunclic.data.model.request.CrearCitaRequest
import com.manuel.tutalleraunclic.data.model.entity.Dashboard
import com.manuel.tutalleraunclic.data.model.entity.Establecimiento
import com.manuel.tutalleraunclic.data.model.request.EstadoRequest
import com.manuel.tutalleraunclic.data.model.request.LoginRequest
import com.manuel.tutalleraunclic.data.model.response.LoginResponse
import com.manuel.tutalleraunclic.data.model.entity.Notificacion
import com.manuel.tutalleraunclic.data.model.request.RegisterRequest
import com.manuel.tutalleraunclic.data.model.entity.Servicio
import com.manuel.tutalleraunclic.data.model.entity.Usuario
import com.manuel.tutalleraunclic.data.model.entity.Vehiculo
import com.manuel.tutalleraunclic.data.model.request.VehiculoRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiService {

    // ==========================
    // 🔐 AUTH
    // ==========================

    @POST("usuarios/login/")
    suspend fun login(
        @Body request: LoginRequest
    ): Response<LoginResponse>


    @POST("usuarios/register/")
    suspend fun register(
        @Body request: RegisterRequest
    ): Response<Void>


    @GET("usuarios/perfil/")
    suspend fun perfil(
        @Header("Authorization") token: String
    ): Response<Usuario>


    // ==========================
    // 🏢 ESTABLECIMIENTOS
    // ==========================

    @GET("establecimientos/")
    suspend fun getEstablecimientos():
            Response<List<Establecimiento>>


    @GET("establecimientos/{id}/")
    suspend fun getDetalleEstablecimiento(

        @Path("id") establecimientoId: Int

    ): Response<Establecimiento>


    // ==========================
    // 🛠 SERVICIOS
    // ==========================

    @GET("servicios/establecimiento/{id}/")
    suspend fun getServicios(

        @Path("id") establecimientoId: Int

    ): Response<List<Servicio>>


    // ==========================
    // 🚗 VEHICULOS
    // ==========================

    @GET("usuarios/vehiculos/")
    suspend fun misVehiculos(

        @Header("Authorization") token: String

    ): Response<List<Vehiculo>>


    @POST("usuarios/vehiculos/crear/")
    suspend fun crearVehiculo(

        @Header("Authorization") token: String,
        @Body vehiculo: VehiculoRequest

    ): Response<Void>


    // ==========================
    // 📅 CITAS
    // ==========================

    @POST("citas/crear/")
    suspend fun crearCita(

        @Header("Authorization") token: String,
        @Body cita: CitaRequest

    ): Response<Void>




    @GET("citas/mis/")
    suspend fun misCitas(
        @Header("Authorization") token: String
    ): Response<List<Cita>>

    @POST("citas/crear/")
    suspend fun crearCita(
        @Body cita: CrearCitaRequest
    ): Response<Cita>


    @GET("citas/empresa/{establecimiento_id}/")
    suspend fun citasEmpresa(

        @Header("Authorization") token: String,
        @Path("establecimiento_id") establecimientoId: Int

    ): Response<List<Cita>>


    @PATCH("citas/{id}/estado/")
    suspend fun cambiarEstadoCita(

        @Header("Authorization") token: String,
        @Path("id") citaId: Int,
        @Body estado: EstadoRequest

    ): Response<Void>


    // ==========================
    // ⭐ CALIFICACIONES
    // ==========================

    @POST("calificaciones/crear/")
    suspend fun crearCalificacion(

        @Header("Authorization") token: String,
        @Body calificacion: CalificacionRequest

    ): Response<Void>


    @GET("calificaciones/establecimiento/{id}/")
    suspend fun calificacionesEstablecimiento(

        @Path("id") establecimientoId: Int

    ): Response<List<Calificacion>>


    // ==========================
    // 🔔 NOTIFICACIONES
    // ==========================

    @GET("notificaciones/")
    suspend fun misNotificaciones(

        @Header("Authorization") token: String

    ): Response<List<Notificacion>>


    @PATCH("notificaciones/{id}/leida/")
    suspend fun marcarNotificacionLeida(

        @Header("Authorization") token: String,
        @Path("id") notificacionId: Int

    ): Response<Void>


    // ==========================
    // 📊 DASHBOARD EMPRESA
    // ==========================

    @GET("citas/dashboard/{establecimiento_id}/")
    suspend fun dashboardEmpresa(

        @Header("Authorization") token: String,
        @Path("establecimiento_id") establecimientoId: Int

    ): Response<Dashboard>

}