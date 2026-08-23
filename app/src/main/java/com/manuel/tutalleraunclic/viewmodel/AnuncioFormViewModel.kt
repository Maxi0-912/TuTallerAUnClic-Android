package com.manuel.tutalleraunclic.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.manuel.tutalleraunclic.data.model.entity.Establecimiento
import com.manuel.tutalleraunclic.data.model.entity.Servicio
import com.manuel.tutalleraunclic.data.model.entity.TipoServicio
import com.manuel.tutalleraunclic.data.model.response.CupoAnunciosResponse
import com.manuel.tutalleraunclic.data.repository.AnuncioRepository
import com.manuel.tutalleraunclic.data.repository.MainRepository
import com.manuel.tutalleraunclic.data.repository.ServicioEmpresaRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import javax.inject.Inject

data class AnuncioFormUiState(
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val esEdicion: Boolean = false,
    val anuncioId: Int? = null,

    val misEstablecimientos: List<Establecimiento> = emptyList(),
    val establecimientoId: Int? = null,

    val titulo: String = "",
    val descripcion: String = "",
    val tipo: String = "imagen",
    val categoria: String = "banner",
    val descuento: String = "",
    val textoBoton: String = "",
    val urlBoton: String = "",
    val fechaInicio: String = "",
    val fechaFin: String = "",

    val servicios: List<Servicio> = emptyList(),
    val servicioId: Int? = null,
    val ubicaciones: List<String> = emptyList(),

    // Crear servicio inline desde el formulario de anuncio
    val tiposServicio: List<TipoServicio> = emptyList(),
    val mostrarDialogoServicio: Boolean = false,
    val creandoServicio: Boolean = false,
    val errorCrearServicio: String? = null,

    val imagenUri: Uri? = null,
    val imagenUrlActual: String? = null,

    val cupo: CupoAnunciosResponse? = null,

    // Solo lectura, mostrados si se está editando un anuncio existente
    val estado: String? = null,
    val motivoRechazo: String? = null,

    val guardadoExitoso: Boolean = false,
    val requierePago: Boolean = false
)

