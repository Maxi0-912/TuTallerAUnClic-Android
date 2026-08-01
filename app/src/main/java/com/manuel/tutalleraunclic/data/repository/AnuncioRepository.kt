package com.manuel.tutalleraunclic.data.repository

import com.manuel.tutalleraunclic.data.model.entity.Anuncio
import com.manuel.tutalleraunclic.data.model.response.CupoAnunciosResponse
import com.manuel.tutalleraunclic.data.network.ApiService
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Response
import javax.inject.Inject

class AnuncioRepository @Inject constructor(
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

    suspend fun getAnunciosPublicos(categoria: String? = null): Result<List<Anuncio>> =
        safeApiCall { api.getAnunciosPublicos(categoria) }

    suspend fun getMisAnuncios(): Result<List<Anuncio>> =
        safeApiCall { api.getMisAnuncios() }

    suspend fun getCupo(establecimientoId: Int): Result<CupoAnunciosResponse> =
        safeApiCall { api.getCupoAnuncios(establecimientoId) }

    suspend fun crearAnuncio(
        titulo: String,
        descripcion: String?,
        tipo: String,
        categoria: String,
        descuento: String?,
        textoBoton: String?,
        urlBoton: String?,
        establecimientoId: Int,
        fechaInicio: String?,
        fechaFin: String?,
        imagen: MultipartBody.Part?
    ): Result<Anuncio> = safeApiCall {
        api.crearAnuncio(
            titulo = titulo.toRequestBody("text/plain".toMediaType()),
            descripcion = descripcion.asTextBody(),
            tipo = tipo.toRequestBody("text/plain".toMediaType()),
            categoria = categoria.toRequestBody("text/plain".toMediaType()),
            descuento = descuento.asTextBody(),
            textoBoton = textoBoton.asTextBody(),
            urlBoton = urlBoton.asTextBody(),
            establecimiento = establecimientoId.toString().toRequestBody("text/plain".toMediaType()),
            fechaInicio = fechaInicio.asTextBody(),
            fechaFin = fechaFin.asTextBody(),
            imagen = imagen
        )
    }

    suspend fun editarAnuncio(
        id: Int,
        titulo: String?,
        descripcion: String?,
        tipo: String?,
        categoria: String?,
        descuento: String?,
        textoBoton: String?,
        urlBoton: String?,
        establecimientoId: Int?,
        fechaInicio: String?,
        fechaFin: String?,
        imagen: MultipartBody.Part?
    ): Result<Anuncio> = safeApiCall {
        api.editarAnuncio(
            id = id,
            titulo = titulo.asTextBody(),
            descripcion = descripcion.asTextBody(),
            tipo = tipo.asTextBody(),
            categoria = categoria.asTextBody(),
            descuento = descuento.asTextBody(),
            textoBoton = textoBoton.asTextBody(),
            urlBoton = urlBoton.asTextBody(),
            establecimiento = establecimientoId?.toString().asTextBody(),
            fechaInicio = fechaInicio.asTextBody(),
            fechaFin = fechaFin.asTextBody(),
            imagen = imagen
        )
    }

    suspend fun eliminarAnuncio(id: Int): Result<Unit> {
        return try {
            val response = api.eliminarAnuncio(id)
            if (response.isSuccessful) Result.success(Unit)
            else Result.failure(Exception("Error ${response.code()}"))
        } catch (e: Exception) {
            Result.failure(Exception("Error de conexión: ${e.message}"))
        }
    }
}
