package com.wolfo.storycraft.presentation.ui.features.story_view.details.components

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.BlurredEdgeTreatment
import androidx.compose.ui.draw.blur
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage

@Composable
fun DetailsScreenBackground(
    modifier: Modifier = Modifier,
    backgroundUrl: String?,
    content: @Composable BoxScope.() -> Unit = { }
) {
    val backgroundColor = MaterialTheme.colorScheme.background
    val isDarkTheme = isSystemInDarkTheme()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(backgroundColor)
    ) {
        if (!backgroundUrl.isNullOrBlank()) {
            AsyncImage(
                model = backgroundUrl,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .blur(radius = 0.dp)
            )
        }

       /* if (isDarkTheme) {
            AuroraEffect(
                colors = listOf(
                    NeonYellow.copy(alpha = 0.30f),
                    NeonBlue.copy(alpha = 0.30f),
                    NeonGreen.copy(alpha = 0.2f)
                ),
                positions = listOf(
                    Offset(100f, 200f) to 800f,
                    Offset(800f, 1500f) to 900f,
                    Offset(200f, 2500f) to 700f
                )
            )
        } else {
            AuroraEffect(
                colors = listOf(
                    PastelPink.copy(alpha = 0.35f),
                    SkyBlue.copy(alpha = 0.4f),
                    SoftYellow.copy(alpha = 0.3f)
                ),
                positions = listOf(
                    Offset(100f, 200f) to 900f,
                    Offset(900f, 1400f) to 1000f,
                    Offset(200f, 2400f) to 800f
                )
            )
        }*/

        content()
    }
}

@Composable
private fun AuroraEffect(
    colors: List<Color>,
    positions: List<Pair<Offset, Float>> // Pair<Center, Radius>
) {
    positions.forEachIndexed { index, (center, radius) ->
        val color = colors.getOrElse(index) { Color.Transparent }
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.radialGradient(
                        colors = listOf(color, Color.Transparent),
                        center = center,
                        radius = radius
                    )
                )
        )
    }
}