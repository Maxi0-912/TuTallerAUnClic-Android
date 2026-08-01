package com.manuel.tutalleraunclic.ui.screens.establecimientos

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.manuel.tutalleraunclic.R
import com.manuel.tutalleraunclic.utils.fixImageUrl
import com.manuel.tutalleraunclic.viewmodel.EstablecimientoFormViewModel
import com.manuel.tutalleraunclic.viewmodel.UiEvent
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EstablecimientoFormScreen(
    establecimientoId: Int?,
    onBack: () -> Unit,
    onSuccess: () -> Unit,
    viewModel: EstablecimientoFormViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) { viewModel.inicializar(establecimientoId) }

    LaunchedEffect(Unit) {
        viewModel.uiEvent.collect { event ->
            val mensaje = when (event) {
                is UiEvent.ShowMessage -> event.message
                is UiEvent.ShowError -> event.message
            }
            scope.launch { snackbarHostState.showSnackbar(mensaje) }
        }
    }

    LaunchedEffect(state.guardadoExitoso) {
        if (state.guardadoExitoso) onSuccess()
    }

    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri -> viewModel.setFotoUri(uri) }

    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                },
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.logo_solo),
                            contentDescription = "Logo",
                            modifier = Modifier.size(32.dp).clip(CircleShape)
                        )
                        Text(if (state.esEdicion) "Editar establecimiento" else "Nuevo establecimiento", fontWeight = FontWeight.Bold)
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { padding ->

        if (state.isLoading) {
            Box(Modifier.padding(padding).fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // ── Foto ─────────────────────────────────────────────────────────
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .clickable { launcher.launch("image/*") },
                    contentAlignment = Alignment.Center
                ) {
                    val modelo: Any? = state.fotoUri ?: fixImageUrl(state.fotoUrlActual)
                    if (modelo != null) {
                        AsyncImage(
                            model = modelo,
                            contentDescription = "Foto del establecimiento",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.Image, contentDescription = null)
                            Spacer(Modifier.height(4.dp))
                            Text("Toca para elegir una foto")
                        }
                    }
                }
                TextButton(onClick = { launcher.launch("image/*") }) {
                    Text(if (state.fotoUri != null || state.fotoUrlActual != null) "Cambiar foto" else "Elegir foto")
                }
            }

            OutlinedTextField(
                value = state.nombre,
                onValueChange = viewModel::setNombre,
                label = { Text("Nombre *") },
                modifier = Modifier.fillMaxWidth()
            )

            // ── Tipo ─────────────────────────────────────────────────────────
            var tipoExpanded by remember { mutableStateOf(false) }
            val tipoSeleccionado = state.tipos.firstOrNull { it.id == state.tipoId }
            ExposedDropdownMenuBox(expanded = tipoExpanded, onExpandedChange = { tipoExpanded = it }) {
                OutlinedTextField(
                    value = tipoSeleccionado?.nombre ?: "",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Tipo *") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = tipoExpanded) },
                    modifier = Modifier.fillMaxWidth().menuAnchor()
                )
                ExposedDropdownMenu(expanded = tipoExpanded, onDismissRequest = { tipoExpanded = false }) {
                    if (state.tipos.isEmpty()) {
                        DropdownMenuItem(text = { Text("No hay tipos disponibles") }, onClick = {})
                    }
                    state.tipos.forEach { tipo ->
                        DropdownMenuItem(
                            text = { Text(tipo.nombre) },
                            onClick = {
                                viewModel.setTipoId(tipo.id)
                                tipoExpanded = false
                            }
                        )
                    }
                }
            }

            OutlinedTextField(
                value = state.direccion,
                onValueChange = viewModel::setDireccion,
                label = { Text("Dirección") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = state.telefono,
                onValueChange = viewModel::setTelefono,
                label = { Text("Teléfono") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = state.descripcion,
                onValueChange = viewModel::setDescripcion,
                label = { Text("Descripción") },
                minLines = 2,
                modifier = Modifier.fillMaxWidth()
            )

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = state.horaApertura,
                    onValueChange = viewModel::setHoraApertura,
                    label = { Text("Hora apertura (HH:mm)") },
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = state.horaCierre,
                    onValueChange = viewModel::setHoraCierre,
                    label = { Text("Hora cierre (HH:mm)") },
                    modifier = Modifier.weight(1f)
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = state.latitud,
                    onValueChange = viewModel::setLatitud,
                    label = { Text("Latitud *") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = state.longitud,
                    onValueChange = viewModel::setLongitud,
                    label = { Text("Longitud *") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(Modifier.height(8.dp))

            Button(
                onClick = viewModel::guardar,
                enabled = !state.isSaving,
                modifier = Modifier.fillMaxWidth().height(48.dp)
            ) {
                if (state.isSaving) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White)
                } else {
                    Text(if (state.esEdicion) "Guardar cambios" else "Crear establecimiento")
                }
            }
        }
    }
}
