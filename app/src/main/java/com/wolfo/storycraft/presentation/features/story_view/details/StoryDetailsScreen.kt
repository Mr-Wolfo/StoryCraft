package com.wolfo.storycraft.presentation.features.story_view.details

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
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Star
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.wolfo.storycraft.domain.model.StoryBase
import com.wolfo.storycraft.presentation.common.CustomScrollableColumn
import com.wolfo.storycraft.presentation.common.Error
import com.wolfo.storycraft.presentation.common.GlassCard
import com.wolfo.storycraft.presentation.common.Loading
import com.wolfo.storycraft.presentation.common.PremiumInfoChip
import com.wolfo.storycraft.presentation.common.formatNumber
import org.koin.androidx.compose.koinViewModel

@Composable
fun StoryDetailsScreen(
    storyId: Long?,
    viewModel: StoryDetailsViewModel = koinViewModel(),
    onReadStory: (Long) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val loadFullUiState by viewModel.loadFullUiState.collectAsState()

    when {
        uiState.isLoading -> Loading()
        uiState.error != null -> Error(uiState.error!!)
        uiState.story != null -> StoryDetails(story = uiState.story!!, onReadStory =  {
            viewModel.loadStoryFullById().run {
                if(loadFullUiState.success)
                    onReadStory(it)
            }
        })
        else -> { viewModel.attemptLoadStory()
            Text(text = "Прочитайте чужую историю или создайте свою!")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun StoryDetails(
    story: StoryBase,
    onReadStory: (Long) -> Unit,
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
                )
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
                            story.tags?.forEach { tag ->
                                ElevatedFilterChip(
                                    selected = false,
                                    onClick = {},
                                    label = {
                                        Text(
                                            text = tag,
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
                }
            }
        }
    }
}
