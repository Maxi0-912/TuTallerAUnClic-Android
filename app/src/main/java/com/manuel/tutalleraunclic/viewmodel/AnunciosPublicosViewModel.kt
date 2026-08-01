package com.manuel.tutalleraunclic.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.manuel.tutalleraunclic.data.model.entity.Anuncio
import com.manuel.tutalleraunclic.data.repository.AnuncioRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/** Alimenta el carrusel de banners de la home con GET /anuncios/?categoria=banner. */
@HiltViewModel
class AnunciosPublicosViewModel @Inject constructor(
    private val repository: AnuncioRepository
) : ViewModel() {

    private val _banners = MutableStateFlow<List<Anuncio>>(emptyList())
    val banners: StateFlow<List<Anuncio>> = _banners.asStateFlow()

    init {
        cargarBanners()
    }

    fun cargarBanners() {
        viewModelScope.launch {
            repository.getAnunciosPublicos(categoria = "banner")
                .onSuccess { _banners.value = it }
                .onFailure { _banners.value = emptyList() }
        }
    }
}
