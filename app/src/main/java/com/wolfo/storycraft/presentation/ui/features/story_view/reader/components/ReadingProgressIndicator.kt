package com.wolfo.storycraft.presentation.ui.features.story_view.reader.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ReadingProgressIndicator(
    progress: Float,
    modifier: Modifier = Modifier
) {
    LinearProgressIndicator(
        progress = { progress },
        modifier = modifier
            .fillMaxWidth()
            .height(4.dp),
        color = MaterialTheme.colorScheme.tertiary,
        trackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
    )
}
