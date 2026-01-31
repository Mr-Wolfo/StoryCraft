package com.wolfo.storycraft.presentation.ui.features.story_view.reader.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.wolfo.storycraft.presentation.theme.spacing

@Composable
fun ChoiceButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val elevation by animateDpAsState(
        targetValue = if (isPressed) 1.dp else 2.dp,
        animationSpec = tween(durationMillis = 100)
    )

    ElevatedCard(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 56.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.elevatedCardElevation(
            defaultElevation = elevation,
            pressedElevation = 1.dp
        ),
        shape = MaterialTheme.shapes.extraLarge,
        interactionSource = interactionSource
    ) {
        Row(modifier = Modifier.padding(horizontal = MaterialTheme.spacing.medium),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween) {
            Text(
                text = text,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(MaterialTheme.spacing.medium)
            )
        }
    }
}