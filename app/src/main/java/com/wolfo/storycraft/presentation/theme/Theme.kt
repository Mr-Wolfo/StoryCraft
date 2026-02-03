package com.wolfo.storycraft.presentation.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import com.google.accompanist.systemuicontroller.rememberSystemUiController

private val DarkColorScheme = darkColorScheme(
    primary = NeonGreen,
    onPrimary = Color.Black,
    primaryContainer = DarkSurface,
    onPrimaryContainer = Color.White,
    background = DarkBackground,
    onBackground = Color.White,
    surface = DarkSurface,
    onSurface = Color.White,
    surfaceVariant = DarkVariant,
    onSurfaceVariant = Color(0xFFE0E5FC),
    outline = Color.White.copy(alpha = 0.5f),
    outlineVariant = Color.White.copy(alpha = 0.1f)
)

private val LightColorScheme = lightColorScheme(
    primary = NeonGreen,
    onPrimary = Color.White,
    primaryContainer = LightSurface,
    onPrimaryContainer = Color.Black,
    background = LightBackground,
    onBackground = Color(0xFF1B1B1F),
    surface = LightSurface,
    onSurface = Color(0xFF1B1B1F),
    surfaceVariant = LightVariant,
    onSurfaceVariant = Color(0xFF48484D),
    outline = Color.Black.copy(alpha = 0.12f)
)

val MaterialTheme.extendedColors: ExtendedColors
    @Composable
    @ReadOnlyComposable
    get() = LocalExtendedColors.current

val ExtendedLightColors = ExtendedColors(
    star = StarLemon,
    success = Color.Green,
    info = Color.Green,
    accentBlue = NeonBlue
)

val ExtendedDarkColors = ExtendedColors(
    star = StarGold,
    success = Color.Green,
    info = Color.Green,
    accentBlue = NeonBlue
)

data class ExtendedColors(
    val star: Color,
    val success: Color,
    val info: Color,
    val accentBlue: Color
)

val LocalExtendedColors = staticCompositionLocalOf {
    ExtendedColors(
        star = Color.Unspecified,
        success = Color.Unspecified,
        info = Color.Unspecified,
        accentBlue = Color.Unspecified
    )
}

@Composable
fun StoryCraftTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    val extendedColors = if (darkTheme) ExtendedDarkColors else ExtendedLightColors
    val localSpacing = LocalSpacing

    val systemUiController = rememberSystemUiController()
    systemUiController.setSystemBarsColor(
        color = Color.Transparent
    )

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            WindowCompat.setDecorFitsSystemWindows(window, false)
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
            WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        shapes = AppShapes
    ) {
        CompositionLocalProvider(
            LocalExtendedColors provides extendedColors,
            LocalSpacing provides Spacing(),
            content = content
        )
    }
}