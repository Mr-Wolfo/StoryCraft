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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedFilterChip
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.wolfo.storycraft.R
import com.wolfo.storycraft.domain.model.Review
import com.wolfo.storycraft.domain.model.story.StoryBaseInfo
import com.wolfo.storycraft.presentation.common.BackgroundImage
import com.wolfo.storycraft.presentation.common.CustomScrollableColumn
import com.wolfo.storycraft.presentation.common.Error
import com.wolfo.storycraft.presentation.common.ErrorBottomMessage
import com.wolfo.storycraft.presentation.common.GlassCard
import com.wolfo.storycraft.presentation.common.Loading
import com.wolfo.storycraft.presentation.common.LoadingBar
import com.wolfo.storycraft.presentation.common.PremiumInfoChip
import com.wolfo.storycraft.presentation.common.Utils
import com.wolfo.storycraft.presentation.common.formatNumber
import com.wolfo.storycraft.presentation.features.story_list.AppStatusBarUiState
import com.wolfo.storycraft.presentation.theme.extendedColors
import org.koin.androidx.compose.koinViewModel
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun StoryDetailsScreen(
    storyId: String?,
    viewModel: StoryDetailsViewModel = koinViewModel(),
    onReadStory: (String) -> Unit,
    onNavigateToCreateStory: () -> Unit,
    onNavigateToStoryList: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val loadFullUiState by viewModel.loadFullState.collectAsState()
    val reviewsUiState by viewModel.loadReviewsState.collectAsState()
    val appStatusBarUiState by viewModel.appStatusBarUiState.collectAsState()
    val addReviewState by viewModel.addReviewState.collectAsState()

    LaunchedEffect(loadFullUiState) {
        if(loadFullUiState is FullStoryLoadState.Success) {
            onReadStory(storyId!!)
            viewModel.resetLoadState()
        }
    }

    Log.d("SLDetails", "$storyId")

    when(val state = uiState) {
        is StoryDetailsUiState.Loading -> Loading()
        is StoryDetailsUiState.Error -> Error(e = state.error.message ?: "Unknown Error")
        is StoryDetailsUiState.Success -> StoryDetails(
            story = state.data,
            reviewsUiState = reviewsUiState,
            currentUserId = viewModel.currentUserId.collectAsState(),
            addReviewState = addReviewState,
            onReadStory = { viewModel.attemptLoadFullStory() },
            onDeleteReview = { viewModel.deleteReview(it) },
            onCreateReview = { rating, text -> viewModel.addReview(storyId!!, rating, text) }
        )
        is StoryDetailsUiState.Idle -> {
            if (storyId == null) {
                EmptyStoryPlaceholder(
                    onExploreStories = onNavigateToStoryList,
                    onCreateStory = onNavigateToCreateStory
                )
            } else {
                // Если storyId есть, но состояние Idle - показываем загрузку
                Loading()
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
                Log.d("StoryList Screen", "Failed")
                ErrorBottomMessage(
                    message = barState.error.message ?: "Unknown error",
                    isVisible = true
                ) { }}
        }
        else -> { } // НЕ ПРОИЗОЙДЁТ
    }
}


