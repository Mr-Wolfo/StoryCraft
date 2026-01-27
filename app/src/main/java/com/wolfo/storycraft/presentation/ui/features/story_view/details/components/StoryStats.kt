package com.wolfo.storycraft.presentation.ui.features.story_view.details.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.wolfo.storycraft.presentation.common.GlassCard
import com.wolfo.storycraft.presentation.common.formatNumber
import com.wolfo.storycraft.presentation.theme.StoryCraftTheme
import com.wolfo.storycraft.presentation.ui.components.StoryCraftCard
import com.wolfo.storycraft.presentation.ui.utils.glass
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.rememberHazeState

@Composable
fun StoryStats(
    pageCount: Int,
    viewCount: Int,
    rating: Float,
    modifier: Modifier = Modifier
) {
    GlassCard {
        Row(
            modifier = modifier.fillMaxWidth().padding(6.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
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
    GlassCard(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.onPrimaryContainer,
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = modifier
                .padding(vertical = 5.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
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
