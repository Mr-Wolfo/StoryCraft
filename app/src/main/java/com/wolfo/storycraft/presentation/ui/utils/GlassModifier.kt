package com.wolfo.storycraft.presentation.ui.utils

import android.os.Build
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.wolfo.storycraft.presentation.theme.Black
import com.wolfo.storycraft.presentation.theme.MediumText
import com.wolfo.storycraft.presentation.theme.NeonBlue
import com.wolfo.storycraft.presentation.theme.NeonGreen
import com.wolfo.storycraft.presentation.theme.NeonYellow
import com.wolfo.storycraft.presentation.theme.extendedColors
import dev.chrisbanes.haze.HazeDefaults
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.HazeStyle
import dev.chrisbanes.haze.HazeTint
import dev.chrisbanes.haze.hazeChild
import dev.chrisbanes.haze.hazeEffect

/**
 * Кастомный модификатор для создания "стеклянного" эффекта с использованием
 * библиотеки Haze.
 *
 * @param hazeState Состояние, управляющее размытием.
 * @param shape Форма элемента для корректной обрезки размытия.
 * @param backgroundColor Цвет подложки элемента, который будет использоваться для тонирования.
 * @param blurRadius Радиус размытия.
 * @param borderWidth Ширина рамки.
 */
@Composable
fun Modifier.glass(
    hazeState: HazeState? = null,
    shape: Shape = MaterialTheme.shapes.large,
    backgroundColor: Color = MaterialTheme.colorScheme.surface,
    blurRadius: Dp = 10.dp,
    borderWidth: Dp = 1.5.dp,
): Modifier {
    val borderBrush = Brush.verticalGradient(
        colors = listOf(
            MaterialTheme.extendedColors.main.copy(alpha = 1f),
            MaterialTheme.extendedColors.main.copy(alpha = 0.7f)
        )
    )

    return this
        .clip(shape)
        .hazeEffect(
            state = hazeState,
            style = HazeStyle(
                blurRadius = blurRadius,
                tint = HazeTint(color = backgroundColor),
                noiseFactor = 0f
            )
        )
}