package com.manuel.tutalleraunclic.ui.theme

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary            = Primary,
    secondary          = Secondary,
    tertiary           = Accent,
    background         = Background,
    surface            = Surface,
    surfaceVariant     = Color(0xFFE8EEF7),
    outline            = Color(0xFFCBD5E1),
    onPrimary          = Color.White,
    onSecondary        = Color.White,
    onTertiary         = Color.Black,
    onBackground       = TextPrimary,
    onSurface          = TextPrimary,
    onSurfaceVariant   = TextSecondary,
    error              = Color(0xFFDC2626),
)

private val DarkColorScheme = darkColorScheme(
    primary            = DarkPrimary,
    secondary          = DarkSecondary,
    tertiary           = DarkTertiary,
    background         = DarkBackground,
    surface            = DarkSurface,
    surfaceVariant     = DarkSurfaceVariant,
    outline            = DarkOutline,
    onPrimary          = Color(0xFF0B1120),
    onSecondary        = Color(0xFF0B1120),
    onTertiary         = Color(0xFF0B1120),
    onBackground       = DarkOnBackground,
    onSurface          = DarkOnSurface,
    onSurfaceVariant   = Color(0xFF94A3B8),
    error              = Color(0xFFF87171),
)

// CompositionLocals para acceder al toggle desde cualquier composable
val LocalIsDarkMode   = compositionLocalOf { false }
val LocalToggleTheme  = compositionLocalOf<() -> Unit> { {} }

@Composable
fun TuTallerAUnClicTheme(
    darkTheme: Boolean = false,
    onToggleTheme: () -> Unit = {},
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    CompositionLocalProvider(
        LocalIsDarkMode  provides darkTheme,
        LocalToggleTheme provides onToggleTheme,
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography  = Typography,
            content     = content
        )
    }
}
