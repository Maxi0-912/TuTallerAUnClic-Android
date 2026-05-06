package com.manuel.tutalleraunclic.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.manuel.tutalleraunclic.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun SplashScreen(onFinish: () -> Unit) {

    val alpha = remember { Animatable(0f) }
    val scale = remember { Animatable(0.6f) }

    LaunchedEffect(Unit) {
        // Fade-in + scale simultáneos
        launch {
            alpha.animateTo(
                targetValue = 1f,
                animationSpec = tween(800, easing = EaseOut)
            )
        }
        scale.animateTo(
            targetValue = 1f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness    = Spring.StiffnessLow
            )
        )
        delay(800)
        onFinish()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF0B1120),
                        Color(0xFF0F2040),
                        Color(0xFF0B1120),
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp),
            modifier = Modifier
                .alpha(alpha.value)
                .scale(scale.value)
        ) {
            Image(
                painter = painterResource(id = R.drawable.logo_solo),
                contentDescription = "Logo",
                modifier = Modifier.size(140.dp)
            )

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "TuTaller",
                    color = Color(0xFF60A5FA),
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "A Un Clic",
                    color = Color(0xFFCBD5E1),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Light,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
