package com.manuel.tutalleraunclic.ui.screens.perfil

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import coil.compose.AsyncImage
import com.manuel.tutalleraunclic.data.model.entity.avatarUrl
import com.manuel.tutalleraunclic.data.model.entity.displayNombre
import com.manuel.tutalleraunclic.data.model.entity.displayRol
import com.manuel.tutalleraunclic.data.model.request.UpdateUserRequest
import com.manuel.tutalleraunclic.ui.components.DesactivarCuentaSection
import com.manuel.tutalleraunclic.ui.components.TemaCard
import com.manuel.tutalleraunclic.viewmodel.PerfilViewModel
import com.manuel.tutalleraunclic.viewmodel.UiEvent

private val GreenSuccess = Color(0xFF2E7D32)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PerfilEmpresaScreen(
    onNavigateToLogin: () -> Unit,
    viewModel: PerfilViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()

    // ── Campos de texto ──────────────────────────────────────────────────────
    var nombre       by remember { mutableStateOf("") }
    var apellido     by remember { mutableStateOf("") }
    var email        by remember { mutableStateOf("") }
    var telefono     by remember { mutableStateOf("") }
    var fieldsLoaded by remember { mutableStateOf(false) }

    // ── Contraseña ───────────────────────────────────────────────────────────
    var nuevaPassword  by remember { mutableStateOf("") }
    var confirmarPass  by remember { mutableStateOf("") }
    var verNueva       by remember { mutableStateOf(false) }
    var verConfirmar   by remember { mutableStateOf(false) }

    // ── Mensajes inline ──────────────────────────────────────────────────────
    var errorText   by remember { mutableStateOf<String?>(null) }
    var successText by remember { mutableStateOf<String?>(null) }

    // Pre-carga campos una sola vez cuando llegan los datos del usuario
    LaunchedEffect(state.usuario) {
        state.usuario?.let { user ->
            if (!fieldsLoaded) {
                nombre   = user.first_name ?: ""
                apellido = user.last_name  ?: ""
                email    = user.email      ?: ""
                telefono = user.telefono   ?: ""
                fieldsLoaded = true
            }
        }
    }

    // Recarga el perfil al volver a esta pantalla
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) viewModel.cargarPerfil()
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    // Recibe eventos del ViewModel y los muestra como texto inline
    LaunchedEffect(Unit) {
        viewModel.uiEvent.collect { event ->
            when (event) {
                is UiEvent.ShowMessage -> { successText = event.message; errorText = null }
                is UiEvent.ShowError   -> { errorText = event.message;   successText = null }
            }
        }
    }

    // Navega a login después del logout (state.success = true)
    LaunchedEffect(state.success) {
        if (state.success) onNavigateToLogin()
    }

    // Selector de foto desde galería
    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri -> viewModel.setFotoUri(uri) }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Mi perfil") }) }
    ) { padding ->

        // Pantalla de carga inicial
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
                .padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(0.dp)
        ) {
            Spacer(Modifier.height(8.dp))

            // ── Avatar circular con botón de cámara ──────────────────────────
            val avatarPlaceholder = ColorPainter(Color(0xFFE0E0E0))
            val avatarModel: Any = state.fotoUri
                ?: state.usuario?.avatarUrl
                ?: ""

            Box(contentAlignment = Alignment.BottomEnd) {
                AsyncImage(
                    model = avatarModel,
                    contentDescription = "Foto de perfil",
                    placeholder = avatarPlaceholder,
                    error = avatarPlaceholder,
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
                            contentDescription = "Cambiar foto",
                            tint = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            Spacer(Modifier.height(12.dp))

            // ── Nombre y rol ─────────────────────────────────────────────────
            state.usuario?.let { user ->
                Text(
                    text = user.displayNombre,
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
                )
                Text(
                    text = user.displayRol,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(Modifier.height(24.dp))

            // ── Información personal ─────────────────────────────────────────
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.large
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Información personal",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold)
                    )
                    OutlinedTextField(
                        value = nombre,
                        onValueChange = { nombre = it; errorText = null; successText = null },
                        label = { Text("Nombre") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = apellido,
                        onValueChange = { apellido = it; errorText = null; successText = null },
                        label = { Text("Apellido") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it; errorText = null; successText = null },
                        label = { Text("Correo electrónico") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = telefono,
                        onValueChange = { telefono = it; errorText = null; successText = null },
                        label = { Text("Teléfono") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            Spacer(Modifier.height(12.dp))

            // ── Cambiar contraseña ────────────────────────────────────────────
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.large
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Cambiar contraseña",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold)
                    )
                    OutlinedTextField(
                        value = nuevaPassword,
                        onValueChange = { nuevaPassword = it; errorText = null; successText = null },
                        label = { Text("Nueva contraseña") },
                        singleLine = true,
                        visualTransformation = if (verNueva) VisualTransformation.None
                                              else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        trailingIcon = {
                            IconButton(onClick = { verNueva = !verNueva }) {
                                Icon(
                                    imageVector = if (verNueva) Icons.Default.VisibilityOff
                                                  else Icons.Default.Visibility,
                                    contentDescription = null
                                )
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = confirmarPass,
                        onValueChange = { confirmarPass = it; errorText = null; successText = null },
                        label = { Text("Confirmar contraseña") },
                        singleLine = true,
                        visualTransformation = if (verConfirmar) VisualTransformation.None
                                              else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        trailingIcon = {
                            IconButton(onClick = { verConfirmar = !verConfirmar }) {
                                Icon(
                                    imageVector = if (verConfirmar) Icons.Default.VisibilityOff
                                                  else Icons.Default.Visibility,
                                    contentDescription = null
                                )
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            Spacer(Modifier.height(12.dp))

            TemaCard()

            Spacer(Modifier.height(12.dp))

            // ── Mensajes de error / éxito ─────────────────────────────────────
            errorText?.let {
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(4.dp))
            }
            successText?.let {
                Text(
                    text = it,
                    color = GreenSuccess,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(4.dp))
            }

            // ── Guardar cambios ───────────────────────────────────────────────
            Button(
                onClick = {
                    val passwordFilled = nuevaPassword.isNotBlank() || confirmarPass.isNotBlank()
                    val fotoSeleccionada = state.fotoUri != null

                    // Validar contraseña si se intenta cambiar
                    if (passwordFilled) {
                        if (nuevaPassword != confirmarPass) {
                            errorText = "Las contraseñas no coinciden"
                            return@Button
                        }
                        if (nuevaPassword.length < 8) {
                            errorText = "La contraseña debe tener al menos 8 caracteres"
                            return@Button
                        }
                        // El multipart no puede transportar contraseña; pedir al usuario
                        // que guarde en dos pasos si tiene ambos cambios pendientes
                        if (fotoSeleccionada) {
                            errorText = "Guarda la foto primero y luego cambia la contraseña por separado"
                            return@Button
                        }
                    }

                    errorText = null
                    successText = null

                    val data = UpdateUserRequest(
                        first_name = nombre.ifBlank { null },
                        last_name  = apellido.ifBlank { null },
                        email      = email.ifBlank { null },
                        telefono   = telefono.ifBlank { null },
                        password   = if (passwordFilled) nuevaPassword else null
                    )
                    viewModel.actualizarPerfilConFoto(data = data, fotoUri = state.fotoUri)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                enabled = !state.isLoading
            ) {
                if (state.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text("Guardar cambios", fontWeight = FontWeight.SemiBold)
                }
            }

            Spacer(Modifier.height(8.dp))

            // ── Cerrar sesión ─────────────────────────────────────────────────
            OutlinedButton(
                onClick = { viewModel.logout() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            ) {
                Text("Cerrar sesión")
            }

            Spacer(Modifier.height(8.dp))

            DesactivarCuentaSection(onDesactivar = { viewModel.desactivarCuenta() })

            Spacer(Modifier.height(16.dp))
        }
    }
}