@HiltViewModel
class AnuncioFormViewModel @Inject constructor(
    private val anuncioRepository: AnuncioRepository,
    private val mainRepository: MainRepository,
    private val servicioEmpresaRepository: ServicioEmpresaRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _uiState = MutableStateFlow(AnuncioFormUiState())
    val uiState: StateFlow<AnuncioFormUiState> = _uiState.asStateFlow()

    private val _uiEvent = MutableSharedFlow<UiEvent>()
    val uiEvent: SharedFlow<UiEvent> = _uiEvent.asSharedFlow()

    private var yaInicializado = false

    /** Llamado una vez desde la pantalla. [anuncioId] null = creando un anuncio nuevo. */
    fun inicializar(anuncioId: Int?) {
        if (yaInicializado) return
        yaInicializado = true
        _uiState.update { it.copy(esEdicion = anuncioId != null, anuncioId = anuncioId) }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            val perfil = mainRepository.getPerfil().getOrNull()
            val misEstablecimientos = mainRepository.getEstablecimientos().getOrDefault(emptyList())
                .filter { it.propietario == perfil?.id }
            val tiposServicio = servicioEmpresaRepository.getTipos().getOrDefault(emptyList())

            _uiState.update { it.copy(misEstablecimientos = misEstablecimientos, tiposServicio = tiposServicio) }

            if (anuncioId != null) {
                val anuncio = anuncioRepository.getMisAnuncios().getOrNull()
                    ?.firstOrNull { it.id == anuncioId }

                if (anuncio != null) {
                    _uiState.update {
                        it.copy(
                            titulo = anuncio.titulo,
                            descripcion = anuncio.descripcion ?: "",
                            tipo = anuncio.tipo,
                            categoria = anuncio.categoria,
                            descuento = anuncio.descuento ?: "",
                            textoBoton = anuncio.texto_boton ?: "",
                            urlBoton = anuncio.url_boton ?: "",
                            fechaInicio = anuncio.fecha_inicio ?: "",
                            fechaFin = anuncio.fecha_fin ?: "",
                            establecimientoId = anuncio.establecimiento,
                            servicioId = anuncio.servicio?.id,
                            ubicaciones = anuncio.ubicaciones ?: emptyList(),
                            imagenUrlActual = anuncio.imagen_url,
                            estado = anuncio.estado,
                            motivoRechazo = anuncio.motivo_rechazo
                        )
                    }
                    cargarServicios(anuncio.establecimiento)
                } else {
                    _uiEvent.emit(UiEvent.ShowError("No se pudo cargar el anuncio"))
                }
            } else if (misEstablecimientos.size == 1) {
                seleccionarEstablecimiento(misEstablecimientos.first().id)
            }

            _uiState.update { it.copy(isLoading = false) }
        }
    }

    fun seleccionarEstablecimiento(id: Int) {
        _uiState.update { it.copy(establecimientoId = id, servicioId = null, servicios = emptyList()) }
        if (!_uiState.value.esEdicion) cargarCupo(id)
        cargarServicios(id)
    }

    private fun cargarCupo(establecimientoId: Int) {
        viewModelScope.launch {
            anuncioRepository.getCupo(establecimientoId)
                .onSuccess { cupo -> _uiState.update { it.copy(cupo = cupo) } }
                .onFailure { _uiState.update { it.copy(cupo = null) } }
        }
    }

    private fun cargarServicios(establecimientoId: Int) {
        viewModelScope.launch {
            try {
                val servicios = mainRepository.getServicios(establecimientoId)
                _uiState.update { it.copy(servicios = servicios) }
            } catch (_: Exception) {
                _uiState.update { it.copy(servicios = emptyList()) }
            }
        }
    }

    fun setTitulo(v: String) = _uiState.update { it.copy(titulo = v) }
    fun setDescripcion(v: String) = _uiState.update { it.copy(descripcion = v) }
    fun setTipo(v: String) = _uiState.update { it.copy(tipo = v) }
    fun setCategoria(v: String) = _uiState.update { it.copy(categoria = v) }
    fun setDescuento(v: String) = _uiState.update { it.copy(descuento = v) }
    fun setTextoBoton(v: String) = _uiState.update { it.copy(textoBoton = v) }
    fun setUrlBoton(v: String) = _uiState.update { it.copy(urlBoton = v) }
    fun setFechaInicio(v: String) = _uiState.update { it.copy(fechaInicio = v) }
    fun setFechaFin(v: String) = _uiState.update { it.copy(fechaFin = v) }
    fun setImagenUri(uri: Uri?) = _uiState.update { it.copy(imagenUri = uri) }
    fun setServicioId(id: Int?) = _uiState.update { it.copy(servicioId = id) }

    fun toggleUbicacion(valor: String) = _uiState.update {
        val nuevas = if (valor in it.ubicaciones) it.ubicaciones - valor else it.ubicaciones + valor
        it.copy(ubicaciones = nuevas)
    }

    fun abrirDialogoNuevoServicio() = _uiState.update {
        it.copy(mostrarDialogoServicio = true, errorCrearServicio = null)
    }

    fun cerrarDialogoNuevoServicio() = _uiState.update {
        it.copy(mostrarDialogoServicio = false, errorCrearServicio = null)
    }

    /** Crea un servicio sin salir del formulario de anuncio y lo deja seleccionado. */
    fun crearServicioInline(nombre: String, tipoServicioId: Int?) {
        val establecimientoId = _uiState.value.establecimientoId
        if (establecimientoId == null) {
            _uiState.update { it.copy(errorCrearServicio = "Selecciona un establecimiento primero") }
            return
        }
        if (nombre.isBlank()) {
            _uiState.update { it.copy(errorCrearServicio = "El nombre es obligatorio") }
            return
        }
        if (tipoServicioId == null) {
            _uiState.update { it.copy(errorCrearServicio = "Selecciona un tipo de servicio") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(creandoServicio = true, errorCrearServicio = null) }
            servicioEmpresaRepository.crear(nombre.trim(), establecimientoId, tipoServicioId)
                .onSuccess { creado ->
                    val nuevoServicio = Servicio(
                        id = creado.id,
                        nombre = creado.nombre,
                        establecimiento = creado.establecimiento_id,
                        tipo_servicio = creado.tipo_servicio_id
                    )
                    _uiState.update {
                        it.copy(
                            creandoServicio = false,
                            mostrarDialogoServicio = false,
                            servicios = it.servicios + nuevoServicio,
                            servicioId = nuevoServicio.id
                        )
                    }
                }
                .onFailure { e ->
                    _uiState.update {
                        it.copy(
                            creandoServicio = false,
                            errorCrearServicio = e.message ?: "No se pudo crear el servicio"
                        )
                    }
                }
        }
    }

    fun guardar() {
        val state = _uiState.value
        val establecimientoId = state.establecimientoId

        if (state.titulo.isBlank()) {
            viewModelScope.launch { _uiEvent.emit(UiEvent.ShowError("El título es obligatorio")) }
            return
        }
        if (establecimientoId == null) {
            viewModelScope.launch { _uiEvent.emit(UiEvent.ShowError("Selecciona un establecimiento")) }
            return
        }
        if (!state.esEdicion && state.imagenUri == null) {
            viewModelScope.launch { _uiEvent.emit(UiEvent.ShowError("Selecciona una imagen para el anuncio")) }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true) }

            val imagenPart = state.imagenUri?.toMultipartPart()

            val result = if (state.esEdicion) {
                anuncioRepository.editarAnuncio(
                    id = state.anuncioId!!,
                    titulo = state.titulo,
                    descripcion = state.descripcion.ifBlank { null },
                    tipo = state.tipo,
                    categoria = state.categoria,
                    descuento = state.descuento.ifBlank { null },
                    textoBoton = state.textoBoton.ifBlank { null },
                    urlBoton = state.urlBoton.ifBlank { null },
                    establecimientoId = establecimientoId,
                    fechaInicio = state.fechaInicio.ifBlank { null },
                    fechaFin = state.fechaFin.ifBlank { null },
                    servicioId = state.servicioId,
                    ubicaciones = state.ubicaciones,
                    imagen = imagenPart
                )
            } else {
                anuncioRepository.crearAnuncio(
                    titulo = state.titulo,
                    descripcion = state.descripcion.ifBlank { null },
                    tipo = state.tipo,
                    categoria = state.categoria,
                    descuento = state.descuento.ifBlank { null },
                    textoBoton = state.textoBoton.ifBlank { null },
                    urlBoton = state.urlBoton.ifBlank { null },
                    establecimientoId = establecimientoId,
                    fechaInicio = state.fechaInicio.ifBlank { null },
                    fechaFin = state.fechaFin.ifBlank { null },
                    servicioId = state.servicioId,
                    ubicaciones = state.ubicaciones,
                    imagen = imagenPart
                )
            }

            result
                .onSuccess { anuncio ->
                    // El backend no manda un campo "requiere_pago": un anuncio requiere pago
                    // cuando es_pago == true y todavía no está pagado.
                    val requierePago = anuncio.es_pago == true && anuncio.pagado != true
                    val mensaje = if (requierePago) {
                        "Tu anuncio quedó pendiente de pago y de aprobación."
                    } else {
                        "Tu anuncio queda pendiente de aprobación"
                    }
                    _uiState.update {
                        it.copy(
                            isSaving = false,
                            guardadoExitoso = true,
                            requierePago = requierePago,
                            anuncioId = anuncio.id
                        )
                    }
                    _uiEvent.emit(UiEvent.ShowMessage(mensaje))
                }
                .onFailure { e ->
                    _uiState.update { it.copy(isSaving = false) }
                    _uiEvent.emit(UiEvent.ShowError(e.message ?: "No se pudo guardar el anuncio"))
                }
        }
    }

    private fun Uri.toMultipartPart(): MultipartBody.Part? {
        return try {
            val inputStream = context.contentResolver.openInputStream(this) ?: return null
            val bytes = inputStream.readBytes()
            inputStream.close()

            val mimeType = context.contentResolver.getType(this) ?: "image/jpeg"
            val requestBody = bytes.toRequestBody(mimeType.toMediaTypeOrNull())

            MultipartBody.Part.createFormData("imagen", "imagen.jpg", requestBody)
        } catch (e: Exception) {
            null
        }
    }
}
