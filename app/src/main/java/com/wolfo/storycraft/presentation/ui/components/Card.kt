package com.wolfo.storycraft.presentation.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.wolfo.storycraft.presentation.theme.StoryCraftTheme

@Composable
fun StoryCraftCard(
    modifier: Modifier = Modifier,
    shape: Shape = MaterialTheme.shapes.large,
    borderStroke: BorderStroke? = BorderStroke(1.dp, Color.White.copy(alpha = 0.2f)),
    containerColor: Color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
    onClick: () -> Unit = {},
    content: @Composable (ColumnScope.() -> Unit)
) {
    Card(
        modifier = modifier,
        shape = shape,
        colors = CardDefaults.cardColors(
            containerColor = containerColor
        ),
        border = borderStroke,
        onClick = onClick,
        content = content
    )
}

@Preview
@Composable
private fun StoryCraftCardPreview() {
    StoryCraftTheme {
        StoryCraftCard {
            Text("This is a glassmorphism Card")
        }
    }
}