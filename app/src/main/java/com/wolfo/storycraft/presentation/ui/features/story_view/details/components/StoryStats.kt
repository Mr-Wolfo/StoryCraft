package com.wolfo.storycraft.presentation.ui.features.story_view.details.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.wolfo.storycraft.presentation.common.formatNumber
import com.wolfo.storycraft.presentation.theme.StoryCraftTheme
import com.wolfo.storycraft.presentation.theme.spacing
import com.wolfo.storycraft.presentation.ui.components.AppCard

@Composable
fun StoryStats(
    pageCount: Int,
    viewCount: Int,
    rating: Float,
    modifier: Modifier = Modifier
) {
    AppCard {
        Row(
            modifier = modifier.fillMaxWidth().padding(MaterialTheme.spacing.extraSmall),
            horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.extraSmall)
        ) {
            StatItem(
                value = "%.1f".format(rating),
                label = "Рейтинг",
                modifier = Modifier.weight(1f)
            )
            StatItem(
                value = formatNumber(viewCount),
                label = "Просмотров",
                modifier = Modifier.weight(1f)
            )
            StatItem(
                value = pageCount.toString(),
                label = "Страниц",
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun StatItem(
    value: String,
    label: String,
    modifier: Modifier = Modifier
) {
    AppCard(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.surfaceVariant,
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = modifier
                .padding(vertical = MaterialTheme.spacing.extraSmall),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.extraSmall)
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}


@Preview(showBackground = true, backgroundColor = 0xFF1C1936)
@Composable
private fun StoryStatsPreview() {
    StoryCraftTheme {
        StoryStats(
            pageCount = 128,
            viewCount = 135_789,
            rating = 4.9f,
            modifier = Modifier.padding(16.dp)
        )
    }
}
