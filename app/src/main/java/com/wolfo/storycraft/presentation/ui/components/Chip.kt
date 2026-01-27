package com.wolfo.storycraft.presentation.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.wolfo.storycraft.presentation.theme.StoryCraftTheme
import com.wolfo.storycraft.presentation.ui.utils.glass
import dev.chrisbanes.haze.rememberHazeState


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StoryCraftChip(
    text: String,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    Surface(
        onClick = onClick,
        modifier = modifier,
        shape = MaterialTheme.shapes.small,
        color = Color.Transparent, // Сам Surface прозрачный
        contentColor = MaterialTheme.colorScheme.onSurfaceVariant
    ) {
        Box(
            modifier = Modifier
                .padding(horizontal = 12.dp, vertical = 6.dp)
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.labelLarge,
            )
        }
    }
}
@Preview
@Composable
private fun StoryCraftChipPreview() {
    StoryCraftTheme {
        StoryCraftChip(text = "Фэнтези", isSelected = false, onClick = {})
    }
}

@Preview
@Composable
private fun StoryCraftChipSelectedPreview() {
    StoryCraftTheme {
        StoryCraftChip(text = "Хоррор", isSelected = true, onClick = {})
    }
}