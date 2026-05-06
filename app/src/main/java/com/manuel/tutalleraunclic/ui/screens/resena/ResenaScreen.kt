package com.manuel.tutalleraunclic.ui.screens.resena

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.manuel.tutalleraunclic.viewmodel.ResenaViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResenaScreen(
    citaId: Int,
    establecimientoId: Int,
    onSuccess: () -> Unit,
    onBack: () -> Unit,
    viewModel: ResenaViewModel = hiltViewModel()
) {
    var puntuacion by remember { mutableIntStateOf(0) }
    var comentario by remember { mutableStateOf("") }
    val snackbarHostState = remember { SnackbarHostState() }

    // Navegar al enviar con éxito
    LaunchedEffect(viewModel.success) {
        if (viewModel.success) {
            snackbarHostState.showSnackbar("¡Reseña enviada con éxito!")
            onSuccess()
        }
    }

    // Mostrar errores
    LaunchedEffect(viewModel.error) {
        viewModel.error?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.resetError()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Calificar servicio") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {

            Spacer(modifier = Modifier.height(8.dp))

            // Título
            Text(
                text = "¿Cómo fue tu experiencia?",
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
            )

            Text(
                text = "Tu opinión ayuda a otros usuarios a elegir el mejor taller o lavadero",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )

            // Selector de estrellas
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = when (puntuacion) {
                        1 -> "Muy malo"
                        2 -> "Malo"
                        3 -> "Regular"
                        4 -> "Bueno"
                        5 -> "Excelente"
                        else -> "Toca para calificar"
                    },
                    style = MaterialTheme.typography.titleMedium,
                    color = if (puntuacion > 0) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    (1..5).forEach { star ->
                        IconButton(
                            onClick = { puntuacion = star },
                            modifier = Modifier.size(48.dp)
                        ) {
                            Icon(
                                imageVector = if (star <= puntuacion) Icons.Filled.Star
                                else Icons.Outlined.StarOutline,
                                contentDescription = "$star estrellas",
                                tint = if (star <= puntuacion) MaterialTheme.colorScheme.tertiary
                                else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
                                modifier = Modifier.size(40.dp)
                            )
                        }
                    }
                }
            }

            // Campo de comentario
            OutlinedTextField(
                value = comentario,
                onValueChange = { comentario = it },
                label = { Text("Cuéntanos tu experiencia (opcional)") },
                placeholder = { Text("El servicio fue rápido, el trato fue amable...") },
                minLines = 4,
                maxLines = 6,
                modifier = Modifier.fillMaxWidth()
            )

            // Botón enviar
            Button(
                onClick = {
                    viewModel.enviarResena(citaId, puntuacion, comentario)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                enabled = !viewModel.isLoading && puntuacion > 0
            ) {
                if (viewModel.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        text = "Enviar reseña",
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }
        }
    }
}
