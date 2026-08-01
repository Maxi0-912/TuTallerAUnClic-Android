package com.manuel.tutalleraunclic.data.repository

import com.manuel.tutalleraunclic.data.model.entity.Servicio
import com.manuel.tutalleraunclic.data.model.request.ActualizarCitaRequest
import com.manuel.tutalleraunclic.data.model.request.ComentarioEmpresaRequest
import com.manuel.tutalleraunclic.data.model.request.CrearCitaRequest
import com.manuel.tutalleraunclic.data.model.request.EstadoRequest
import com.manuel.tutalleraunclic.data.model.response.CitaEmpresaResponse
import com.manuel.tutalleraunclic.data.model.response.CitaResponse
import com.manuel.tutalleraunclic.data.network.ApiService
import retrofit2.Response
import javax.inject.Inject

class CitaRepository @Inject constructor(
    private val api: ApiService
) {

    private suspend inline fun <T> safeApiCall(
        crossinline call: suspend () -> Response<T>
    ): Result<T> {
        return try {
            val response = call()
            if (response.isSuccessful) {
                response.body()?.let { Result.success(it) }
                    ?: Result.failure(Exception("Respuesta vacía"))
            } else {
                Result.failure(Exception("Error ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Error de conexión: ${e.message}"))
        }
    }

    suspend fun crearCita(request: CrearCitaRequest): Response<CitaResponse> =
        api.crearCita(request)

    suspend fun eliminarCita(id: Int): Result<Unit> {
        return try {
            val response = api.eliminarCita(id)
            if (response.isSuccessful) Result.success(Unit)
            else Result.failure(Exception("Error ${response.code()}"))
        } catch (e: Exception) {
            Result.failure(Exception("Error de conexión"))
        }
    }

    suspend fun editarCita(id: Int, request: ActualizarCitaRequest): Result<CitaResponse> =
        safeApiCall { api.editarCita(id, request) }

    suspend fun actualizarCita(id: Int, request: ActualizarCitaRequest): Response<CitaResponse> =
        api.editarCita(id, request)

    suspend fun getMisCitas(): Result<List<CitaResponse>> =
        safeApiCall { api.getMisCitas() }

    // Devuelve las horas ya ocupadas para ese establecimiento y fecha
    suspend fun getCitasOcupadas(establecimientoId: Int, fecha: String): Result<List<String>> =
        safeApiCall { api.getCitasOcupadas(establecimientoId, fecha) }

    suspend fun obtenerCitaPorId(id: Int): Result<CitaResponse> =
        safeApiCall { api.getCita(id) }

    suspend fun getServicios(establecimientoId: Int): Result<List<Servicio>> =
        safeApiCall { api.getServicios(establecimientoId) }

    // ── Citas empresa ─────────────────────────────────────────────────────────

    suspend fun getCitasEmpresa(): Result<List<CitaEmpresaResponse>> =
        safeApiCall { api.getCitasEmpresa() }

    suspend fun cambiarEstadoCita(id: Int, estado: String): Result<CitaEmpresaResponse> =
        safeApiCall { api.cambiarEstadoCita(id, EstadoRequest(estado)) }

    suspend fun comentarCita(id: Int, comentario: String): Result<CitaEmpresaResponse> =
        safeApiCall { api.comentarCita(id, ComentarioEmpresaRequest(comentario)) }
}
