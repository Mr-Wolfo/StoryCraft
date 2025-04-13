package com.wolfo.storycraft.presentation.features.storylist

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wolfo.storycraft.domain.model.StoryBase
import org.koin.androidx.compose.koinViewModel

@Composable
fun StoryListScreen(
    viewModel: StoryListViewModel = koinViewModel(),
    onStory: (Long) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    when {
        uiState.isLoading -> Loading()
        uiState.error != null -> Error(uiState.error!!)
        uiState.stories.isEmpty() -> Empty()
            else -> {
                StoryList(
                    isRefreshing = false,
                    stories = uiState.stories,
                    { viewModel.updateStoriesCatalog() },
                    { id -> onStory(id) },)
            }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StoryList(
    isRefreshing: Boolean,
    stories: List<StoryBase>,
    onRefresh: () -> Unit,
    onStory: (Long) -> Unit
) {
    PullToRefreshBox(
        isRefreshing = isRefreshing,
        onRefresh = onRefresh,
        modifier = Modifier.fillMaxSize()
    ) {
        LazyColumn(modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(5.dp),
            verticalArrangement = Arrangement.spacedBy(5.dp)
        ) { itemsIndexed(items = stories) { index, story -> Item(story) {onStory(it)} } }
    }
}

@Composable
fun Empty() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(text = "Пусто")
    }
}


@Composable
fun Loading() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
fun Error(e: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(text = e)
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun Item(
    story: StoryBase,
    onStory: (Long) -> Unit
) {
    // Здесь можно получить реальное изображение для истории (например, из viewModel)
    val imagePainter: Painter? = null // Замените на реальную загрузку изображения

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(160.dp)
            .padding(horizontal = 8.dp, vertical = 4.dp)
            .clickable { onStory(story.id) },
        shape = MaterialTheme.shapes.medium,
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Фоновое изображение или градиент
            if (imagePainter != null) {
                Image(
                    painter = imagePainter,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(
                                    MaterialTheme.colorScheme.primary,
                                    MaterialTheme.colorScheme.secondary
                                ),
                                startX = 0f,
                                endX = Float.POSITIVE_INFINITY
                            )
                        )
                )
            }

            // Затемнение и затухание к правому краю
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                Color.Black.copy(alpha = 0.3f),
                                Color.Transparent
                            ),
                            endX = 0.7f
                        )
                    )
            )

            // Контент поверх фона
            Row(
                modifier = Modifier.fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Текстовая информация
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(16.dp)
                ) {
                    Text(
                        text = story.title,
                        style = MaterialTheme.typography.titleLarge.copy(
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = story.description ?: "",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = Color.White.copy(alpha = 0.9f)
                        ),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Теги
                    if (!story.tags.isNullOrEmpty()) {
                        FlowRow(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            (story.tags as Iterable<String>).forEach { tag ->
                                Box(
                                    modifier = Modifier
                                        .clip(MaterialTheme.shapes.small)
                                        .background(Color.White.copy(alpha = 0.2f))
                                        .border(
                                            width = 1.dp,
                                            color = Color.White.copy(alpha = 0.4f),
                                            shape = MaterialTheme.shapes.small
                                        )
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = tag,
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = Color.White
                                        )
                                    )
                                }
                            }
                        }
                    }
                }

                // Иконка перехода
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = "Read story",
                    tint = Color.White,
                    modifier = Modifier
                        .padding(end = 16.dp)
                        .size(28.dp)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun StoryCardPreview() {
    MaterialTheme {
        Surface {
            Item(
                story = StoryBase(
                    id = 1,
                    title = "Приключения в затерянном городе",
                    description = "История о группе исследователей, обнаруживших древний город, скрытый в глубинах джунглей. То, что они нашли, изменило их жизни навсегда...",
                    authorId = 1,
                    tags = listOf("Приключения", "Фэнтези", "Тайна"),
                    averageRating = 4.5f
                ),
                onStory = {}
            )
        }
    }
}