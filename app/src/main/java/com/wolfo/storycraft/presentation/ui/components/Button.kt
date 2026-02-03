package com.wolfo.storycraft.presentation.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wolfo.storycraft.presentation.theme.StoryCraftTheme

@Composable
fun AppButton(
    text: String,
    isPrimary: Boolean = false,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {

    val buttonModifier = if (isPrimary) {
        modifier
            .border(
                width = 2.dp,
                color = MaterialTheme.colorScheme.primary,
                shape = MaterialTheme.shapes.extraLarge
            )
            .background(
                color = Color.Transparent,
                shape = MaterialTheme.shapes.extraLarge
            )
    } else {
        modifier.background(
            color = MaterialTheme.colorScheme.surfaceVariant,
            shape = MaterialTheme.shapes.extraLarge
        )
    }

    Box(
        modifier = buttonModifier
            .clip(MaterialTheme.shapes.extraLarge)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(),
                onClick = onClick
            )
            .padding(horizontal = 32.dp, vertical = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = if (isPrimary) text.uppercase() else text,
            color = if (isPrimary) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = if (isPrimary) FontWeight.Bold else FontWeight.Medium,
            letterSpacing = if (isPrimary) 1.sp else 0.sp
        )
    }
}

@Preview
@Composable
private fun StoryCraftButtonPreview() {
    StoryCraftTheme {
        AppButton(text = "Читать", onClick = {})
    }
}