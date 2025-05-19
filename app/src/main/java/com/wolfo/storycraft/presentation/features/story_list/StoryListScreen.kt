package com.wolfo.storycraft.presentation.features.story_list

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.wolfo.storycraft.R
import com.wolfo.storycraft.domain.DataError
import com.wolfo.storycraft.domain.model.StoryBaseInfo
import com.wolfo.storycraft.domain.model.Tag
import com.wolfo.storycraft.domain.model.UserSimple
import com.wolfo.storycraft.presentation.common.ErrorBottomMessage
import com.wolfo.storycraft.presentation.common.GlassCard
import com.wolfo.storycraft.presentation.common.LoadingBar
import com.wolfo.storycraft.presentation.common.Utils
import com.wolfo.storycraft.presentation.common.formatNumber
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StoryListScreen(
    viewModel: StoryListViewModel = koinViewModel(),
    onStoryClick: (String) -> Unit,
) {
    val uiState by viewModel.uiState.collectAsState()
    val appStatusBarUiState by viewModel.appStatusBarUiState.collectAsState()

    val scrollState = rememberScrollState()
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            GlassCard(modifier = Modifier.fillMaxWidth().windowInsetsPadding(WindowInsets.statusBars).padding(top = 5.dp)) {
                Row(modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically) {
                    OutlinedTextField(value = "Здесь будет поиск", onValueChange = {}, modifier = Modifier.fillMaxWidth(0.8f))
                    Icon(Icons.Default.Search, contentDescription = "Search", modifier = Modifier.size(40.dp))
                }
            }
        }
    ) { paddingValues ->
        StoryListBackground(scrollState = scrollState)
        Box(modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)) {

            when (val state = uiState) {
                StoryListUiState.Loading -> FullScreenLoading()
                is StoryListUiState.Error -> ErrorState(
                    error = (uiState as StoryListUiState.Error<List<StoryBaseInfo>>).error,
                    onRetry = { viewModel.refreshStoriesCatalog() }
                )
                is StoryListUiState.Success -> {
                    if (state.data.isEmpty()) {
                        EmptyState()
                    } else {
                        StoryListContent(
                            stories = state.data,
                            scrollState = scrollState,
                            onRefresh = { coroutineScope.launch { viewModel.refreshStoriesCatalog() } },
                            onStoryClick = {
                                Log.d("SLScreen", it)
                                onStoryClick(it)}
                        )
                    }
                }
                StoryListUiState.Idle -> {}
            }
        }

        when(val barState = appStatusBarUiState) {
            is AppStatusBarUiState.Idle -> { }
            is AppStatusBarUiState.Loading -> {
                Box(
                    modifier = Modifier.windowInsetsPadding(WindowInsets.navigationBars)
                        .fillMaxSize().background(Color.Transparent),
                    contentAlignment = Alignment.BottomCenter
                ) {
                    println("LOAD")
                    LoadingBar(
                        isVisible = true
                    )
                }
            }
            is AppStatusBarUiState.Error -> {
                Box(
                    modifier = Modifier.windowInsetsPadding(WindowInsets.navigationBars).padding(10.dp)
                        .fillMaxSize(),
                    contentAlignment = Alignment.BottomCenter
                ) {
                    Log.d("StoryList Screen", "Failed")
                    ErrorBottomMessage(
                        message = barState.error.message ?: "Unknown error",
                        isVisible = true
                    ) { }}
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun StoryListContent(
    stories: List<StoryBaseInfo>,
    scrollState: ScrollState,
    onRefresh: () -> Unit,
    onStoryClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    PullToRefreshBox(
        isRefreshing = false,
        onRefresh = onRefresh,
        modifier = modifier
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            state = rememberLazyListState(),
            contentPadding = PaddingValues(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(
                items = stories,
                key = { it.id }
            ) { story ->
                StoryListItem(
                    story = story,
                    onClick = { onStoryClick(story.id) }
                )
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

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun StoryListItem(
    story: StoryBaseInfo,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val borderColor = if (false) MaterialTheme.colorScheme.primary else Color.Transparent // is Premium?

    val dateTime = remember { Utils.toLocaleDateTime(story.publishedTime) }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(180.dp)
            .clickable(
                onClick = onClick
            ),
        shape = MaterialTheme.shapes.medium,
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = BorderStroke(
            width = if (false) 1.dp else 0.dp, // is Premium?
            color = borderColor
        )
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Фоновое изображение
            AsyncImage(
                model = story.coverImageUrl,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
                placeholder = painterResource(R.drawable.ic_launcher_background),
                error = painterResource(R.drawable.stars_sky)
            )

            // Градиент для лучшей читаемости текста
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color.Black.copy(alpha = 0.7f)
                            ),
                            startY = 0.4f
                        )
                    )
            )

            // Контент
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    // Заголовок
                    Text(
                        text = story.title,
                        style = MaterialTheme.typography.titleLarge.copy(
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        ),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Описание
                    Text(
                        text = story.description ?: "",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = Color.White.copy(alpha = 0.9f)
                        ),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                // Теги и мета-информация
                Column {
                    if (story.tags.isNotEmpty()) {
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            story.tags.forEach { tag ->
                                TagChip(tag = tag.name)
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    StoryMetaInfo(
                        rating = story.averageRating,
                        views = story.viewCount,
                        date = dateTime
                    )
                }
            }

            // Иконка премиума
            if (false) { // is Premium?
                PremiumBadge(modifier = Modifier.align(Alignment.TopEnd))
            }
        }
    }
}

@Composable
fun TagChip(tag: String) {
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

@Composable
fun StoryMetaInfo(
    rating: Float,
    views: Int,
    date: LocalDateTime,
    modifier: Modifier = Modifier
) {

    val formattedDate = remember(date) {
        DateTimeFormatter
            .ofPattern("dd.MM.yy", Locale.getDefault())
            .format(date)
    }


    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Рейтинг
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Default.Star,
                contentDescription = null,
                tint = Color.Yellow,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "%.1f".format(rating),
                style = MaterialTheme.typography.labelMedium.copy(
                    color = Color.White
                )
            )
        }

        // Просмотры
        Text(
            text = "${formatNumber(views)} просмотров",
            style = MaterialTheme.typography.labelMedium.copy(
                color = Color.White.copy(alpha = 0.8f)
            )
        )

        // Дата
        Text(
            text = formattedDate,
            style = MaterialTheme.typography.labelMedium.copy(
                color = Color.White.copy(alpha = 0.8f)
            )
        )
    }
}

