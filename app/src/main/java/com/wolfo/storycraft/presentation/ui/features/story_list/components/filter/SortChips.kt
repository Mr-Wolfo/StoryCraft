package com.wolfo.storycraft.presentation.ui.features.story_list.components.filter

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun SortChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    StoryFilterChip(
        label = label,
        selected = selected,
        onClick = onClick,
        modifier = modifier
    )
}

@Composable
fun SortOrderChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    StoryFilterChip(
        label = label,
        selected = selected,
        onClick = onClick,
        modifier = modifier
    )
}