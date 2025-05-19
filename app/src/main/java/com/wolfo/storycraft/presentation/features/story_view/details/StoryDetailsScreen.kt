package com.wolfo.storycraft.presentation.features.story_view.details

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedFilterChip
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.wolfo.storycraft.R
import com.wolfo.storycraft.domain.model.Review
import com.wolfo.storycraft.domain.model.StoryBaseInfo
import com.wolfo.storycraft.domain.model.Tag
import com.wolfo.storycraft.domain.model.UserSimple
import com.wolfo.storycraft.presentation.common.CustomScrollableColumn
import com.wolfo.storycraft.presentation.common.ErrorBottomMessage
import com.wolfo.storycraft.presentation.common.GlassCard
import com.wolfo.storycraft.presentation.common.Loading
import com.wolfo.storycraft.presentation.common.LoadingBar
import com.wolfo.storycraft.presentation.common.PremiumInfoChip
import com.wolfo.storycraft.presentation.common.formatNumber
import com.wolfo.storycraft.presentation.features.story_list.AppStatusBarUiState
import org.koin.androidx.compose.koinViewModel
import java.time.LocalDateTime

@Composable
fun StoryDetailsScreen(
    storyId: String?,
    viewModel: StoryDetailsViewModel = koinViewModel(),
    onReadStory: (String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val loadFullUiState by viewModel.loadFullState.collectAsState()
    val reviewsUiState by viewModel.loadReviewsState.collectAsState()
    val appStatusBarUiState by viewModel.appStatusBarUiState.collectAsState()

    LaunchedEffect(loadFullUiState) {
        if(loadFullUiState is FullStoryLoadState.Success) {
            onReadStory(storyId!!)
            viewModel.resetLoadState()
        }
    }

    Log.d("SLDetails", "$storyId")

    when(val state = uiState) {
        is StoryDetailsUiState.Loading -> Loading()
        is StoryDetailsUiState.Error -> Error(state.error)
        is StoryDetailsUiState.Success -> StoryDetails(
            story = state.data,
            reviewsUiState = reviewsUiState,
            onReadStory = { viewModel.loadStoryFullById() }
        )
        is StoryDetailsUiState.Idle -> {
            viewModel.attemptLoadBaseStory(storyId)
            Text(text = "Прочитайте чужую историю или создайте свою!")
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

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun StoryDetails(
    story: StoryBaseInfo,
    reviewsUiState: StoryReviewsUiState<List<Review>>,
    onReadStory: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    val colorScheme = MaterialTheme.colorScheme
    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp

    // Параллакс эффект для фона
    val parallaxFactor = 0.3f
    val offset = (scrollState.value * parallaxFactor).coerceAtMost(0f)

    // Анимация появления
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visible = true }

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn() + slideInVertically { it / 2 },
        exit = fadeOut() + slideOutVertically { it / 2 }
    ) {
        Box(modifier = modifier.fillMaxSize()) {
            // Иммерсивный фон с параллакс эффектом
            AsyncImage(
                model = "https://mir-s3-cdn-cf.behance.net/project_modules/disp/7ee53b58387179.59fa3579a45c6.jpg",
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        translationY = offset
                        alpha = 0.9f
                    },
                colorFilter = ColorFilter.tint(
                    color = colorScheme.surface.copy(alpha = 0.2f),
                    blendMode = BlendMode.Overlay
                ),
                error = painterResource(R.drawable.abstraction_profile)
            )

            // Затемнение с градиентом
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = Brush.verticalGradient(
                            0f to colorScheme.surface.copy(alpha = 0.7f),
                            0.3f to Color.Transparent,
                            0.7f to Color.Transparent,
                            1f to colorScheme.surface.copy(alpha = 0.8f),
                            startY = 0f,
                            endY = screenHeight.value
                        )
                    )
            )

            // Основной контент
            Scaffold(
                containerColor = Color.Transparent,
                floatingActionButton = {
                    FloatingActionButton(
                        onClick = { onReadStory(story.id) },
                        modifier = Modifier
                            .padding(16.dp)
                            .shadow(16.dp, shape = CircleShape),
                        containerColor = colorScheme.primary,
                        contentColor = colorScheme.onPrimary
                    ) {
                        Icon(
                            imageVector = Icons.Filled.PlayArrow,
                            contentDescription = "Читать",
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }
            ) { padding ->
                CustomScrollableColumn(
                    modifier = Modifier.fillMaxHeight(),
                    scrollState = scrollState,
                    contentPadding = padding,
                    verticalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    Spacer(modifier = Modifier.height(screenHeight * 0.4f))

                    // Заголовок с декоративным элементом
                    Column(
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.padding(horizontal = 24.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .width(40.dp)
                                .height(4.dp)
                                .background(
                                    color = colorScheme.primary,
                                    shape = RoundedCornerShape(2.dp)
                                )
                                .align(Alignment.Start)
                        )

                        Text(
                            text = story.title,
                            style = MaterialTheme.typography.displaySmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = colorScheme.onSurface
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )

                        // Автор с аватаркой
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .background(
                                        color = colorScheme.primary.copy(alpha = 0.2f),
                                        shape = CircleShape
                                    )
                                    .border(
                                        width = 1.dp,
                                        color = colorScheme.primary.copy(alpha = 0.5f),
                                        shape = CircleShape
                                    )
                                    .padding(4.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = "Автор",
                                    tint = colorScheme.primary
                                )
                            }

                            Text(
                                text = "Неизвестный автор",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    color = colorScheme.onSurfaceVariant
                                )
                            )
                        }
                    }

                    // Теги с анимацией
                    AnimatedVisibility(
                        visible = true,// !story.tags.isNullOrEmpty(),
                        enter = fadeIn() + expandVertically(),
                        modifier = Modifier.padding(horizontal = 24.dp)
                    ) {
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            story.tags.forEach { tag ->
                                ElevatedFilterChip(
                                    selected = false,
                                    onClick = {},
                                    label = {
                                        Text(
                                            text = tag.name,
                                            style = MaterialTheme.typography.labelMedium
                                        )
                                    },
                                    colors = FilterChipDefaults.elevatedFilterChipColors(
                                        containerColor = colorScheme.surfaceVariant.copy(alpha = 0.8f),
                                        labelColor = colorScheme.onSurfaceVariant
                                    ),
                                    border = FilterChipDefaults.filterChipBorder(
                                        borderColor = colorScheme.outline.copy(alpha = 0.3f),
                                        enabled = true,
                                        selected = false
                                    ),
                                    elevation = FilterChipDefaults.elevatedFilterChipElevation(
                                        elevation = 4.dp)
                                )
                            }
                        }
                    }

                    // Описание с кастомной карточкой
                    GlassCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp)
                    ) {
                        Column(modifier = Modifier.padding(20.dp).background(Color.Transparent)) {
                            Text(
                                text = "О ПУТЕШЕСТВИИ",
                                style = MaterialTheme.typography.labelLarge.copy(
                                    color = colorScheme.tertiary,
                                    letterSpacing = 1.2.sp
                                ),
                                modifier = Modifier.padding(bottom = 12.dp).background(Color.Transparent)
                            )

                            Text(
                                text = story.description ?: "Эта история ждет своего рассказа...",
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    color = colorScheme.onSurfaceVariant,
                                    lineHeight = 24.sp
                                )
                            )
                        }
                    }

                    // Статистика с иконками
                    GlassCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            PremiumInfoChip(
                                icon = Icons.Filled.Menu,
                                text = "${12} стр.",
                                subText = "Объем"
                            )

                            PremiumInfoChip(
                                icon = Icons.Filled.Face,
                                text = formatNumber(1300),
                                subText = "Просмотры"
                            )

                            PremiumInfoChip(
                                icon = Icons.Filled.Star,
                                text = "4.8",
                                subText = "Рейтинг"
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(80.dp))

                    StoryReviews(reviewsUiState)

                }
            }
        }
    }
}

