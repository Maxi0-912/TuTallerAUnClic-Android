package com.manuel.tutalleraunclic.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import com.manuel.tutalleraunclic.data.repository.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class DetalleViewModel @Inject constructor(
    private val repository: MainRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(DetalleUiState())
    val uiState: StateFlow<DetalleUiState> = _uiState.asStateFlow()

    private val _routePoints = MutableStateFlow<List<LatLng>>(emptyList())
    val routePoints: StateFlow<List<LatLng>> = _routePoints.asStateFlow()

    private val _isLoadingRoute = MutableStateFlow(false)
    val isLoadingRoute: StateFlow<Boolean> = _isLoadingRoute.asStateFlow()

    private val _routeError = MutableStateFlow<String?>(null)
    val routeError: StateFlow<String?> = _routeError.asStateFlow()

    fun cargar(id: Int) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            val detalleJob   = async { repository.getDetalleEstablecimiento(id) }
            val serviciosJob = async { runCatching { repository.getServicios(id) } }
            val resenasJob   = async { repository.getResenasEstablecimiento(id) }

            val detalleResult   = detalleJob.await()
            val serviciosResult = serviciosJob.await()
            val resenasResult   = resenasJob.await()

            _uiState.update {
                it.copy(
                    isLoading       = false,
                    establecimiento = detalleResult.getOrNull(),
                    servicios       = serviciosResult.getOrElse { emptyList() },
                    resenas         = resenasResult.getOrElse { emptyList() },
                    error           = detalleResult.exceptionOrNull()?.message
                )
            }
        }
    }

    fun cargarRuta(
        userLat: Double, userLng: Double,
        destLat: Double, destLng: Double
    ) {
        val apiKey = "AIzaSyBs1FqjWwhCt1YCEsQimNSHNIB6wBCxf5o"
        viewModelScope.launch {
            _isLoadingRoute.value = true
            _routeError.value     = null
            try {
                val encodedPolyline: String? = withContext(Dispatchers.IO) {
                    val urlString =
                        "https://maps.googleapis.com/maps/api/directions/json" +
                        "?origin=$userLat,$userLng" +
                        "&destination=$destLat,$destLng" +
                        "&mode=driving" +
                        "&key=$apiKey"
                    android.util.Log.d("RUTA_API", "API Key primeros 10: ${apiKey.take(10)}")
                    android.util.Log.d("RUTA_API", "URL: $urlString")
                    val url = java.net.URL(urlString)
                    val conn = url.openConnection() as java.net.HttpURLConnection
                    conn.connectTimeout = 10_000
                    conn.readTimeout    = 10_000
                    val body = conn.inputStream.bufferedReader().readText()
                    android.util.Log.d("RUTA_API", "Respuesta Directions API: $body")
                    conn.disconnect()
                    val json   = org.json.JSONObject(body)
                    val routes = json.getJSONArray("routes")
                    if (routes.length() == 0) return@withContext null
                    routes.getJSONObject(0)
                          .getJSONObject("overview_polyline")
                          .getString("points")
                }
                if (encodedPolyline != null) {
                    _routePoints.value = com.google.maps.android.PolyUtil.decode(encodedPolyline)
                } else {
                    _routeError.value = "No se encontró ruta"
                }
            } catch (e: Exception) {
                _routeError.value = "Error al cargar la ruta"
            } finally {
                _isLoadingRoute.value = false
            }
        }
    }
}