@Composable
private fun EmptyStoryPlaceholder(
    onExploreStories: () -> Unit,
    onCreateStory: () -> Unit
) {

    BackgroundImage(
        painter = painterResource(R.drawable.details_background)
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.Menu,
            contentDescription = null,
            modifier = Modifier.size(100.dp),
            tint = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "История не выбрана",
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Выберите историю из коллекции или создайте свою собственную",
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            color = MaterialTheme.extendedColors.onDarkBackground
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = onExploreStories,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(text = "Исследовать истории")
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedButton(
            onClick = onCreateStory,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(text = "Создать свою историю",
                color = MaterialTheme.extendedColors.onDarkBackground)
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun StoryDetails(
    story: StoryBaseInfo,
    reviewsUiState: StoryReviewsUiState<List<Review>>,
    addReviewState: AddReviewUiState,
    currentUserId: State<String?>,
    onReadStory: (String) -> Unit,
    onDeleteReview: (String) -> Unit,
    onCreateReview: (Int, String) -> Unit,
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
                model = story.coverImageUrl,
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
                error = painterResource(R.drawable.details_background)
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
                    modifier = Modifier.fillMaxHeight().windowInsetsPadding(WindowInsets.navigationBars).padding(bottom = 30.dp),
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
                                    .padding(1.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(48.dp)
                                        .clip(CircleShape)
                                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    AsyncImage(
                                        model = story.author.avatarUrl,
                                        contentDescription = "Аватар",
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier.fillMaxSize(),
                                        error = rememberVectorPainter(Icons.Default.Person)
                                    )
                                }
                            }

                            Text(
                                text = story.author.username,
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
                                FilterChip(
                                    selected = false,
                                    onClick = {},
                                    label = {
                                        Text(
                                            text = tag.name,
                                            modifier = Modifier.background(Color.Transparent),
                                            style = MaterialTheme.typography.labelMedium
                                        )
                                    },
                                    colors = FilterChipDefaults.filterChipColors(
                                        containerColor = colorScheme.surfaceContainer.copy(alpha = 0.8f),
                                    ),
                                    border = FilterChipDefaults.filterChipBorder(
                                        enabled = true,
                                        selected = false,
                                        borderColor = colorScheme.surfaceContainer.copy(alpha = 0.8f)
                                    )
                                )
                            }
                        }
                    }

                    // Описание
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
                                text = formatNumber(story.viewCount),
                                subText = "Просмотры"
                            )

                            PremiumInfoChip(
                                icon = Icons.Filled.Star,
                                color = MaterialTheme.extendedColors.star,
                                text = "%.1f".format(story.averageRating),
                                subText = "Рейтинг"
                            )
                        }
                    }

                    StoryReviews(reviewsUiState, addReviewState, currentUserId, { onDeleteReview(it) }, { rating, text -> onCreateReview(rating, text) })

                }
            }
        }
    }
}

@Composable
fun StoryReviews(
    reviewsUiState: StoryReviewsUiState<List<Review>>,
    addReviewState: AddReviewUiState,
    currentUserId: State<String?>,
    onDeleteReview: (String) -> Unit,
    onAddReview: (Int, String) -> Unit,
    modifier: Modifier = Modifier
) {
    var showAllReviews by remember { mutableStateOf(false) }

    var showAddReviewDialog by remember { mutableStateOf(false) }
    var rating by remember { mutableIntStateOf(0) }
    var reviewText by remember { mutableStateOf("") }

    if (showAddReviewDialog) {
        AlertDialog(
            onDismissRequest = { showAddReviewDialog = false },
            title = { Text("Добавить отзыв") },
            text = {
                Column {
                    // Рейтинг звездами
                    Row(
                        modifier = Modifier.padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        repeat(5) { index ->
                            IconButton(
                                onClick = { rating = index + 1 },
                                modifier = Modifier.size(48.dp)
                            ) {
                                Icon(
                                    imageVector = if (index < rating) Icons.Filled.Star else Icons.Outlined.Star,
                                    contentDescription = "Рейтинг ${index + 1}",
                                    tint = if (index < rating) MaterialTheme.extendedColors.star else MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(32.dp)
                                )
                            }
                        }
                    }

                    // Текст отзыва
                    OutlinedTextField(
                        value = reviewText,
                        onValueChange = { reviewText = it },
                        label = { Text("Ваш отзыв") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3,
                        maxLines = 5
                    )

                    // Состояние добавления
                    when (addReviewState) {
                        is AddReviewUiState.Loading -> {
                            CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
                        }
                        is AddReviewUiState.Error -> {
                            Text(
                                text = (addReviewState as AddReviewUiState.Error).message,
                                color = MaterialTheme.colorScheme.error,
                                modifier = Modifier.padding(top = 8.dp)
                            )
                        }
                        is AddReviewUiState.Success -> {
                            // Закрываем диалог после успешного добавления
                            LaunchedEffect(Unit) {
                                showAddReviewDialog = false
                                rating = 0
                                reviewText = ""
                            }
                        }
                        else -> {}
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (rating > 0) {
                            onAddReview(rating, reviewText)
                        }
                    },
                    enabled = rating > 0 && reviewText.isNotBlank()
                ) {
                    Text("Отправить")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showAddReviewDialog = false }
                ) {
                    Text("Отмена")
                }
            }
        )
    }


    GlassCard(
        modifier = modifier.padding(horizontal = 24.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Отзывы",
                    modifier = Modifier.align(Alignment.CenterVertically),
                    style = MaterialTheme.typography.titleLarge
                )

                Spacer(Modifier.padding(horizontal = 10.dp))

                if (reviewsUiState is StoryReviewsUiState.Success &&
                    !hasUserReview(reviewsUiState.data, currentUserId.value) && currentUserId.value != null) {
                        IconButton(
                            onClick = { showAddReviewDialog = true },
                            modifier = Modifier.size(40.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Добавить отзыв",
                                tint = MaterialTheme.colorScheme.tertiary
                            )
                        }
                    }

                if (reviewsUiState is StoryReviewsUiState.Success && reviewsUiState.data.size > 3) {
                    TextButton(
                        onClick = { showAllReviews = !showAllReviews }
                    ) {
                        Text(
                            text = if (showAllReviews) "Скрыть" else "Все отзывы",
                            style = MaterialTheme.typography.labelLarge
                        )
                    }
                }
            }

            when(val state = reviewsUiState) {
                is StoryReviewsUiState.Loading -> LoadingBar(true)
                is StoryReviewsUiState.Error -> Error(state.error)
                is StoryReviewsUiState.Success -> {
                    val reviews = remember(state.data, currentUserId) {
                        // Разделяем отзывы на собственный и остальные
                        val (ownReview, otherReviews) = state.data.partition {
                            it.user.id == currentUserId.value
                        }

                        val randomReviews = if (otherReviews.size > 3) {
                            otherReviews.shuffled().take(3)
                        } else {
                            otherReviews
                        }
                        ownReview + randomReviews
                    }

                    val displayedReviews = if (showAllReviews) state.data else reviews

                    if (displayedReviews.isEmpty()) {
                        Text(
                            text = "Отзывов ещё никто не оставлял",
                            modifier = Modifier.padding(vertical = 16.dp)
                        )
                    } else {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            displayedReviews.forEach { review ->
                                ReviewItem(
                                    review = review,
                                    isOwnReview = review.user.id == currentUserId.value,
                                    onDelete = { onDeleteReview(review.id) }
                                )
                            }
                        }
                    }
                }
                is StoryReviewsUiState.Idle -> {
                    Text(
                        text = "Загрузка отзывов...",
                        modifier = Modifier.padding(vertical = 16.dp)
                    )
                }
            }
        }
    }
}


