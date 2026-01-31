package com.wolfo.storycraft.presentation.ui.features.story_view.details.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage

@Composable
fun ImageBackgroundScreen(
    modifier: Modifier = Modifier,
    backgroundUrl: String?,
    content: @Composable BoxScope.() -> Unit = { }
) {
    val backgroundColor = MaterialTheme.colorScheme.background

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
            )
        }
        content()
    }
}