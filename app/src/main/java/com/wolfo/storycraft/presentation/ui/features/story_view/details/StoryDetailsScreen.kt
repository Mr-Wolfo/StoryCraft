package com.wolfo.storycraft.presentation.ui.features.story_view.details

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.rememberNestedScrollInteropConnection
import androidx.compose.ui.unit.dp
import com.wolfo.storycraft.domain.model.story.StoryBaseInfo
import com.wolfo.storycraft.presentation.common.ErrorScreen
import com.wolfo.storycraft.presentation.common.LoadingBar
import com.wolfo.storycraft.presentation.common.StatusBottomMessage
import com.wolfo.storycraft.presentation.theme.spacing
import com.wolfo.storycraft.presentation.ui.components.AppCard
import com.wolfo.storycraft.presentation.ui.components.AppTopBar
import com.wolfo.storycraft.presentation.ui.components.FullScreenLoading
import com.wolfo.storycraft.presentation.ui.features.story_list.AppStatusBarUiState
import com.wolfo.storycraft.presentation.ui.features.story_view.details.components.ReviewsSection
import com.wolfo.storycraft.presentation.ui.features.story_view.details.components.StoryActions
import com.wolfo.storycraft.presentation.ui.features.story_view.details.components.StoryDescription
import com.wolfo.storycraft.presentation.ui.features.story_view.details.components.StoryHeader
import com.wolfo.storycraft.presentation.ui.features.story_view.details.components.StoryImage
import com.wolfo.storycraft.presentation.ui.features.story_view.details.components.StoryStats
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
        is StoryDetailsUiState.Loading -> FullScreenLoading()
        is StoryDetailsUiState.Error -> ErrorScreen(e = state.error.message ?: "Unknown Error")
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
                FullScreenLoading()
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
            AppTopBar(
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Назад")
                    }
                },
                actions = {
                    IconButton(onClick = onShareClick) {
                        Icon(Icons.Default.Share, contentDescription = "Поделиться")
                    }
                }
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
                top = paddingValues.calculateTopPadding() + MaterialTheme.spacing.small,
                bottom =  MaterialTheme.spacing.small,
            ),
            verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.extraSmall)
        ) {
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