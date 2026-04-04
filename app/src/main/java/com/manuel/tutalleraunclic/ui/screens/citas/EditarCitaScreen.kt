package com.manuel.tutalleraunclic.ui.screens.citas

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable // 🔥 ESTE FALTABA
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun EditarCitaScreen(
    citaId: Int
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text("Editando cita ID: $citaId")
    }
}