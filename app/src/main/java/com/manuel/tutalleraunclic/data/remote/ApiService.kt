package com.manuel.tutalleraunclic.data.remote

import retrofit2.Response
import retrofit2.http.*
import com.manuel.tutalleraunclic.data.model.*

interface ApiService {

    // ======================
    // AUTH
    // ======================

    @POST("usuarios/login/")
    suspend fun login(
        @Body request: LoginRequest
    ): Response<LoginResponse>

    @POST("usuarios/register/")
    suspend fun register(
        @Body request: RegisterRequest
    ): Response<Unit>

    // ======================
    // ESTABLECIMIENTOS
    // ======================

    @GET("establecimientos/")
    suspend fun getEstablecimientos(): Response<List<Establecimiento>>

    // ======================
    // VEHÍCULOS
    // ======================

    @GET("usuarios/vehiculos/")
    suspend fun misVehiculos(): Response<List<Vehiculo>>

    // ======================
    // CITAS
    // ======================

    @POST("citas/crear/")
    suspend fun crearCita(
        @Body request: CrearCitaRequest
    ): Response<Unit>
}