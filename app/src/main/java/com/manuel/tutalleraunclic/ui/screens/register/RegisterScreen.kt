package com.manuel.tutalleraunclic.ui.screens.register

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.manuel.tutalleraunclic.R
import com.manuel.tutalleraunclic.viewmodel.RegisterViewModel
import com.manuel.tutalleraunclic.viewmodel.UiEvent

private val Azul        = Color(0xFF2563EB)
private val FondoBlanco = Color(0xFFFFFFFF)
private val GrisBorde   = Color(0xFFD1D5DB)
private val GrisLabel   = Color(0xFF6B7280)
private val GrisFondo   = Color(0xFFF9FAFB)

@Composable
fun RegisterScreen(
    onNavigateToHome: () -> Unit,
    onNavigateToEmpresa: () -> Unit,
    onNavigateToLogin: () -> Unit,
    viewModel: RegisterViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    var nombre    by remember { mutableStateOf("") }
    var apellido  by remember { mutableStateOf("") }
    var username  by remember { mutableStateOf("") }
    var email     by remember { mutableStateOf("") }
    var telefono  by remember { mutableStateOf("") }
    var password  by remember { mutableStateOf("") }
    var confirmar by remember { mutableStateOf("") }
    var verPass         by remember { mutableStateOf(false) }
    var verConf         by remember { mutableStateOf(false) }
    var passwordError   by remember { mutableStateOf(false) }

    var rolSeleccionado by remember { mutableStateOf("cliente") }

    LaunchedEffect(Unit) {
        viewModel.uiEvent.collect { event ->
            when (event) {
                is UiEvent.ShowError   -> snackbarHostState.showSnackbar(event.message)
                is UiEvent.ShowMessage -> snackbarHostState.showSnackbar(event.message)
            }
        }
    }

    LaunchedEffect(state.success) {
        if (state.success) {
            if (state.rolRegistrado == "empresa") onNavigateToEmpresa()
            else onNavigateToHome()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = FondoBlanco
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(FondoBlanco)
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // ── Logo ──────────────────────────────────────────────────────────
            Image(
                painter = painterResource(id = R.drawable.logo_solo),
                contentDescription = "Tu Taller a un Clic",
                modifier = Modifier.size(72.dp)
            )

            // ── Títulos ───────────────────────────────────────────────────────
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "Crear cuenta",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF111827)
                )
                Text(
                    text = "Regístrate en Tu Taller a un Clic",
                    fontSize = 14.sp,
                    color = GrisLabel,
                    textAlign = TextAlign.Center
                )
            }

            // ── Selección de rol ──────────────────────────────────────────────
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                RolCard(
                    titulo = "Cliente",
                    descripcion = "Busco talleres y agendo citas",
                    seleccionado = rolSeleccionado == "cliente",
                    onClick = { rolSeleccionado = "cliente" },
                    modifier = Modifier.weight(1f)
                )
                RolCard(
                    titulo = "Empresa",
                    descripcion = "Tengo un taller y ofrezco servicios",
                    seleccionado = rolSeleccionado == "empresa",
                    onClick = { rolSeleccionado = "empresa" },
                    modifier = Modifier.weight(1f)
                )
            }

            // ── Nombre + Apellido ─────────────────────────────────────────────
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                CampoTexto(
                    value = nombre,
                    onValueChange = { nombre = it },
                    label = "Nombre",
                    modifier = Modifier.weight(1f)
                )
                CampoTexto(
                    value = apellido,
                    onValueChange = { apellido = it },
                    label = "Apellido",
                    modifier = Modifier.weight(1f)
                )
            }

            // ── Usuario ───────────────────────────────────────────────────────
            CampoTexto(
                value = username,
                onValueChange = { username = it },
                label = "Usuario",
                modifier = Modifier.fillMaxWidth()
            )

            // ── Correo ────────────────────────────────────────────────────────
            CampoTexto(
                value = email,
                onValueChange = { email = it },
                label = "Correo electrónico",
                keyboardType = KeyboardType.Email,
                modifier = Modifier.fillMaxWidth()
            )

            // ── Teléfono ──────────────────────────────────────────────────────
            CampoTexto(
                value = telefono,
                onValueChange = { telefono = it },
                label = "Teléfono",
                keyboardType = KeyboardType.Phone,
                modifier = Modifier.fillMaxWidth()
            )

            // ── Contraseña + Confirmar ────────────────────────────────────────
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                CampoPassword(
                    value = password,
                    onValueChange = { password = it },
                    label = "Contraseña",
                    visible = verPass,
                    onToggle = { verPass = !verPass },
                    modifier = Modifier.weight(1f)
                )
                CampoPassword(
                    value = confirmar,
                    onValueChange = { confirmar = it },
                    label = "Confirmar",
                    visible = verConf,
                    onToggle = { verConf = !verConf },
                    modifier = Modifier.weight(1f)
                )
            }

            if (passwordError) {
                Text(
                    text = "Las contraseñas no coinciden",
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 12.sp,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // ── Botón ─────────────────────────────────────────────────────────
            Button(
                onClick = {
                    if (password != confirmar) {
                        passwordError = true
                        return@Button
                    }
                    passwordError = false
                    viewModel.register(
                        username  = username,
                        email     = email,
                        password  = password,
                        rol       = rolSeleccionado,
                        firstName = nombre,
                        lastName  = apellido,
                        telefono  = telefono
                    )
                },
                enabled = !state.isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Azul)
            ) {
                if (state.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp,
                        color = Color.White
                    )
                } else {
                    Text("Crear cuenta", fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
                }
            }

            // ── Link login ────────────────────────────────────────────────────
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("¿Ya tienes cuenta? ", fontSize = 14.sp, color = GrisLabel)
                Text(
                    text = "Inicia sesión",
                    fontSize = 14.sp,
                    color = Azul,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.clickable { onNavigateToLogin() }
                )
            }
        }
    }
}

