package com.manuel.tutalleraunclic.data.network

import com.manuel.tutalleraunclic.data.model.entity.*
import com.manuel.tutalleraunclic.data.model.request.*
import retrofit2.Response
import retrofit2.http.*
import com.manuel.tutalleraunclic.data.model.response.CitaResponse
import com.manuel.tutalleraunclic.data.model.response.LoginResponse
import com.manuel.tutalleraunclic.data.model.response.CupoAnunciosResponse
import com.manuel.tutalleraunclic.data.model.response.CitaEmpresaResponse
import com.manuel.tutalleraunclic.data.model.response.DashboardEmpresaResponse
import com.manuel.tutalleraunclic.data.model.response.EstablecimientoEmpresaResponse
import com.manuel.tutalleraunclic.data.model.response.ServicioEmpresaResponse

interface ApiService {

    // ==========================
    // AUTH
    // ==========================

    @POST("usuarios/login/")
    suspend fun login(
        @Body request: LoginRequest
    ): Response<LoginResponse>

    @POST("usuarios/auth/google/")
    suspend fun loginWithGoogle(
        @Body request: GoogleAuthRequest
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
    // MIS ESTABLECIMIENTOS (empresa)
    // ==========================

    @GET("empresa/mis-establecimientos/")
    suspend fun getMisEstablecimientos(): Response<List<EstablecimientoEmpresaResponse>>

    @GET("tipos-establecimiento/")
    suspend fun getTiposEstablecimiento(): Response<List<TipoEstablecimiento>>

    @Multipart
    @POST("empresa/mis-establecimientos/crear/")
    suspend fun crearEstablecimientoEmpresa(
        @Part("nombre") nombre: okhttp3.RequestBody,
        @Part("direccion") direccion: okhttp3.RequestBody?,
        @Part("telefono") telefono: okhttp3.RequestBody?,
        @Part("descripcion") descripcion: okhttp3.RequestBody?,
        @Part("hora_apertura") horaApertura: okhttp3.RequestBody?,
        @Part("hora_cierre") horaCierre: okhttp3.RequestBody?,
        @Part("latitud") latitud: okhttp3.RequestBody,
        @Part("longitud") longitud: okhttp3.RequestBody,
        @Part("tipo") tipo: okhttp3.RequestBody,
        @Part foto: okhttp3.MultipartBody.Part?
    ): Response<EstablecimientoEmpresaResponse>

    @Multipart
    @PATCH("empresa/mis-establecimientos/{id}/")
    suspend fun editarEstablecimientoEmpresa(
        @Path("id") id: Int,
        @Part("nombre") nombre: okhttp3.RequestBody?,
        @Part("direccion") direccion: okhttp3.RequestBody?,
        @Part("telefono") telefono: okhttp3.RequestBody?,
        @Part("descripcion") descripcion: okhttp3.RequestBody?,
        @Part("hora_apertura") horaApertura: okhttp3.RequestBody?,
        @Part("hora_cierre") horaCierre: okhttp3.RequestBody?,
        @Part("latitud") latitud: okhttp3.RequestBody?,
        @Part("longitud") longitud: okhttp3.RequestBody?,
        @Part("tipo") tipo: okhttp3.RequestBody?,
        @Part foto: okhttp3.MultipartBody.Part?
    ): Response<EstablecimientoEmpresaResponse>

    @DELETE("empresa/mis-establecimientos/{id}/")
    suspend fun eliminarEstablecimientoEmpresa(
        @Path("id") id: Int
    ): Response<Unit>

    // ==========================
    // SERVICIOS (empresa)
    // ==========================

    @GET("empresa/servicios/")
    suspend fun getServiciosEmpresa(): Response<List<ServicioEmpresaResponse>>

    @POST("empresa/servicios/crear/")
    suspend fun crearServicioEmpresa(
        @Body request: ServicioEmpresaRequest
    ): Response<ServicioEmpresaResponse>

    @PATCH("empresa/servicios/{id}/")
    suspend fun editarServicioEmpresa(
        @Path("id") id: Int,
        @Body request: ServicioEmpresaRequest
    ): Response<ServicioEmpresaResponse>

    @DELETE("empresa/servicios/{id}/")
    suspend fun eliminarServicioEmpresa(
        @Path("id") id: Int
    ): Response<Unit>

    @GET("tipos-servicio/")
    suspend fun getTiposServicio(): Response<List<TipoServicio>>

    @POST("tipos-servicio/crear/")
    suspend fun crearTipoServicio(
        @Body request: TipoServicioRequest
    ): Response<TipoServicio>

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
    // CITAS EMPRESA
    // ==========================

    @GET("empresa/citas/")
    suspend fun getCitasEmpresa(): Response<List<CitaEmpresaResponse>>

    @PATCH("citas/{id}/estado/")
    suspend fun cambiarEstadoCita(
        @Path("id") id: Int,
        @Body request: EstadoRequest
    ): Response<CitaEmpresaResponse>

    @PATCH("citas/{id}/comentario/")
    suspend fun comentarCita(
        @Path("id") id: Int,
        @Body request: ComentarioEmpresaRequest
    ): Response<CitaEmpresaResponse>

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
    suspend fun dashboardEmpresa(): Response<DashboardEmpresaResponse>

    // ==========================
    // ANUNCIOS
    // ==========================

    @GET("anuncios/")
    suspend fun getAnunciosPublicos(
        @Query("categoria") categoria: String? = null
    ): Response<List<Anuncio>>

    @GET("api/empresa/anuncios/")
    suspend fun getMisAnuncios(): Response<List<Anuncio>>

    @Multipart
    @POST("api/empresa/anuncios/")
    suspend fun crearAnuncio(
        @Part("titulo") titulo: okhttp3.RequestBody,
        @Part("descripcion") descripcion: okhttp3.RequestBody?,
        @Part("tipo") tipo: okhttp3.RequestBody,
        @Part("categoria") categoria: okhttp3.RequestBody,
        @Part("descuento") descuento: okhttp3.RequestBody?,
        @Part("texto_boton") textoBoton: okhttp3.RequestBody?,
        @Part("url_boton") urlBoton: okhttp3.RequestBody?,
        @Part("establecimiento") establecimiento: okhttp3.RequestBody,
        @Part("fecha_inicio") fechaInicio: okhttp3.RequestBody?,
        @Part("fecha_fin") fechaFin: okhttp3.RequestBody?,
        @Part imagen: okhttp3.MultipartBody.Part?
    ): Response<Anuncio>

    @Multipart
    @PATCH("api/empresa/anuncios/{id}/")
    suspend fun editarAnuncio(
        @Path("id") id: Int,
        @Part("titulo") titulo: okhttp3.RequestBody?,
        @Part("descripcion") descripcion: okhttp3.RequestBody?,
        @Part("tipo") tipo: okhttp3.RequestBody?,
        @Part("categoria") categoria: okhttp3.RequestBody?,
        @Part("descuento") descuento: okhttp3.RequestBody?,
        @Part("texto_boton") textoBoton: okhttp3.RequestBody?,
        @Part("url_boton") urlBoton: okhttp3.RequestBody?,
        @Part("establecimiento") establecimiento: okhttp3.RequestBody?,
        @Part("fecha_inicio") fechaInicio: okhttp3.RequestBody?,
        @Part("fecha_fin") fechaFin: okhttp3.RequestBody?,
        @Part imagen: okhttp3.MultipartBody.Part?
    ): Response<Anuncio>

    @DELETE("api/empresa/anuncios/{id}/")
    suspend fun eliminarAnuncio(
        @Path("id") id: Int
    ): Response<Unit>

    @GET("api/empresa/anuncios/cupo/")
    suspend fun getCupoAnuncios(
        @Query("establecimiento") establecimientoId: Int
    ): Response<CupoAnunciosResponse>
}
