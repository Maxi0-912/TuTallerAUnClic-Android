package com.manuel.tutalleraunclic.data.repository

import com.manuel.tutalleraunclic.data.model.entity.TipoEstablecimiento
import com.manuel.tutalleraunclic.data.model.response.EstablecimientoEmpresaResponse
import com.manuel.tutalleraunclic.data.network.ApiService
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Response
import javax.inject.Inject

class EstablecimientoEmpresaRepository @Inject constructor(
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

    private fun String?.asTextBody(): RequestBody? =
        this?.toRequestBody("text/plain".toMediaType())

    suspend fun getMisEstablecimientos(): Result<List<EstablecimientoEmpresaResponse>> =
        safeApiCall { api.getMisEstablecimientos() }

    suspend fun getTipos(): Result<List<TipoEstablecimiento>> =
        safeApiCall { api.getTiposEstablecimiento() }

    suspend fun crear(
        nombre: String,
        direccion: String?,
        telefono: String?,
        descripcion: String?,
        horaApertura: String?,
        horaCierre: String?,
        latitud: String,
        longitud: String,
        tipoId: Int,
        foto: MultipartBody.Part?
    ): Result<EstablecimientoEmpresaResponse> = safeApiCall {
        api.crearEstablecimientoEmpresa(
            nombre = nombre.toRequestBody("text/plain".toMediaType()),
            direccion = direccion.asTextBody(),
            telefono = telefono.asTextBody(),
            descripcion = descripcion.asTextBody(),
            horaApertura = horaApertura.asTextBody(),
            horaCierre = horaCierre.asTextBody(),
            latitud = latitud.toRequestBody("text/plain".toMediaType()),
            longitud = longitud.toRequestBody("text/plain".toMediaType()),
            tipo = tipoId.toString().toRequestBody("text/plain".toMediaType()),
            foto = foto
        )
    }

    suspend fun editar(
        id: Int,
        nombre: String?,
        direccion: String?,
        telefono: String?,
        descripcion: String?,
        horaApertura: String?,
        horaCierre: String?,
        latitud: String?,
        longitud: String?,
        tipoId: Int?,
        foto: MultipartBody.Part?
    ): Result<EstablecimientoEmpresaResponse> = safeApiCall {
        api.editarEstablecimientoEmpresa(
            id = id,
            nombre = nombre.asTextBody(),
            direccion = direccion.asTextBody(),
            telefono = telefono.asTextBody(),
            descripcion = descripcion.asTextBody(),
            horaApertura = horaApertura.asTextBody(),
            horaCierre = horaCierre.asTextBody(),
            latitud = latitud.asTextBody(),
            longitud = longitud.asTextBody(),
            tipo = tipoId?.toString().asTextBody(),
            foto = foto
        )
    }

    suspend fun eliminar(id: Int): Result<Unit> {
        return try {
            val response = api.eliminarEstablecimientoEmpresa(id)
            if (response.isSuccessful) Result.success(Unit)
            else Result.failure(Exception("Error ${response.code()}"))
        } catch (e: Exception) {
            Result.failure(Exception("Error de conexión: ${e.message}"))
        }
    }
}
