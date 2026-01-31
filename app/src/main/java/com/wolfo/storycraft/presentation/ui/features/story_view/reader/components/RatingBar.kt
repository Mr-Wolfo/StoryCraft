package com.wolfo.storycraft.presentation.ui.features.story_view.reader.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.wolfo.storycraft.presentation.theme.extendedColors
import kotlin.math.floor

@Composable
fun RatingBar(
    rating: Float,
    modifier: Modifier = Modifier
) {
    val filledStars = floor(rating).toInt()
    val hasHalfStar = rating - filledStars >= 0.5

    Row(modifier = modifier) {
        repeat(5) { index ->
            Icon(
                imageVector = when {
                    index < filledStars -> Icons.Filled.Star
                    index == filledStars && hasHalfStar -> Icons.Filled.Star
                    else -> Icons.Outlined.Star
                },
                contentDescription = null,
                tint = if (index < rating) MaterialTheme.extendedColors.star else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}