@Composable
fun StoryReviews(reviewsUiState: StoryReviewsUiState<List<Review>>) {
    GlassCard(

    ) {
        when(val state = reviewsUiState) {
            is StoryReviewsUiState.Loading -> Loading()
            is StoryReviewsUiState.Error -> Error(state.error)
            is StoryReviewsUiState.Success -> {
                Column() {
                    if (reviewsUiState.data.isNotEmpty()) {
                        ReviewItem(review = reviewsUiState.data[0])
                    }
                }
            }
            is StoryReviewsUiState.Idle -> {
                Text(text = "Отзывов ещё никто не оставлял")
            }
        }
    }
}

@Composable
fun ReviewItem(review: Review, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(90.dp),
        shape = MaterialTheme.shapes.medium,
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Box(modifier = Modifier.fillMaxSize()) {

            Box() {
                // Фоновое изображение
                AsyncImage(
                    modifier = Modifier.fillMaxWidth(0.4f),
                    model = review.user.avatarUrl,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    alignment = Alignment.CenterStart,
                    placeholder = painterResource(R.drawable.ic_launcher_background),
                    error = painterResource(R.drawable.abstraction_profile),
                )

                // Градиент для лучшей читаемости текста
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .drawWithContent(
                            {
                                drawContent()
                                drawRect(
                                brush = Brush.horizontalGradient(
                                    0f to Color.Transparent,
                                    1f to Color.Black,
                                    startX = size.width * 0.2f,
                                    endX = size.width * 0.4f
                                    )
                                )
                            }
                        )
                )
            }

            // Контент
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.End
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(0.5f),
                    horizontalAlignment = Alignment.End
                ) {
                    
                    // Заголовок
                    Text(
                        text = review.user.username,
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
                        text = review.reviewText ?: "",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = Color.White.copy(alpha = 0.9f)
                        ),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                // Теги и мета-информация
                Column {

                }
            }
        }
    }
}

@Preview
@Composable
fun ItemPreview() {
    StoryDetails(
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
        reviewsUiState = StoryReviewsUiState.Idle,
        onReadStory = {}
    )
}

@Preview
@Composable
fun ReviewPreview() {
    ReviewItem(
        review = Review(
            id = "dfgfdgdfg",
            rating = 4,
            reviewText = "This is incredible Story!",
            storyId = "asdfg-fdgdf24gh",
            userId = "dfgdfgdfhdfh",
            user = UserSimple(
                id = "dfgdfgdfhdfh",
                username = "Wolfo",
                avatarUrl = ""),
            createdAt = "",
            updatedAt = null
            )
        )
}
