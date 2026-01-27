package com.wolfo.storycraft.presentation.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.wolfo.storycraft.presentation.theme.NeonBlue
import com.wolfo.storycraft.presentation.theme.NeonGreen
import com.wolfo.storycraft.presentation.theme.NeonYellow
import com.wolfo.storycraft.presentation.theme.StoryCraftTheme

@Composable
fun StoryCraftButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val gradientBrush = Brush.horizontalGradient(
        colors = listOf(NeonYellow, NeonGreen, NeonBlue)
    )

    Box(
        modifier = modifier
            .clip(MaterialTheme.shapes.large)
            .background(gradientBrush)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            )
            .padding(horizontal = 32.dp, vertical = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text.uppercase(),
            color = MaterialTheme.colorScheme.onPrimary,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold
        )
    }
}
@Preview
@Composable
private fun StoryCraftButtonPreview() {
    StoryCraftTheme {
        StoryCraftButton(text = "Читать", onClick = {})
    }
}