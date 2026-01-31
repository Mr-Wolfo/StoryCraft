package com.wolfo.storycraft.presentation.ui.features.story_view.details.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.wolfo.storycraft.presentation.theme.StoryCraftTheme
import com.wolfo.storycraft.presentation.theme.spacing
import com.wolfo.storycraft.presentation.ui.components.AppCard
import com.wolfo.storycraft.presentation.ui.components.StoryCraftButton

@Composable
fun StoryActions(
    isFavorite: Boolean,
    onReadClick: () -> Unit,
    onFavoriteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    AppCard {
        Row(
            modifier = modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.medium)
        ) {
            StoryCraftButton(
                text = "Читать",
                onClick = onReadClick,
                modifier = Modifier.weight(1f)
            )

            IconButton(
                onClick = onFavoriteClick,
                modifier = Modifier
                    .size(56.dp)
            ) {
                Icon(
                    imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                    contentDescription = "В избранное",
                    tint = if (isFavorite) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF1C1936)
@Composable
private fun StoryActionsPreview() {
    StoryCraftTheme {
        StoryActions(isFavorite = true, onReadClick = {}, onFavoriteClick = {}, modifier = Modifier.padding(16.dp))
    }
}