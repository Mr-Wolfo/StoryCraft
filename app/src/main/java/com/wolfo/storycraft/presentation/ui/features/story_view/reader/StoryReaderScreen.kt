package com.wolfo.storycraft.presentation.ui.features.story_view.reader

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.Crossfade
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wolfo.storycraft.domain.model.story.Choice
import com.wolfo.storycraft.domain.model.story.StoryFull
import com.wolfo.storycraft.presentation.common.ErrorScreen
import com.wolfo.storycraft.presentation.theme.spacing
import com.wolfo.storycraft.presentation.ui.components.AppCard
import com.wolfo.storycraft.presentation.ui.components.FullScreenLoading
import com.wolfo.storycraft.presentation.ui.features.story_view.details.components.ImageBackgroundScreen
import com.wolfo.storycraft.presentation.ui.features.story_view.details.components.StoryImage
import com.wolfo.storycraft.presentation.ui.features.story_view.reader.components.BackButton
import com.wolfo.storycraft.presentation.ui.features.story_view.reader.components.LastPageContent
import com.wolfo.storycraft.presentation.ui.features.story_view.reader.components.ReadingProgressIndicator
import com.wolfo.storycraft.presentation.ui.features.story_view.reader.components.RegularPageContent
import org.koin.androidx.compose.koinViewModel

@OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalFoundationApi::class,
    ExperimentalAnimationApi::class
)
@Composable
fun StoryReaderScreen(
    storyId: String,
    navPadding: PaddingValues,
    viewModel: StoryReaderViewModel = koinViewModel(),
    onExploreStories: () -> Unit,
    onCreateStory: () -> Unit,
    onReturnToStory: (String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    val currentPageIndex by viewModel.currentPageIndex.collectAsState()
    val canGoBack by viewModel.canGoBack.collectAsState()

    Crossfade(targetState = uiState, label = "state_fade") { state ->
        when (state) {

            is StoryReaderUiState.Loading -> FullScreenLoading()

            is StoryReaderUiState.Error -> ErrorScreen(
                e = state.error.message,
                onRetry = { viewModel.attemptLoadStory() }
            )

            is StoryReaderUiState.Success -> {
                StoryReaderContent(
                    story = state.data,
                    currentPageIndex = currentPageIndex,
                    canGoBack = canGoBack,
                    navPadding = navPadding,
                    onChoiceSelected = { viewModel.navigateToPage(it.targetPageId) },
                    onBackPressed = { viewModel.goBack() },
                    onExploreStories = onExploreStories,
                    onCreateStory = onCreateStory,
                    onReturnToStory = { onReturnToStory(storyId) }
                )
            }

            else -> {}
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun StoryReaderContent(
    story: StoryFull,
    currentPageIndex: Int,
    canGoBack: Boolean,
    navPadding: PaddingValues,
    onChoiceSelected: (Choice) -> Unit,
    onBackPressed: () -> Unit,
    onExploreStories: () -> Unit,
    onCreateStory: () -> Unit,
    onReturnToStory: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    LaunchedEffect(currentPageIndex) {
        scrollState.scrollTo(0)
    }

    Scaffold(
        topBar = {
            AppCard {
                CenterAlignedTopAppBar(
                    modifier = Modifier
                        .statusBarsPadding()
                        .height(45.dp),
                    title = {
                        Text(
                            text = "Страница №${currentPageIndex + 1}",
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.titleMedium.copy(
                                lineHeight = 28.sp,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            ),
                            modifier = Modifier.fillMaxWidth().padding(MaterialTheme.spacing.small)
                        )
                    },
                    navigationIcon = {
                        Row {
                            if (canGoBack) {
                                IconButton(onClick = onBackPressed) {
                                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Назад")
                                }
                            }
                            IconButton(onClick = onReturnToStory) {
                                Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = "Назад к истории")
                            }
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                        navigationIconContentColor = MaterialTheme.colorScheme.onSurface
                    )
                )
            }
        },
    ) { paddingValues ->

        Column(modifier = modifier.fillMaxSize()
            .verticalScroll(scrollState)
            .padding(
                bottom = navPadding.calculateBottomPadding(),
            ),
            verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.extraSmall)
        ) {
            val currentPage = story.pages.getOrNull(currentPageIndex)

            Spacer(modifier = Modifier.height(paddingValues.calculateTopPadding() + MaterialTheme.spacing.small))

            AnimatedContent(
                targetState = currentPageIndex,
                transitionSpec = {
                    fadeIn(tween(500)) togetherWith fadeOut(tween(500))
                },
                label = "page_transition"
            ) { index ->
                val page = story.pages.getOrNull(index) ?: return@AnimatedContent

                if (page.isEndingPage) {
                    LastPageContent(
                        story = story,
                        page = page,
                        onExploreStories = onExploreStories,
                        onCreateStory = onCreateStory,
                        onReturnToStory = onReturnToStory
                    )
                } else {
                    RegularPageContent(
                        page = page,
                        pageNumber = index + 1,
                        onChoiceSelected = onChoiceSelected
                    )
                }
            }
            Spacer(modifier = Modifier.height(MaterialTheme.spacing.small))
        }
        // Прогресс
        ReadingProgressIndicator(
            progress = (currentPageIndex + 1f) / story.pages.size,
            modifier = Modifier.padding(top = paddingValues.calculateTopPadding())
        )
    }
}