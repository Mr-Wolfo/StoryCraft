package com.wolfo.storycraft.presentation.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.wolfo.storycraft.presentation.theme.NeonBlue
import com.wolfo.storycraft.presentation.theme.NeonGreen
import com.wolfo.storycraft.presentation.theme.NeonYellow

@Composable
fun AuroraBackground(
    modifier: Modifier = Modifier,
    backgroundUrl: String?,
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF0D081A)) // Глубокий космический фон
    ) {
        // Размытое фоновое изображение истории
        if (!backgroundUrl.isNullOrBlank()) {
            AsyncImage(
                model = backgroundUrl,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .blur(150.dp) // Очень сильное размытие
            )
        }

        // Цветные "облака" для эффекта авроры
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.radialGradient(
                        colors = listOf(NeonYellow.copy(0.15f), Color.Transparent),
                        center = Offset(100f, 200f),
                        radius = 800f
                    )
                )
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.radialGradient(
                        colors = listOf(NeonBlue.copy(0.15f), Color.Transparent),
                        center = Offset(800f, 1500f),
                        radius = 900f
                    )
                )
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.radialGradient(
                        colors = listOf(NeonGreen.copy(0.1f), Color.Transparent),
                        center = Offset(200f, 2500f),
                        radius = 700f
                    )
                )
        )

        content()
    }
}