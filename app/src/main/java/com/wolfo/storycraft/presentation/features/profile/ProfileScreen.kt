package com.wolfo.storycraft.presentation.features.profile

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
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
import com.wolfo.storycraft.domain.model.StoryBaseInfo
import com.wolfo.storycraft.domain.model.User
import com.wolfo.storycraft.domain.model.UserSimple
import com.wolfo.storycraft.presentation.common.CustomScrollableColumn
import com.wolfo.storycraft.presentation.common.ErrorBottomMessage
import com.wolfo.storycraft.presentation.common.GlassCard
import com.wolfo.storycraft.presentation.common.Loading
import com.wolfo.storycraft.presentation.common.LoadingBar
import com.wolfo.storycraft.presentation.common.PremiumInfoChip
import com.wolfo.storycraft.presentation.common.Utils
import com.wolfo.storycraft.presentation.features.story_list.AppStatusBarUiState
import com.wolfo.storycraft.presentation.features.story_list.PremiumBadge
import org.koin.androidx.compose.koinViewModel
import java.time.Duration
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel = koinViewModel(),
    onLogout: () -> Unit,
    onEditProfile: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val appStatusBarUiState by viewModel.appStatusBarUiState.collectAsState()

    val scrollState = rememberScrollState()

    when(val state = uiState) {
        is ProfileUiState.Idle -> { viewModel.loadProfile() }
        is ProfileUiState.Loading -> Loading()
        is ProfileUiState.Error -> Error(state.error)
        is ProfileUiState.Success -> {
            Profile(content = state.data,
                scrollState) {
                onLogout()
            }
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
                Log.d("Profile Screen", "Failed")
                ErrorBottomMessage(
                    message = barState.error.message ?: "Unknown error",
                    isVisible = true
                ) { }}
        }
    }
}

