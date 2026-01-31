package com.wolfo.storycraft.presentation.ui.features.story_list.components.appbar

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun FilterToggleButton(
    filtersVisible: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    IconButton(
        onClick = onToggle,
        modifier = modifier.size(48.dp),
        colors = IconButtonDefaults.iconButtonColors(
            containerColor = if (filtersVisible) {
                MaterialTheme.colorScheme.primaryContainer
            } else {
                Color.Transparent
            }
        )
    ) {
        Icon(
            imageVector = Icons.Default.Menu,
            contentDescription = if (filtersVisible) {
                "Скрыть фильтры"
            } else {
                "Показать фильтры"
            },
            tint = if (filtersVisible) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.onPrimaryContainer
            }
        )
    }
}