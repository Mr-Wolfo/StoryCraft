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
    primary = MainDark,
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFF424242),
    onPrimaryContainer = Color(0xFFE0E0E0),
    inversePrimary = MainWhite,

    secondary = MainWhite,
    onSecondary = Color(0xFF000000),
    secondaryContainer = Color(0xFFE0E0E0),
    onSecondaryContainer = Color(0xFF212121),

    tertiary = MainBlue,
    onTertiary = Color(0xFFFFFFFF),
    tertiaryContainer = Color(0xFF1E88E5),
    onTertiaryContainer = Color(0xFFBBDEFB),

    background = BackDarkGray,
    onBackground = Color(0xFFE0E0E0),
    surface = Color(0xFF121212),
    onSurface = Color(0xFFE0E0E0),
    surfaceVariant = Color(0xFF424242),
    onSurfaceVariant = Color(0xFFBDBDBD),

    inverseSurface = Color(0xFFE0E0E0),
    inverseOnSurface = Color(0xFF121212),

    error = Color(0xFFF44336),
    onError = Color(0xFFFFFFFF),
    errorContainer = Color(0xFFB71C1C),
    onErrorContainer = Color(0xFFFFCDD2),

    outline = Color(0xFF616161),
    outlineVariant = Color(0xFF424242),
    scrim = Color(0x99000000),

    surfaceBright = Color(0xFFFFFFFF),
    surfaceDim = Color(0xFF121212),
    surfaceContainer = Color(0xFF1E1E1E),
    surfaceContainerHigh = Color(0xFF2D2D2D),
    surfaceContainerHighest = Color(0xFF424242),
    surfaceContainerLow = Color(0xFF121212),
    surfaceContainerLowest = Color(0xFF000000),
)

private val LightColorScheme = lightColorScheme(
    primary = MainWhite,
    onPrimary = Color(0xFF000000),
    primaryContainer = Color(0xFFE0E0E0),
    onPrimaryContainer = Color(0xFF212121),
    inversePrimary = MainDark,

    secondary = MainDark,
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFF424242),
    onSecondaryContainer = Color(0xFFEEEEEE),

    tertiary = MainOrange,
    onTertiary = Color(0xFF000000),
    tertiaryContainer = Color(0xFFFFD180),
    onTertiaryContainer = Color(0xFF311300),

    background = BackLightGray,
    onBackground = Color(0xFF1E1E1E),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF2D2D2D),
    surfaceVariant = Color(0xFFEEEEEE),
    onSurfaceVariant = Color(0xFF424242),

    inverseSurface = Color(0xFF1E1E1E),
    inverseOnSurface = Color(0xFFFFFFFF),

    error = Color(0xFFD32F2F),
    onError = Color(0xFFFFFFFF),
    errorContainer = Color(0xFFFFCDD2),
    onErrorContainer = Color(0xFFB71C1C),

    outline = Color(0xFFBDBDBD),
    outlineVariant = Color(0xFFE0E0E0),
    scrim = Color(0x99000000),

    surfaceBright = Color(0xFFFFFFFF),
    surfaceDim = Color(0xFFF5F5F5),
    surfaceContainer = Color(0xFFEEEEEE),
    surfaceContainerHigh = Color(0xFFE0E0E0),
    surfaceContainerHighest = Color(0xFFFFFFFF),
    surfaceContainerLow = Color(0xFFF5F5F5),
    surfaceContainerLowest = Color(0xFFFFFFFF),
)

val MaterialTheme.extendedColors: ExtendedColors
    @Composable
    @ReadOnlyComposable
    get() = LocalExtendedColors.current

data class ExtendedColors(
    val star: Color,
    val onDarkBackground: Color
)

val ExtendedLightColors = ExtendedColors(
    star = StarLight,
    onDarkBackground = onDarkBackground
)

val ExtendedDarkColors = ExtendedColors(
    star = StarDark,
    onDarkBackground = onDarkBackground
)

private val LocalExtendedColors = staticCompositionLocalOf {
    ExtendedColors(
        star = Color.Unspecified,
        onDarkBackground = onDarkBackground
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

    val systemUiController = rememberSystemUiController()
    systemUiController.setSystemBarsColor(
        color = Color.Transparent
    )

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography
    ) {
        CompositionLocalProvider(
            LocalExtendedColors provides extendedColors,
            content = content
        )
    }
}