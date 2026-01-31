package com.wolfo.storycraft.presentation.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.wolfo.storycraft.presentation.theme.StoryCraftTheme

@Composable
fun AppCard(
    modifier: Modifier = Modifier,
    containerColor: Color = colorScheme.primaryContainer,
    containerAlpha: Float = 1f,
    contentAlignment: Alignment = Alignment.TopStart,
    content: @Composable () -> Unit
) {
    Card(
        modifier = modifier,
        shape = MaterialTheme.shapes.extraLarge,
        colors = CardDefaults.cardColors(
            containerColor = containerColor.copy(alpha = containerAlpha)
        )
    ) {
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = contentAlignment
        ) {
            content()
        }
    }
}

@Preview
@Composable
private fun StoryCraftCardPreview() {
    StoryCraftTheme {
        AppCard {
            Text("This is a Card")
        }
    }
}