// ── Componentes privados ───────────────────────────────────────────────────────

@Composable
private fun RolCard(
    titulo: String,
    descripcion: String,
    seleccionado: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val borderColor = if (seleccionado) Azul else GrisBorde
    val borderWidth = if (seleccionado) 2.dp else 1.dp
    val fondoColor  = if (seleccionado) Color(0xFFEFF6FF) else FondoBlanco

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(fondoColor)
            .border(BorderStroke(borderWidth, borderColor), RoundedCornerShape(10.dp))
            .clickable { onClick() }
            .padding(12.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = titulo,
                fontWeight = if (seleccionado) FontWeight.Bold else FontWeight.SemiBold,
                fontSize = 14.sp,
                color = if (seleccionado) Azul else Color(0xFF111827)
            )
            Text(
                text = descripcion,
                fontSize = 11.sp,
                color = GrisLabel,
                textAlign = TextAlign.Center,
                lineHeight = 14.sp
            )
        }
    }
}

@Composable
private fun CampoTexto(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(label, fontSize = 13.sp, color = GrisLabel, fontWeight = FontWeight.Medium)
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType, autoCorrect = false),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = GrisBorde,
                focusedBorderColor = Azul,
                unfocusedContainerColor = GrisFondo,
                focusedContainerColor = FondoBlanco
            )
        )
    }
}

@Composable
private fun CampoPassword(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    visible: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(label, fontSize = 13.sp, color = GrisLabel, fontWeight = FontWeight.Medium)
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            visualTransformation = if (visible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, autoCorrect = false),
            trailingIcon = {
                IconButton(onClick = onToggle) {
                    Icon(
                        imageVector = if (visible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp),
                        tint = GrisLabel
                    )
                }
            },
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = GrisBorde,
                focusedBorderColor = Azul,
                unfocusedContainerColor = GrisFondo,
                focusedContainerColor = FondoBlanco
            )
        )
    }
}
