package com.wolfo.storycraft.presentation.features.story_view.reader

import android.util.Log
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.wolfo.storycraft.R
import com.wolfo.storycraft.domain.model.story.Choice
import com.wolfo.storycraft.domain.model.story.Page
import com.wolfo.storycraft.domain.model.story.StoryFull
import com.wolfo.storycraft.presentation.common.GlassCard
import com.wolfo.storycraft.presentation.theme.extendedColors
import org.koin.androidx.compose.koinViewModel
import kotlin.math.floor

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class,
    ExperimentalAnimationApi::class
)
@Composable
fun StoryReaderScreen(
    storyId: String,
    viewModel: StoryReaderViewModel = koinViewModel(),
    onExploreStories: () -> Unit,
    onCreateStory: () -> Unit,
    onReturnToStory: (String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val pageState = remember { mutableStateOf(0) }
    val lastPageState = remember { mutableListOf<Int>() }
    val scrollState = rememberScrollState()
    val density = LocalDensity.current
    val context = LocalContext.current

    // Обработка состояний загрузки
    when (uiState) {
        StoryReaderUiState.Idle -> {
        }
        StoryReaderUiState.Loading -> {
            FullScreenLoader()
            return
        }
        is StoryReaderUiState.Error -> {
            ErrorView(
                error = (uiState as StoryReaderUiState.Error).error.message,
                onRetry = { viewModel.attemptLoadStory() }
            )
            return
        }

        is StoryReaderUiState.Success -> {
        }
    }

    val story = (uiState as? StoryReaderUiState.Success)?.data ?: return
    val currentPage = story.pages.getOrNull(pageState.value)

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        // Фоновые элементы
//        val circleColor = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.05f)
//        Canvas(modifier = Modifier.fillMaxSize()) {
//            drawCircle(
//                color = circleColor,
//                radius = size.maxDimension * 0.7f,
//                center = Offset(size.width * 0.8f, size.height * 0.2f)
//            )
//        }

        // Изображение страницы
        AsyncImage(
            model = story.pages.getOrNull(pageState.value)?.imageUrl,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            error = painterResource(R.drawable.details_background),
            modifier = Modifier.fillMaxSize()
        )

        // Основной контент
        Column(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.statusBars)
                .padding(horizontal = 36.dp)
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Анимированная страница
            AnimatedContent(
                targetState = pageState.value,
                transitionSpec = {
                    slideInHorizontally { width -> width } + fadeIn() togetherWith
                            slideOutHorizontally { width -> -width } + fadeOut()
                },
                label = "page_transition"
            ) { currentPageIndex ->
                val page = story.pages.getOrNull(currentPageIndex) ?: return@AnimatedContent

                Log.d("Read Story", "Page 0: ${story.pages[0]}")

                if (page.isEndingPage) {
                    LastPageContent(
                        story = story,
                        page = page,
                        onExploreStories = onExploreStories,
                        onCreateStory = onCreateStory,
                        onReturnToStory = { onReturnToStory(storyId) }
                    )
                } else {
                    RegularPageContent(
                        page = page,
                        onChoiceSelected = { choice ->
                            lastPageState.add(currentPageIndex)
                            val targetIndex = story.pages.indexOfFirst { it.id == choice.targetPageId }
                            if (targetIndex != -1) {
                                pageState.value = targetIndex
                            }
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

        // Прогресс чтения
        ReadingProgressIndicator(
            progress = (pageState.value + 1f) / story.pages.size,
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter)
        )

        // Кнопка назад
        if (lastPageState.isNotEmpty()) {
            IconButton(
                onClick = {
                    pageState.value = lastPageState.removeAt(lastPageState.lastIndex)
                },
                modifier = Modifier
                    .offset(x = 16.dp, y = 8.dp)
                    .windowInsetsPadding(WindowInsets.statusBars)
                    .size(48.dp)
                    .background(
                        color = MaterialTheme.colorScheme.surfaceContainerHigh,
                        shape = CircleShape
                    )
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Назад",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

@Composable
private fun RegularPageContent(
    page: Page,
    onChoiceSelected: (Choice) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .animateContentSize(),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // Текст страницы
        GlassCard {
            Text(
                text = page.pageText,
                style = MaterialTheme.typography.bodyLarge.copy(
                    lineHeight = 28.sp,
                    color = MaterialTheme.colorScheme.surfaceBright
                ),
                modifier = Modifier.fillMaxWidth().padding(10.dp)
            )
        }

        // Разделитель (только если есть выбор)
        if (page.choices.isNotEmpty()) {
            HorizontalDivider(
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f),
                thickness = 1.dp
            )
        }

        // Варианты выбора
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            page.choices.forEach { choice ->
                ChoiceButton(
                    text = choice.choiceText,
                    onClick = { onChoiceSelected(choice) }
                )
            }

        }
    }
}

@Composable
private fun LastPageContent(
    story: StoryFull,
    page: Page,
    onExploreStories: () -> Unit,
    onCreateStory: () -> Unit,
    onReturnToStory: () -> Unit,
    modifier: Modifier = Modifier
) {
    GlassCard {


    }
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        verticalArrangement = Arrangement.spacedBy(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Контент завершения

        GlassCard {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = Color.Transparent,
                        shape = RoundedCornerShape(24.dp)
                    )
                    .padding(32.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Иконка завершения
                Icon(
                    imageVector = Icons.Outlined.Star,
                    contentDescription = null,
                    modifier = Modifier.size(64.dp),
                    tint = MaterialTheme.extendedColors.star
                )

                // Заголовок
                Text(
                    text = "История завершена!",
                    style = MaterialTheme.typography.headlineMedium,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurface
                )

                // Текст страницы
                Text(
                    text = page.pageText,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        lineHeight = 28.sp,
                        textAlign = TextAlign.Center
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                // Рейтинг истории
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    RatingBar(
                        rating = story.averageRating,
                        modifier = Modifier.height(24.dp)
                    )
                    Text(
                        text = "%.1f".format(story.averageRating),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // Кнопки действий
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            FilledTonalButton(
                onClick = onExploreStories,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.filledTonalButtonColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            ) {
                Text(
                    text = "Исследовать другие истории",
                    style = MaterialTheme.typography.labelLarge
                )
            }

            OutlinedButton(
                onClick = onCreateStory,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                enabled = true,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
            ) {
                Text(
                    text = "Создать свою историю",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.extendedColors.onDarkBackground
                )
            }

            TextButton(
                onClick = onReturnToStory,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Вернуться к истории",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.extendedColors.onDarkBackground
                )
            }
        }
    }
}

@Preview
@Composable
fun Test() {
    ChoiceButton(
        text = "Вот тестовая страница",
        onClick = {},
    )
}

@Composable
private fun ChoiceButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val elevation by animateDpAsState(
        targetValue = if (isPressed) 1.dp else 2.dp,
        animationSpec = tween(durationMillis = 100)
    )

    ElevatedCard(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 56.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
        ),
        elevation = CardDefaults.elevatedCardElevation(
            defaultElevation = elevation,
            pressedElevation = 1.dp
        ),
        interactionSource = interactionSource
    ) {
        Row(modifier = Modifier
            .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween) {
            Text(
                text = text,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(16.dp),
                maxLines = 2
            )
            Icon(
                imageVector = Icons.AutoMirrored.Default.ArrowForward,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp)
            )
        }

    }
}

@Composable
private fun ReadingProgressIndicator(
    progress: Float,
    modifier: Modifier = Modifier
) {
    LinearProgressIndicator(
        progress = { progress },
        modifier = modifier
            .fillMaxWidth()
            .height(4.dp),
        color = MaterialTheme.colorScheme.primary,
        trackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
    )
}

@Composable
private fun BackButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    IconButton(
        onClick = onClick,
        modifier = modifier
            .size(48.dp)
            .background(
                color = MaterialTheme.colorScheme.surfaceContainerHigh,
                shape = CircleShape
            ),
        colors = IconButtonDefaults.iconButtonColors(
            contentColor = MaterialTheme.colorScheme.onSurface
        )
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = "Назад"
        )
    }
}

@Composable
private fun RatingBar(
    rating: Float,
    modifier: Modifier = Modifier
) {
    val filledStars = floor(rating).toInt()
    val hasHalfStar = rating - filledStars >= 0.5

    Row(modifier = modifier) {
        repeat(5) { index ->
            Icon(
                imageVector = when {
                    index < filledStars -> Icons.Filled.Star
                    index == filledStars && hasHalfStar -> Icons.Filled.Star
                    else -> Icons.Outlined.Star
                },
                contentDescription = null,
                tint = if (index < rating) MaterialTheme.extendedColors.star else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

// Вспомогательные компоненты
@Composable
private fun FullScreenLoader() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
private fun ErrorView(error: String?, onRetry: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = error ?: "Произошла ошибка",
            color = MaterialTheme.colorScheme.error,
            style = MaterialTheme.typography.bodyLarge
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onRetry) {
            Text("Повторить попытку")
        }
    }
}