package com.wolfo.storycraft.presentation.ui.features.story_view.details.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.wolfo.storycraft.domain.model.story.StoryBaseInfo
import com.wolfo.storycraft.domain.model.story.Tag
import com.wolfo.storycraft.domain.model.user.UserSimple
import com.wolfo.storycraft.presentation.theme.StoryCraftTheme
import com.wolfo.storycraft.presentation.theme.spacing
import com.wolfo.storycraft.presentation.ui.components.AppCard
import com.wolfo.storycraft.presentation.ui.components.AppChip

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun StoryHeader(
    story: StoryBaseInfo,
    modifier: Modifier = Modifier
) {
    AppCard {
        Column(
            modifier = modifier
                .fillMaxWidth()
                .padding(MaterialTheme.spacing.large),
            verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.medium)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small)) {
                Text(
                    text = story.title,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small)
                ) {
                    AsyncImage(
                        model = story.author.avatarUrl,
                        contentDescription = "Аватар автора",
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .border(1.5.dp, MaterialTheme.colorScheme.primary, CircleShape),
                        contentScale = ContentScale.Crop
                    )
                    Text(
                        text = story.author.username,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }

            if (story.tags.isNotEmpty()) {
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small),
                    verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small)
                ) {
                    story.tags.forEach { tag ->
                        AppChip(tag = tag.name)
                    }
                }
            }
        }
    }
}


@Preview(showBackground = true, backgroundColor = 0xFF1C1936)
@Composable
private fun StoryHeaderPreview() {
    val mockStory = StoryBaseInfo(
        id = "1",
        title = "Хроники Затерянных Миров",
        coverImageUrl = "",
        averageRating = 4.8f,
        publishedTime = "2023-10-25T10:00:00Z",
        description = "Описание истории...",
        viewCount = 12345,
        author = UserSimple(id = "1", username = "Мастер Слов", avatarUrl = ""),
        tags = listOf(Tag("1", "Фэнтези"), Tag("2", "Высокое фэнтези"), Tag("3", "Магия"), Tag("4", "Приключения"))
    )
    StoryCraftTheme {
       StoryHeader(story = mockStory, modifier = Modifier.padding(16.dp))
    }
}
