package com.manuel.tutalleraunclic.data.remote

import retrofit2.http.*
import retrofit2.Response
import com.manuel.tutalleraunclic.data.model.LoginRequest
import com.manuel.tutalleraunclic.data.model.LoginResponse
import com.manuel.tutalleraunclic.data.model.Vehiculo
import com.manuel.tutalleraunclic.data.model.Establecimiento
import com.manuel.tutalleraunclic.data.model.CrearCitaRequest

interface ApiService {

    @POST("auth/login/")
    suspend fun login(
        @Body request: LoginRequest
    ): Response<LoginResponse>

    @GET("establecimientos/")
    suspend fun getEstablecimientos(
        @Header("Authorization") token: String
    ): List<Establecimiento>

    @GET("vehiculos/")
    suspend fun misVehiculos(
        @Header("Authorization") token: String
    ): List<Vehiculo>

    @POST("citas/crear/")
    suspend fun crearCita(
        @Header("Authorization") token: String,
        @Body request: CrearCitaRequest
    ): Response<Unit>
}