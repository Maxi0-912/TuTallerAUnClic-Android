package com.manuel.tutalleraunclic.ui.screens.login

import androidx.activity.compose.BackHandler
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.manuel.tutalleraunclic.R
import com.manuel.tutalleraunclic.viewmodel.LoginUiState
import com.manuel.tutalleraunclic.viewmodel.LoginViewModel

/**
 * Comparte la MISMA instancia de LoginViewModel que LoginScreen (scope de
 * Activity, ver AppRoot) para reenviar el idToken guardado ahí junto con el
 * rol elegido — nunca viaja por argumentos de navegación.
 */
@Composable
fun SeleccionarRolScreen(
    viewModel: LoginViewModel,
    onRolSeleccionado: (String) -> Unit,
    onCancelar: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val isLoading = uiState is LoginUiState.Loading

    // Se captura una sola vez al entrar: mientras se envía el rol, uiState
    // pasa por Loading/Error y perdería el nombre/foto si se leyera en vivo.
    val datosGoogle = remember {
        uiState as? LoginUiState.NeedsRolSelection ?: LoginUiState.NeedsRolSelection(null, null)
    }

    var rolSeleccionado by remember { mutableStateOf<Int?>(null) }

    BackHandler(onBack = onCancelar)

    LaunchedEffect(uiState) {
        val estado = uiState
        if (estado is LoginUiState.Success) onRolSeleccionado(estado.rolNombre)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (!datosGoogle.foto.isNullOrBlank()) {
                AsyncImage(
                    model = datosGoogle.foto,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.size(72.dp).clip(CircleShape)
                )
            } else {
                Image(
                    painter = painterResource(id = R.drawable.logo_solo),
                    contentDescription = null,
                    modifier = Modifier.size(64.dp)
                )
            }

            Spacer(Modifier.height(24.dp))

            Text(
                text = if (!datosGoogle.nombre.isNullOrBlank()) {
                    "¡Hola, ${datosGoogle.nombre}!"
                } else {
                    "¿Cómo querés usar TuTaller?"
                },
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(Modifier.height(8.dp))

            Text(
                text = "Elegí tu perfil para continuar",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(32.dp))

            RolCard(
                icon = Icons.Default.DirectionsCar,
                title = "Soy Cliente",
                description = "Busco talleres y lavaderos, agendo citas y califico servicios",
                selected = rolSeleccionado == 2,
                enabled = !isLoading,
                onClick = { rolSeleccionado = 2 }
            )

            Spacer(Modifier.height(16.dp))

            RolCard(
                icon = Icons.Default.Build,
                title = "Soy Empresa",
                description = "Tengo un taller o lavadero y quiero recibir clientes",
                selected = rolSeleccionado == 3,
                enabled = !isLoading,
                onClick = { rolSeleccionado = 3 }
            )

            Spacer(Modifier.height(28.dp))

            Button(
                onClick = { rolSeleccionado?.let(viewModel::asignarRol) },
                enabled = rolSeleccionado != null && !isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(14.dp)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(22.dp),
                        strokeWidth = 2.5.dp,
                        color = Color.White
                    )
                } else {
                    Text("Continuar", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                }
            }

            if (uiState is LoginUiState.Error) {
                Spacer(Modifier.height(12.dp))
                Text(
                    text = (uiState as LoginUiState.Error).message,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.error,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(Modifier.height(40.dp))
        }
    }
}

@Composable
private fun RolCard(
    icon: ImageVector,
    title: String,
    description: String,
    selected: Boolean,
    enabled: Boolean,
    onClick: () -> Unit
) {
    val borderColor by animateColorAsState(
        targetValue = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant,
        label = "rolCardBorder"
    )
    val containerColor by animateColorAsState(
        targetValue = if (selected)
            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f)
        else
            MaterialTheme.colorScheme.surface,
        label = "rolCardContainer"
    )
    val elevation by animateDpAsState(
        targetValue = if (selected) 6.dp else 1.dp,
        label = "rolCardElevation"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .clickable(enabled = enabled, onClick = onClick),
        shape = RoundedCornerShape(18.dp),
        border = BorderStroke(2.dp, borderColor),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        elevation = CardDefaults.cardElevation(defaultElevation = elevation)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(
                        if (selected) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.surfaceVariant
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (selected) MaterialTheme.colorScheme.onPrimary
                           else MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(26.dp)
                )
            }

            Spacer(Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = description,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.65f)
                )
            }
        }
    }
}
