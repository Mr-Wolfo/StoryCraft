package com.wolfo.storycraft.presentation.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.HazeStyle
import dev.chrisbanes.haze.HazeTint
import dev.chrisbanes.haze.hazeEffect

@Composable
fun PremiumBadge(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .padding(8.dp)
            .background(
                color = MaterialTheme.colorScheme.primary,
                shape = CircleShape
            )
            .size(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Default.Star,
            contentDescription = "Премиум",
            tint = MaterialTheme.colorScheme.onPrimary,
            modifier = Modifier.size(16.dp)
        )
    }
}

@Composable
fun AutoPaddingBox(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier.fillMaxSize()
            .windowInsetsPadding(WindowInsets.statusBars)
            .windowInsetsPadding(WindowInsets.navigationBars)
    ) {
        content()
    }
}


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