@Composable
fun ReviewItem(
    review: Review,
    isOwnReview: Boolean,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showDeleteDialog by remember { mutableStateOf(false) }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Удалить отзыв?") },
            text = { Text("Вы уверены, что хотите удалить свой отзыв?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDelete()
                        showDeleteDialog = false
                    }
                ) {
                    Text("Удалить")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDeleteDialog = false }
                ) {
                    Text("Отмена")
                }
            }
        )
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors().copy(containerColor = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.8f)),
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Аватар пользователя
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    AsyncImage(
                        model = review.user.avatarUrl,
                        contentDescription = "Аватар",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize(),
                        error = rememberVectorPainter(Icons.Default.Person)
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                // Контент отзыва
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = review.user.username,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )

                        if (isOwnReview) {
                            Box(
                                modifier = Modifier
                                    .background(
                                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                                        shape = RoundedCornerShape(4.dp)
                                    )
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "Ваш",
                                    style = MaterialTheme.typography.labelSmall,
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    // Рейтинг
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        repeat(5) { index ->
                            Icon(
                                imageVector = if (index < review.rating) Icons.Filled.Star else Icons.Outlined.Star,
                                contentDescription = null,
                                tint = if (index < review.rating) MaterialTheme.extendedColors.star else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = review.reviewText ?: "",
                        style = MaterialTheme.typography.bodyMedium
                    )

                    Spacer(modifier = Modifier.height(8.dp))


                    val dateTime = remember { Utils.toLocaleDateTime(review.createdAt) }

                    val formattedDate = remember(dateTime) {
                        DateTimeFormatter
                            .ofPattern("dd.MM.yy", Locale.getDefault())
                            .format(dateTime)
                    }

                    Text(
                        text = formattedDate,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Кнопка удаления для своего отзыва
                if (isOwnReview) {
                    IconButton(
                        onClick = { showDeleteDialog = true },
                        modifier = Modifier.align(Alignment.Top)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Удалить отзыв",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
        }
    }
}

fun hasUserReview(reviews: List<Review>, userId: String?): Boolean {
    return reviews.any { it.user.id == userId }
}
