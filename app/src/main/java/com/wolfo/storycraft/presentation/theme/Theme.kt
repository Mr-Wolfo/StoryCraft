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
import com.wolfo.storycraft.presentation.theme.LightPeriwinkle

private val DarkColorScheme = darkColorScheme(
    primary = Black,
    onPrimary = DarkGray,
    primaryContainer = DarkGray,
    onPrimaryContainer = Gray,
    secondary = NeonGreen,
    onSecondary = TextOnGradient,
    background = Black,
    onBackground = LightPeriwinkle,
    surface = DarkGray,
    onSurface = LightPeriwinkle,
    surfaceVariant = DarkGray.copy(alpha = 0.5f),
    onSurfaceVariant = SlateGray,
    outline = LightPeriwinkle,
    outlineVariant = Color.White
)

private val LightColorScheme = lightColorScheme(
    primary = LightPeriwinkle,
    onPrimary = Color.White,
    primaryContainer = Color.White,
    onPrimaryContainer = DarkLight,
    secondary = Color(0xFF5A5D72),
    onSecondary = Color.White,
    background = LightBackground,
    onBackground = DarkText,
    surface = Color.White,
    onSurface = DarkText,
    surfaceVariant = LightPeriwinkle.copy(alpha = 0.5f),
    onSurfaceVariant = MediumText,
    outline = Color.Black,
    outlineVariant = Color.DarkGray
)

val MaterialTheme.extendedColors: ExtendedColors
    @Composable
    @ReadOnlyComposable
    get() = LocalExtendedColors.current


data class ExtendedColors(
    val star: Color,
    val main: Color,
    val oppositeMain: Color,
    val onDarkBackground: Color,
    val mainBlue: Color,
    val mainYellow: Color,
    val mainCyan: Color,
    val mainLightGreen: Color
)

val ExtendedLightColors = ExtendedColors(
    star = StarLight,
    main = LightPeriwinkle,
    oppositeMain = DarkGray,
    onDarkBackground = DarkText,
    mainBlue = NeonBlue,
    mainYellow = NeonYellow,
    mainCyan = NeonBlue,
    mainLightGreen = NeonGreen
)

val ExtendedDarkColors = ExtendedColors(
    star = StarDark,
    main = DarkGray,
    oppositeMain = LightPeriwinkle,
    onDarkBackground = LightPeriwinkle,
    mainBlue = NeonBlue,
    mainYellow = NeonYellow,
    mainCyan = NeonBlue,
    mainLightGreen = NeonGreen
)

private val LocalExtendedColors = staticCompositionLocalOf {
    ExtendedColors(
        star = Color.Unspecified,
        main = Color.Unspecified,
        oppositeMain = Color.Unspecified,
        onDarkBackground = Color.Unspecified,
        mainBlue = Color.Unspecified,
        mainYellow = Color.Unspecified,
        mainCyan = Color.Unspecified,
        mainLightGreen = Color.Unspecified
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
            content = content
        )
    }
}