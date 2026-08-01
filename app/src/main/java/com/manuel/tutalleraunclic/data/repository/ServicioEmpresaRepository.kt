package com.manuel.tutalleraunclic.data.repository

import com.manuel.tutalleraunclic.data.model.entity.TipoServicio
import com.manuel.tutalleraunclic.data.model.request.ServicioEmpresaRequest
import com.manuel.tutalleraunclic.data.model.request.TipoServicioRequest
import com.manuel.tutalleraunclic.data.model.response.ServicioEmpresaResponse
import com.manuel.tutalleraunclic.data.network.ApiService
import retrofit2.Response
import javax.inject.Inject

class ServicioEmpresaRepository @Inject constructor(
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
                Result.failure(Exception(response.errorBody()?.string() ?: "Error ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Error de conexión: ${e.message}"))
        }
    }

    suspend fun getServicios(): Result<List<ServicioEmpresaResponse>> =
        safeApiCall { api.getServiciosEmpresa() }

    suspend fun getTipos(): Result<List<TipoServicio>> =
        safeApiCall { api.getTiposServicio() }

    suspend fun crearTipo(nombre: String): Result<TipoServicio> =
        safeApiCall { api.crearTipoServicio(TipoServicioRequest(nombre)) }

    suspend fun crear(nombre: String, establecimientoId: Int, tipoServicioId: Int): Result<ServicioEmpresaResponse> =
        safeApiCall { api.crearServicioEmpresa(ServicioEmpresaRequest(nombre, establecimientoId, tipoServicioId)) }

    suspend fun editar(id: Int, nombre: String, establecimientoId: Int, tipoServicioId: Int): Result<ServicioEmpresaResponse> =
        safeApiCall { api.editarServicioEmpresa(id, ServicioEmpresaRequest(nombre, establecimientoId, tipoServicioId)) }

    suspend fun eliminar(id: Int): Result<Unit> {
        return try {
            val response = api.eliminarServicioEmpresa(id)
            if (response.isSuccessful) Result.success(Unit)
            else Result.failure(Exception("Error ${response.code()}"))
        } catch (e: Exception) {
            Result.failure(Exception("Error de conexión: ${e.message}"))
        }
    }
}