@Composable
fun PremiumBadge(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .padding(8.dp)
            .background(
                color = MaterialTheme.colorScheme.primary,
                shape = CircleShape
            )
            .size(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Default.Star,
            contentDescription = "Премиум",
            tint = MaterialTheme.colorScheme.onPrimary,
            modifier = Modifier.size(16.dp)
        )
    }
}

@Composable
private fun FullScreenLoading() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
private fun EmptyState() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.Warning,
            contentDescription = null,
            modifier = Modifier.size(48.dp),
            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Историй пока нет",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        )
    }
}

@Composable
private fun ErrorState(
    error: DataError,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.Warning,
            contentDescription = null,
            modifier = Modifier.size(48.dp),
            tint = MaterialTheme.colorScheme.error
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = when (error) {
                is DataError.Network -> "Ошибка сети"
                is DataError.Database -> "Ошибка базы данных"
                else -> "Произошла ошибка"
            },
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = error.message ?: "Неизвестная ошибка",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = onRetry,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        ) {
            Text("Повторить попытку")
        }
    }
}

@Composable
private fun StoryListBackground(scrollState: ScrollState) {
    val parallaxFactor = 0.3f
    val offset = scrollState.value * parallaxFactor

    Box(modifier = Modifier.fillMaxSize()) {
        AsyncImage(
            model = "https://zefirka.club/wallpapers/uploads/posts/2023-04/thumbs/1680362002_zefirka-club-p-chernie-oboi-na-telefon-dlya-malchikov-16.jpg",
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer { translationY = offset }
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        0f to Color.Transparent,
                        0.5f to MaterialTheme.colorScheme.surface.copy(alpha = 0.7f),
                        1f to MaterialTheme.colorScheme.surface
                    )
                )
        )
    }
}
