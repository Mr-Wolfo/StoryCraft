package com.wolfo.storycraft.presentation.ui.features.story_list.components.list

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.wolfo.storycraft.R
import com.wolfo.storycraft.domain.model.story.StoryBaseInfo
import com.wolfo.storycraft.domain.model.story.Tag
import com.wolfo.storycraft.domain.model.user.UserSimple
import com.wolfo.storycraft.presentation.theme.spacing
import com.wolfo.storycraft.presentation.ui.components.AppChip
import com.wolfo.storycraft.presentation.ui.utils.UiUtils
import java.time.LocalDateTime

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun StoryListItem(
    story: StoryBaseInfo,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val dateTime = remember { UiUtils.toLocaleDateTime(story.publishedTime) }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(300.dp)
            .clickable(onClick = onClick),
        shape = MaterialTheme.shapes.large,
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            AsyncImage(
                model = story.coverImageUrl,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
                placeholder = painterResource(R.drawable.ic_launcher_background),
                error = painterResource(R.drawable.story_placeholder)
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.7f)),
                            startY = 0.4f
                        )
                    )
            )

            Column(modifier = Modifier.fillMaxSize()) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(MaterialTheme.spacing.medium)
                ) {
                    Text(
                        text = story.title,
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        ),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.height(MaterialTheme.spacing.small))

                    Text(
                        text = story.description ?: "",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = Color.White.copy(0.9f)
                        ),
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp)
                        .background(
                            shape = MaterialTheme.shapes.large,
                            color = MaterialTheme.colorScheme.primaryContainer)
                        .padding(horizontal = MaterialTheme.spacing.medium)
                        .padding(top = MaterialTheme.spacing.extraSmall, bottom = MaterialTheme.spacing.small),
                    verticalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier.weight(1f),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        if (story.tags.isNotEmpty()) {
                            FlowRow(
                                horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small),
                                maxItemsInEachRow = 2,
                                maxLines = 1
                            ) {
                                story.tags.take(2).forEach { tag ->
                                    AppChip(tag = tag.name)
                                }
                            }
                        } else {
                            AppChip(tag = "Тегов нет")
                        }
                    }

                    StoryMetaInfo(
                        rating = story.averageRating,
                        views = story.viewCount,
                        date = dateTime
                    )
                }
            }
        }
    }
}


@Preview
@Composable
fun ItemPreview() {
    StoryListItem(
        story = StoryBaseInfo(
            id = "this_test_id",
            title = "Полёт фантазии",
            description = "Незабываемое путешествие по совершенно безумным фантазиям автора!",
            coverImageUrl = "https://tse1.mm.bing.net/th?id=OIP.j4Ap1-mqoEhq9MDG7BtG0wHaFb&pid=15.1",
            averageRating = 4.5f,
            publishedTime = LocalDateTime.now().toString(),
            viewCount = 850,
            author = UserSimple(
                id = "test_author_id",
                username = "Wolfo",
                avatarUrl = null
            ),
            tags = listOf(Tag("1", "безумие"), Tag("2", "юмор"))
        ),
        onClick = {}
    )
}