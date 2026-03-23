package com.manuel.tutalleraunclic.ui.screens.citas
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.manuel.tutalleraunclic.viewmodel.MisCitasViewModel

@Composable
fun MisCitasScreen(
    navController: NavController,
    viewModel: MisCitasViewModel = viewModel()
){

    LaunchedEffect(Unit) {
        viewModel.cargarCitas()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        Text(
            text = "Mis Citas",
            style = MaterialTheme.typography.headlineMedium
        )

    }
}