@Composable
fun Profile(content: User,
            scrollState: ScrollState,
            onLogout: () -> Unit) {
    Box(modifier = Modifier.fillMaxSize()) {
        // Параллакс-фон с аватаркой
        val backgroundColor = MaterialTheme.colorScheme.background
        Box(modifier = Modifier.fillMaxSize()) {
            // 1. Белый фон на весь экран
            Box(modifier = Modifier.fillMaxSize().background(backgroundColor)) {
            }

            // 2. Аватарка с градиентным переходом
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(350.dp)
                    .graphicsLayer {
                        translationY = scrollState.value * 0.5f
                    }
                    .drawWithContent {
                        drawContent() // Рисуем аватарку
                        // Рисуем градиент поверх
                        drawRect(
                            brush = Brush.verticalGradient(
                                0f to Color.Transparent,
                                1f to backgroundColor,
                                startY = size.height * 0.8f,
                            )
                        )
                    }
            ) {
                AsyncImage(
                    model = content.avatarUrl,
                    contentDescription = "Аватар",
                    contentScale = ContentScale.Crop,
                    error = painterResource(R.drawable.abstraction_profile),
                    modifier = Modifier.fillMaxSize()
                )
            }
        }

        CustomScrollableColumn(scrollState = scrollState, Modifier.fillMaxWidth().padding(top = 250.dp).padding(horizontal = 10.dp)) {

            GlassCard(modifier = Modifier.fillMaxWidth()) {

                Column(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = content.username,
                        style = MaterialTheme.typography.headlineMedium,
                    )

                    HorizontalDivider(
                        modifier = Modifier.fillMaxSize(0.5f),
                        color = MaterialTheme.colorScheme.background
                    )

                    content.signature?.let {
                        Text(
                            text = content.signature!!,
                        )

                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            val dateTime = remember { Utils.toLocaleDateTime(content.createdAt) }

            val activeDays = Duration.between(dateTime, LocalDateTime.now()).toDays()

            // Основная информация
            GlassCard(Modifier.fillMaxWidth()) {
                Column {
                    // Статистика (аналогично StoryDetails)
                    FlowRow(modifier = Modifier
                        .background(Color.Transparent)
                        .fillMaxWidth()
                        .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceAround) {
                        PremiumInfoChip(icon = Icons.Filled.Menu,
                            text = "${content.stories.size}",
                            subText = "Кол-во историй") // Кол-во историй
                        PremiumInfoChip(
                            icon = Icons.Filled.Star,
                            color = Color.Yellow,
                            text = "${content.overallRating}",
                            subText = "Рейтинг"
                        ) // Рейтинг
                        PremiumInfoChip(icon = Icons.Filled.DateRange,
                            text = activeDays.toString(),
                            subText = "Дней в StoryCraft") // Кол-во историй
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            GlassCard(modifier = Modifier.heightIn(min = 0.dp, max = 350.dp)) {
                Column(modifier = Modifier
                    .fillMaxWidth().padding(5.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) { // Адаптивная высота) {
                    Text(text = "Ваши истории",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.SemiBold
                        ))

                    HorizontalDivider(
                        modifier = Modifier.fillMaxWidth(0.5f),
                        color = MaterialTheme.colorScheme.background
                    )

                    LazyVerticalGrid(
                        columns = GridCells.Adaptive(150.dp),
                        contentPadding = PaddingValues(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(content.stories) { story ->
                            StoryByUserItem(story, onClick = {})
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(80.dp))
        }

        // Кнопка выхода
        FloatingActionButton(
            onClick = onLogout,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(24.dp),
            containerColor = MaterialTheme.colorScheme.errorContainer
        ) {
            Icon(Icons.AutoMirrored.Filled.ExitToApp, "Выйти")
        }
    }
}

@Preview
@Composable
fun ProfilePreview() {
    val scrollState = rememberScrollState()

    val content = User(
        id = "sdjhfjksdhf-dsfgdfg-fdgfg",
        username = "Wolfo",
        email = "test@gmail.com",
        signature = "This is MY Android App!",
        avatarUrl = "",
        createdAt = "2025-01-09T12:00:14.984Z",
        overallRating = 4.63f,
        stories = listOf(
            StoryBaseInfo(
                id = "dsgdfgdfg-fdgdf23r-dsf23",
                title = "My story",
                coverImageUrl = "",
                averageRating = 4.8f,
                publishedTime = "2025-05-07T12:21:56.288Z",
                description = null,
                viewCount = 0,
                author = UserSimple(
                    id = "sdjhfjksdhf-dsfgdfg-fdgfg",
                    username = "Wolfo",
                    avatarUrl = ""
                )
            ),
            StoryBaseInfo(
                id = "dsgdfgdfg-fdgdf23r-dsf23",
                title = "My story",
                coverImageUrl = "",
                averageRating = 4.8f,
                publishedTime = "2025-05-07T12:21:56.288Z",
                description = null,
                viewCount = 0,
                author = UserSimple(
                    id = "sdjhfjksdhf-dsfgdfg-fdgfg",
                    username = "Wolfo",
                    avatarUrl = ""
                )
            ),
            StoryBaseInfo(
                id = "dsgdfgdfg-fdgdf23r-dsf23",
                title = "My story",
                coverImageUrl = "",
                averageRating = 4.8f,
                publishedTime = "2025-05-07T12:21:56.288Z",
                description = null,
                viewCount = 0,
                author = UserSimple(
                    id = "sdjhfjksdhf-dsfgdfg-fdgfg",
                    username = "Wolfo",
                    avatarUrl = ""
                )
            ),StoryBaseInfo(
                id = "dsgdfgdfg-fdgdf23r-dsf23",
                title = "My story",
                coverImageUrl = "",
                averageRating = 4.8f,
                publishedTime = "2025-05-07T12:21:56.288Z",
                description = null,
                viewCount = 0,
                author = UserSimple(
                    id = "sdjhfjksdhf-dsfgdfg-fdgfg",
                    username = "Wolfo",
                    avatarUrl = ""
                )
            ),StoryBaseInfo(
                id = "dsgdfgdfg-fdgdf23r-dsf23",
                title = "My story",
                coverImageUrl = "",
                averageRating = 4.8f,
                publishedTime = "2025-05-07T12:21:56.288Z",
                description = null,
                viewCount = 0,
                author = UserSimple(
                    id = "sdjhfjksdhf-dsfgdfg-fdgfg",
                    username = "Wolfo",
                    avatarUrl = ""
                )
            ),StoryBaseInfo(
                id = "dsgdfgdfg-fdgdf23r-dsf23",
                title = "My story",
                coverImageUrl = "",
                averageRating = 4.8f,
                publishedTime = "2025-05-07T12:21:56.288Z",
                description = null,
                viewCount = 0,
                author = UserSimple(
                    id = "sdjhfjksdhf-dsfgdfg-fdgfg",
                    username = "Wolfo",
                    avatarUrl = ""
                )
            ),StoryBaseInfo(
                id = "dsgdfgdfg-fdgdf23r-dsf23",
                title = "My story",
                coverImageUrl = "",
                averageRating = 4.8f,
                publishedTime = "2025-05-07T12:21:56.288Z",
                description = null,
                viewCount = 0,
                author = UserSimple(
                    id = "sdjhfjksdhf-dsfgdfg-fdgfg",
                    username = "Wolfo",
                    avatarUrl = ""
                )
            ),StoryBaseInfo(
                id = "dsgdfgdfg-fdgdf23r-dsf23",
                title = "My story",
                coverImageUrl = "",
                averageRating = 4.8f,
                publishedTime = "2025-05-07T12:21:56.288Z",
                description = null,
                viewCount = 0,
                author = UserSimple(
                    id = "sdjhfjksdhf-dsfgdfg-fdgfg",
                    username = "Wolfo",
                    avatarUrl = ""
                )
            )
        )
    )

    Profile(content = content, scrollState = scrollState) { }
}


@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun StoryByUserItem(
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
            .height(70.dp)
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
        Box(modifier = Modifier.fillMaxWidth()) {
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
                    .padding(12.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    // Заголовок
                    Text(
                        text = story.title,
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        ),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Row(modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = Color.Yellow,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "%.1f".format(story.averageRating),
                            style = MaterialTheme.typography.labelMedium.copy(
                                color = Color.White
                            )
                        )
                    }

                    val formattedDate = remember(dateTime) {
                        DateTimeFormatter
                            .ofPattern("dd.MM.yy", Locale.getDefault())
                            .format(dateTime)
                    }

                    // Дата
                    Text(
                        text = formattedDate,
                        style = MaterialTheme.typography.labelMedium.copy(
                            color = Color.White.copy(alpha = 0.8f)
                        )
                    )
                }
            }

            // Иконка премиума
            if (false) { // is Premium?
                PremiumBadge(modifier = Modifier.align(Alignment.TopEnd).size(35.dp))
            }
        }
    }
}
