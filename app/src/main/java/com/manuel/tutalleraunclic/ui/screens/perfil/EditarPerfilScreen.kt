package com.manuel.tutalleraunclic.ui.screens.perfil

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.manuel.tutalleraunclic.utils.fixImageUrl

import com.manuel.tutalleraunclic.data.model.entity.avatarUrl
import com.manuel.tutalleraunclic.data.model.request.UpdateUserRequest
import com.manuel.tutalleraunclic.viewmodel.PerfilViewModel
import com.manuel.tutalleraunclic.viewmodel.UiEvent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditarPerfilScreen(
    onBack: () -> Unit,
    viewModel: PerfilViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    var username  by remember { mutableStateOf("") }
    var nombre    by remember { mutableStateOf("") }
    var apellido  by remember { mutableStateOf("") }
    var email     by remember { mutableStateOf("") }
    var telefono  by remember { mutableStateOf("") }
    var fieldsLoaded by remember { mutableStateOf(false) }

    // Pre-carga los campos una sola vez cuando llegan los datos del usuario
    LaunchedEffect(state.usuario) {
        state.usuario?.let { user ->
            if (!fieldsLoaded) {
                username = user.username  ?: ""
                nombre   = user.first_name ?: ""
                apellido = user.last_name  ?: ""
                email    = user.email     ?: ""
                telefono = user.telefono  ?: ""
                fieldsLoaded = true
            }
        }
    }

    LaunchedEffect(Unit) {
        if (state.usuario == null) viewModel.cargarPerfil()
    }

    LaunchedEffect(Unit) {
        viewModel.uiEvent.collect { event ->
            when (event) {
                is UiEvent.ShowMessage -> { snackbarHostState.showSnackbar(event.message); onBack() }
                is UiEvent.ShowError   -> snackbarHostState.showSnackbar(event.message)
            }
        }
    }

    // El launcher entrega el URI al ViewModel para que sobreviva rotaciones
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri -> viewModel.setFotoUri(uri) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Editar perfil") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->

        if (state.isLoading && state.usuario == null) {
            Box(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center
            ) { CircularProgressIndicator() }
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            // ── Avatar ──────────────────────────────────────────────────────
            // Prioridad: imagen recién seleccionada > foto actual del perfil > placeholder
            val avatarModel: Any = state.fotoUri
                ?: fixImageUrl(state.usuario?.avatarUrl)
                ?: "https://i.pravatar.cc/300?u=0"

            Box(contentAlignment = Alignment.BottomEnd) {
                AsyncImage(
                    model = avatarModel,
                    contentDescription = "Foto de perfil",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(110.dp)
                        .clip(CircleShape)
                        .border(2.dp, MaterialTheme.colorScheme.primary, CircleShape)
                        .clickable { launcher.launch("image/*") }
                )
                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(32.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            Icons.Default.CameraAlt,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            Text(
                "Toca para cambiar foto",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(Modifier.height(4.dp))

            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                label = { Text("Usuario") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            OutlinedTextField(
                value = nombre,
                onValueChange = { nombre = it },
                label = { Text("Nombre") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            OutlinedTextField(
                value = apellido,
                onValueChange = { apellido = it },
                label = { Text("Apellido") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Correo electrónico") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            OutlinedTextField(
                value = telefono,
                onValueChange = { telefono = it },
                label = { Text("Teléfono") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(Modifier.height(4.dp))

            Button(
                onClick = {
                    viewModel.actualizarPerfilConFoto(
                        data = UpdateUserRequest(
                            username   = username.ifBlank { null },
                            first_name = nombre.ifBlank { null },
                            last_name  = apellido.ifBlank { null },
                            email      = email.ifBlank { null },
                            telefono   = telefono.ifBlank { null }
                        ),
                        fotoUri = state.fotoUri
                    )
                },
                modifier = Modifier.fillMaxWidth().height(52.dp),
                enabled = !state.isLoading
            ) {
                if (state.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text("Guardar cambios")
                }
            }
        }
    }
}
