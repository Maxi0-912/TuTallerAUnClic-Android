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
    surfaceVariant     = Color(0xFFFFFFFF),
    outline            = Color(0xFFE2E8F0),
    onPrimary          = Color.White,
    onSecondary        = Color.White,
    onTertiary         = Color.Black,
    onBackground       = TextPrimary,
    onSurface          = TextPrimary,
    onSurfaceVariant   = TextSecondary,
    error              = Color(0xFFDC2626),
)

private val DarkColorScheme = darkColorScheme(
    primary            = PrimaryBlue,
    secondary          = PrimaryPurple,
    tertiary           = DarkTertiary,
    background         = AppBackgroundDark,
    surface            = AppSurfaceDark,
    surfaceVariant     = Color(0xFF252540),
    outline            = AppBorderDark,
    onPrimary          = Color.White,
    onSecondary        = Color.White,
    onTertiary         = Color.White,
    onBackground       = AppTextPrimary,
    onSurface          = AppTextPrimary,
    onSurfaceVariant   = AppTextSecondary,
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
