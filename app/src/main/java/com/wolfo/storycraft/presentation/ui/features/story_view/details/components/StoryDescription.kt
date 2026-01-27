package com.wolfo.storycraft.presentation.ui.features.story_view.details.components

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wolfo.storycraft.presentation.common.GlassCard
import com.wolfo.storycraft.presentation.theme.extendedColors
import com.wolfo.storycraft.presentation.ui.utils.glass
import dev.chrisbanes.haze.HazeState

@Composable
fun StoryDescription(
    description: String,
    modifier: Modifier = Modifier
) {
    var isExpanded by remember { mutableStateOf(false) }
    val canBeExpanded = description.length > 200

    GlassCard {
        Column(
            modifier = modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 20.dp)
                .animateContentSize()
        ) {
            Text(
                text = description,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 24.sp,
                maxLines = if (isExpanded) Int.MAX_VALUE else 5,
                overflow = TextOverflow.Ellipsis
            )
            if (canBeExpanded && !isExpanded) {
                TextButton(
                    onClick = { isExpanded = true },
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text("Читать далее...", color = MaterialTheme.extendedColors.oppositeMain)
                }
            }
        }
    }
}
