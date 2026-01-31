package com.wolfo.storycraft.presentation.ui.features.story_view.details.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.wolfo.storycraft.presentation.ui.components.AppCard

@Composable
fun StoryImage(imageUrl: String?) {
    if (imageUrl != null) {
        AppCard {
            AsyncImage(
                model = imageUrl,
                contentDescription = null,
                contentScale = ContentScale.FillWidth,
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 350.dp)
            )
        }
    }
}