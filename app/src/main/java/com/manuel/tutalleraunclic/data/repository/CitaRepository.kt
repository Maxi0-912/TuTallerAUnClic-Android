package com.manuel.tutalleraunclic.data.repository

import com.manuel.tutalleraunclic.data.network.ApiService
import javax.inject.Inject
import  com.manuel.tutalleraunclic.data.model.request.CrearCitaRequest
import  com.manuel.tutalleraunclic.data.model.response.CitaResponse
class CitaRepository @Inject constructor(
    private val api: ApiService
) {
    suspend fun crearCita(request: CrearCitaRequest) {
        api.crearCita(request)
    }

    suspend fun eliminarCita(id: Int): Result<Unit> {
        return try {
            val response = api.eliminarCita(id)

            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Error ${response.code()}"))
            }

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun editarCita(id: Int, request: CrearCitaRequest) {
        api.editarCita(id, request)
    }

    suspend fun getMisCitas(): List<CitaResponse> {
        return api.getMisCitas()
    }


    suspend fun getHorarios(
        establecimientoId: Int,
        fecha: String
    ): List<String> {
        return api.getHorariosDisponibles(establecimientoId, fecha)
    }
}