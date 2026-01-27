package com.wolfo.storycraft.presentation.ui.features.story_view.details

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeightIn
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.rememberNestedScrollInteropConnection
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.wolfo.storycraft.domain.model.story.StoryBaseInfo
import com.wolfo.storycraft.presentation.common.Error
import com.wolfo.storycraft.presentation.common.GlassCard
import com.wolfo.storycraft.presentation.common.StatusBottomMessage
import com.wolfo.storycraft.presentation.common.Loading
import com.wolfo.storycraft.presentation.common.LoadingBar
import com.wolfo.storycraft.presentation.ui.features.story_list.AppStatusBarUiState
import com.wolfo.storycraft.presentation.ui.features.story_view.details.components.DetailsScreenBackground
import com.wolfo.storycraft.presentation.ui.features.story_view.details.components.ReviewsSection
import com.wolfo.storycraft.presentation.ui.features.story_view.details.components.StoryActions
import com.wolfo.storycraft.presentation.ui.features.story_view.details.components.StoryDescription
import com.wolfo.storycraft.presentation.ui.features.story_view.details.components.StoryHeader
import com.wolfo.storycraft.presentation.ui.features.story_view.details.components.StoryImage
import com.wolfo.storycraft.presentation.ui.features.story_view.details.components.StoryStats
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.rememberHazeState
import org.koin.androidx.compose.koinViewModel

@Composable
fun StoryDetailsScreen(
    storyId: String?,
    viewModel: StoryDetailsViewModel = koinViewModel(),
    navPadding: PaddingValues,
    onReadStory: (String) -> Unit,
    onNavigateToCreateStory: () -> Unit,
    onNavigateToStoryList: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val loadFullUiState by viewModel.loadFullState.collectAsState()
    val reviewsUiState by viewModel.loadReviewsState.collectAsState()
    val appStatusBarUiState by viewModel.appStatusBarUiState.collectAsState()

    LaunchedEffect(loadFullUiState) {
        if(loadFullUiState is FullStoryLoadState.Success) {
            storyId?.let { onReadStory(it) }
            viewModel.resetLoadState()
        }
    }

    when(val state = uiState) {
        is StoryDetailsUiState.Loading -> Loading()
        is StoryDetailsUiState.Error -> Error(e = state.error.message ?: "Unknown Error")
        is StoryDetailsUiState.Success ->  StoryDetailsContent(
            story = state.data,
            reviewsState = reviewsUiState,
            currentUserId = viewModel.currentUserId.collectAsState().value,
            navPadding = navPadding,
            onReadClick = { onReadStory(state.data.id) },
            onBackClick = { onNavigateToStoryList() },
            onShareClick = { /* TODO */ },
            onFavoriteClick = { /* TODO */ },
            onDeleteReview = { reviewId -> viewModel.deleteReview(reviewId) }
        )
        is StoryDetailsUiState.Idle -> {
            if (storyId == null) {
                EmptyStoryPlaceholder(
                    onExploreStories = onNavigateToStoryList,
                    onCreateStory = onNavigateToCreateStory
                )
            } else {
                Loading()
            }
        }
    }

    // Статус бар
    when(val barState = appStatusBarUiState) {
        is AppStatusBarUiState.Idle -> { }
        is AppStatusBarUiState.Loading -> {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .windowInsetsPadding(WindowInsets.navigationBars),
                contentAlignment = Alignment.BottomCenter
            ) {
                LoadingBar(isVisible = true)
            }
        }
        is AppStatusBarUiState.Error -> {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .windowInsetsPadding(WindowInsets.navigationBars),
                contentAlignment = Alignment.BottomCenter
            ) {
                StatusBottomMessage(
                    message = barState.error.message ?: "Unknown error",
                    isVisible = true
                ) {  }
            }
        }
        else -> { }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StoryDetailsContent(
    story: StoryBaseInfo,
    reviewsState: StoryReviewsUiState<*>,
    currentUserId: String?,
    navPadding: PaddingValues,
    onReadClick: () -> Unit,
    onBackClick: () -> Unit,
    onShareClick: () -> Unit,
    onFavoriteClick: () -> Unit,
    onDeleteReview: (String) -> Unit,
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                modifier = Modifier
                    .clip(shape = MaterialTheme.shapes.extraLarge)
                    .background(color = MaterialTheme.colorScheme.primaryContainer)
                    .statusBarsPadding()
                    .height(45.dp),
                title = { },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Назад")
                    }
                },
                actions = {
                    IconButton(onClick = onShareClick) {
                        Icon(Icons.Default.Share, contentDescription = "Поделиться")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onBackground,
                    navigationIconContentColor = MaterialTheme.colorScheme.onBackground,
                    actionIconContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        },
    ) { paddingValues ->

        val nestedScrollInteropConnection = rememberNestedScrollInteropConnection()

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = navPadding.calculateBottomPadding())
                .nestedScroll(nestedScrollInteropConnection),
            contentPadding = PaddingValues(
                top = paddingValues.calculateTopPadding() + 8.dp,
                bottom =  8.dp,
            ),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {

           /* item {
                HorizontalDivider(
                    modifier = Modifier
                        .fillMaxWidth(0.5f)
                        .clip(MaterialTheme.shapes.extraLarge),
                    color = MaterialTheme.colorScheme.primaryContainer,
                    thickness = 8.dp
                )
            }*/

            item {
                StoryImage(
                    imageUrl = story.coverImageUrl
                )
            }

            item {
                StoryHeader(
                    story = story
                )
            }

            if (!story.description.isNullOrBlank()) {
                item {
                    StoryDescription(
                        description =  story.description!!)
                }
            }

            item {
                StoryActions(
                    isFavorite = false,
                    onReadClick = onReadClick,
                    onFavoriteClick = onFavoriteClick
                )
            }

            item {
                StoryStats(
                    pageCount = 44, // TODO: Брать реальное кол-во страниц (сделать на сервере подсчёт)
                    viewCount = story.viewCount,
                    rating = story.averageRating
                )
            }

            item {
                ReviewsSection(
                    reviewsState = reviewsState,
                    currentUserId = currentUserId
                )
            }
        }
    }
}