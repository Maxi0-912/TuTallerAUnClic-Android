package com.manuel.tutalleraunclic.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.manuel.tutalleraunclic.data.local.ThemePreference
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ThemeViewModel @Inject constructor(
    private val themePreference: ThemePreference
) : ViewModel() {

    val isDarkMode: StateFlow<Boolean> = themePreference.isDarkMode
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), false)

    fun toggle() {
        viewModelScope.launch {
            themePreference.setDarkMode(!isDarkMode.value)
        }
    }
}
