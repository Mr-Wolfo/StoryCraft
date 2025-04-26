package com.wolfo.storycraft.presentation.features.story_view.reader

import android.util.Log
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class,
    ExperimentalAnimationApi::class
)
@Composable
fun StoryReaderScreen(
    storyId: Long,
    viewModel: StoryReaderViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val pageState = remember { mutableStateOf(0) }
    val lastPageState = remember { mutableListOf<Int>() }
    val scrollState = rememberScrollState()
    val context = LocalContext.current

    // Анимация перелистывания страниц
    val pageFlipProgress by animateFloatAsState(
        targetValue = if (uiState.isLoading) 0f else 1f,
        animationSpec = tween(600)
    )


    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.surfaceContainerLowest,
                        MaterialTheme.colorScheme.surfaceContainerHigh
                    )
                )
            )
    ) {
        val circleColor = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.05f)
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawCircle(
                color = circleColor,
                radius = size.maxDimension * 0.7f,
                center = Offset(size.width * 0.8f, size.height * 0.2f)
            )
        }

        Log.d("Image", "${uiState.story?.pages?.getOrNull(pageState.value)?.coverImageUrl}")

        AsyncImage(
            model = uiState.story?.pages?.getOrNull(pageState.value)?.coverImageUrl,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
        )

        // Основной контент
        Column(
            modifier = Modifier
                .fillMaxSize()
                    .windowInsetsPadding(WindowInsets.statusBars)
                .padding(horizontal = 24.dp)
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
            ) { currentPage ->
                val page = uiState.story?.pages?.getOrNull(currentPage)

                if (page != null) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .animateContentSize()
                            .padding(16.dp)
                            .background(
                                color = MaterialTheme.colorScheme.surfaceContainer,
                                shape = RoundedCornerShape(16.dp)
                            )
                            .border(
                                width = 1.dp,
                                color = MaterialTheme.colorScheme.outlineVariant,
                                shape = RoundedCornerShape(16.dp)
                            )
                            .padding(24.dp),
                        verticalArrangement = Arrangement.spacedBy(24.dp)
                    ) {
                        // Текст страницы
                        Text(
                            text = page.pageText,
                            style = MaterialTheme.typography.bodyLarge.copy(
                                lineHeight = 28.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )

                        // Разделитель
                        HorizontalDivider(
                            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f),
                            thickness = 1.dp
                        )

                        // Варианты выбора
                        Column(
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            page.choices.forEach { choice ->
                                ElevatedCard(
                                    onClick = {
                                        Log.d("CurrentPage", "Id: ${pageState.value}")
                                        Log.d("Choice", "TargetId: ${choice.targetPageId.toInt()}")
                                        lastPageState.add(pageState.value)
                                        pageState.value = choice.targetPageId.toInt()
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .heightIn(min = 56.dp),
                                    colors = CardDefaults.elevatedCardColors(
                                        containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
                                    ),
                                    elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp)
                                ) {
                                    Text(
                                        text = choice.choiceText,
                                        style = MaterialTheme.typography.bodyMedium,
                                        modifier = Modifier.padding(16.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

        // Прогресс-бар
        LinearProgressIndicator(
            progress = { (pageState.value + 1f) / (uiState.story?.pages?.size?.toFloat() ?: 1f) },
            modifier = Modifier
                .fillMaxWidth()
                .height(4.dp),
            color = MaterialTheme.colorScheme.primary,
            trackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
        )

        Spacer(Modifier)

        // Кнопка назад
        IconButton(
            onClick = {
                if (lastPageState.isNotEmpty()) {
                    pageState.value = lastPageState.last()
                    lastPageState.removeAt(lastPageState.lastIndex)
                }
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