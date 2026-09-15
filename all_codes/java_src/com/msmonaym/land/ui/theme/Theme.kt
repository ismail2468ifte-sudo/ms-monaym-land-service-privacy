package com.msmonaym.land.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary = LandGreenPrimary,
    onPrimary = Color.White,
    primaryContainer = LandGreenContainer,
    onPrimaryContainer = LandGreenOnContainer,
    secondary = LandGreenLight,
    onSecondary = Color.White,
    tertiary = GoldAccent,
    onTertiary = Color.Black,
    background = SurfaceLight,
    onBackground = TextPrimary,
    surface = CardBackground,
    onSurface = TextPrimary,
    surfaceVariant = LandGreenContainer.copy(alpha = 0.5f),
    onSurfaceVariant = TextSecondary,
    outline = DividerColor
)

private val DarkColorScheme = darkColorScheme(
    primary = LandGreenLight,
    onPrimary = Color.White,
    primaryContainer = LandGreenDark,
    onPrimaryContainer = Color.White,
    secondary = GoldAccent,
    onSecondary = Color.Black,
    background = Color(0xFF101C14),
    onBackground = Color(0xFFE2E8E3),
    surface = Color(0xFF18261C),
    onSurface = Color(0xFFE2E8E3),
    surfaceVariant = Color(0xFF223427),
    onSurfaceVariant = Color(0xFFB5C4B7),
    outline = Color(0xFF384D3E)
)

@Composable
fun MSMonaymTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = LandGreenPrimary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}
