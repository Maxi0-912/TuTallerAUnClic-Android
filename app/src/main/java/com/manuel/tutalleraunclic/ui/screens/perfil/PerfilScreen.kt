package com.manuel.tutalleraunclic.ui.screens.perfil

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import coil.compose.AsyncImage
import com.manuel.tutalleraunclic.data.model.entity.Usuario
import com.manuel.tutalleraunclic.data.model.entity.avatarUrl
import com.manuel.tutalleraunclic.data.model.entity.displayEmail
import com.manuel.tutalleraunclic.data.model.entity.displayNombre
import com.manuel.tutalleraunclic.data.model.entity.displayRol
import com.manuel.tutalleraunclic.data.model.entity.displayTelefono
import com.manuel.tutalleraunclic.data.model.entity.displayUsername
import com.manuel.tutalleraunclic.ui.components.DesactivarCuentaSection
import com.manuel.tutalleraunclic.ui.components.TemaCard
import com.manuel.tutalleraunclic.viewmodel.PerfilViewModel
import com.manuel.tutalleraunclic.viewmodel.UiEvent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PerfilScreen(
    onNavigateToLogin: () -> Unit,
    onNavigateToEditarPerfil: () -> Unit,
    viewModel: PerfilViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) viewModel.cargarPerfil()
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    LaunchedEffect(Unit) {
        viewModel.uiEvent.collect { event ->
            when (event) {
                is UiEvent.ShowError   -> snackbarHostState.showSnackbar(event.message)
                is UiEvent.ShowMessage -> snackbarHostState.showSnackbar(event.message)
            }
        }
    }

    LaunchedEffect(state.success) {
        if (state.success) onNavigateToLogin()
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Mi perfil") }) },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            if (state.isLoading && state.usuario == null) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }

            state.usuario?.let { user ->
                PerfilContent(
                    user = user,
                    onEditarPerfil = onNavigateToEditarPerfil,
                    onLogout = { viewModel.logout() },
                    onEliminarCuenta = { viewModel.desactivarCuenta() }
                )
            }
        }
    }
}

// ─── Contenido principal ────────────────────────────────────────────────────

@Composable
private fun PerfilContent(
    user: Usuario,
    onEditarPerfil: () -> Unit,
    onLogout: () -> Unit,
    onEliminarCuenta: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(0.dp)
    ) {
        Spacer(Modifier.height(8.dp))

        val avatarPlaceholder = ColorPainter(Color(0xFFE0E0E0))
        AsyncImage(
            model = user.avatarUrl,
            contentDescription = "Foto de perfil",
            placeholder = avatarPlaceholder,
            error = avatarPlaceholder,
            modifier = Modifier
                .size(110.dp)
                .clip(CircleShape)
        )

        Spacer(Modifier.height(16.dp))

        Text(
            text = user.displayNombre,
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
        )
        Text(
            text = user.displayRol,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(Modifier.height(24.dp))

        InfoCard(user = user)

        Spacer(Modifier.height(16.dp))

        TemaCard()

        Spacer(Modifier.height(24.dp))

        Button(
            onClick = onEditarPerfil,
            modifier = Modifier.fillMaxWidth()
        ) { Text("Editar perfil") }

        Spacer(Modifier.height(8.dp))

        OutlinedButton(
            onClick = onLogout,
            modifier = Modifier.fillMaxWidth()
        ) { Text("Cerrar sesión") }

        Spacer(Modifier.height(8.dp))

        DesactivarCuentaSection(onDesactivar = onEliminarCuenta)

        Spacer(Modifier.height(16.dp))
    }
}

// ─── Card de información ─────────────────────────────────────────────────────

@Composable
private fun InfoCard(user: Usuario) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            InfoRow(
                icon  = Icons.Default.Person,
                label = "Usuario",
                value = user.displayUsername
            )
            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
            InfoRow(
                icon  = Icons.Default.Email,
                label = "Correo",
                value = user.displayEmail
            )
            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
            InfoRow(
                icon  = Icons.Default.Phone,
                label = "Teléfono",
                value = user.displayTelefono
            )
            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
            InfoRow(
                icon  = Icons.Default.Work,
                label = "Rol",
                value = user.displayRol
            )
        }
    }
}

// ─── Fila de información ─────────────────────────────────────────────────────

@Composable
private fun InfoRow(icon: ImageVector, label: String, value: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(24.dp)
        )
        Column {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}
