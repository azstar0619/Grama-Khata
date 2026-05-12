package com.example.myapplication.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColors = lightColorScheme(
    primary              = GreenMid,
    onPrimary            = Color.White,
    primaryContainer     = GreenContainer,
    onPrimaryContainer   = GreenOnContainer,
    secondary            = SaffronDark,
    onSecondary          = Color.White,
    secondaryContainer   = SaffronContainer,
    onSecondaryContainer = SaffronDark,
    tertiary             = Color(0xFF0277BD),
    background           = BgLight,
    onBackground         = Color(0xFF1A1C19),
    surface              = SurfaceLight,
    onSurface            = Color(0xFF1A1C19),
    surfaceVariant       = SurfaceVarLight,
    onSurfaceVariant     = Color(0xFF44483F),
    error                = Color(0xFFB3261E),
    outline              = Color(0xFF74796C),
)

private val DarkColors = darkColorScheme(
    primary              = GreenLight,
    onPrimary            = Color(0xFF003A0A),
    primaryContainer     = GreenDark,
    onPrimaryContainer   = GreenContainer,
    secondary            = SaffronLight,
    onSecondary          = Color(0xFF401B00),
    secondaryContainer   = SaffronDark,
    onSecondaryContainer = SaffronContainer,
    tertiary             = Color(0xFF4FC3F7),
    background           = BgDark,
    onBackground         = Color(0xFFE2E3DD),
    surface              = SurfaceDark,
    onSurface            = Color(0xFFE2E3DD),
    surfaceVariant       = SurfaceVarDark,
    onSurfaceVariant     = Color(0xFFC4C9BB),
    error                = Color(0xFFF2B8B5),
    outline              = Color(0xFF8E9285),
)

@Composable
fun GramaKhataTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColors else LightColors
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }
    MaterialTheme(
        colorScheme = colorScheme,
        typography = GramaKhataTypography,
        content = content
    )
}
