package com.wolfo.storycraft.presentation.ui.features.story_list

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import com.wolfo.storycraft.presentation.common.ErrorState
import com.wolfo.storycraft.presentation.common.StatusBarManager
import com.wolfo.storycraft.presentation.theme.spacing
import com.wolfo.storycraft.presentation.ui.components.FullScreenLoading
import com.wolfo.storycraft.presentation.ui.features.story_list.components.EmptyState
import com.wolfo.storycraft.presentation.ui.features.story_list.components.appbar.StoryListTopAppBar
import com.wolfo.storycraft.presentation.ui.features.story_list.components.filter.FiltersPanel
import com.wolfo.storycraft.presentation.ui.features.story_list.components.list.NativeAdCard
import com.wolfo.storycraft.presentation.ui.features.story_list.components.list.StoryListItem
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StoryListScreen(
    viewModel: StoryListViewModel = koinViewModel(),
    searchViewModel: SearchAndFilterViewModel = koinViewModel(),
    navPadding: PaddingValues,
    onStoryClick: (String) -> Unit,
) {
    val uiState by viewModel.uiState.collectAsState()
    val appStatusBarUiState by viewModel.appStatusBarUiState.collectAsState()

//    val scrollState = rememberScrollState()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())

    val coroutineScope = rememberCoroutineScope()

    val searchQuery by searchViewModel.searchQuery.collectAsState()
    val filtersVisible by searchViewModel.filtersVisible.collectAsState()
    val currentQuery by searchViewModel.currentQuery.collectAsState()

    val mixedList by viewModel.mixedList.collectAsState()

    LaunchedEffect(currentQuery) {
        coroutineScope.launch {
            viewModel.loadStories(currentQuery)
        }
    }

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            StoryListTopAppBar(
                searchQuery = searchQuery,
                filtersVisible = filtersVisible,
                onSearchQueryChanged = searchViewModel::updateSearchQuery,
                onFiltersToggle = searchViewModel::toggleFilters,
                scrollBehavior = scrollBehavior,
                viewModel = searchViewModel
            )
        }
    ) { paddingValues ->

        Box(modifier = Modifier
            .fillMaxSize()
            .padding(bottom = navPadding.calculateBottomPadding())
            ) {
                when (val state = uiState) {
                    StoryListUiState.Loading -> FullScreenLoading()
                    is StoryListUiState.Error -> ErrorState(
                        error = state.error,
                        onRetry = { viewModel.loadStories(currentQuery) }
                    )
                    is StoryListUiState.Success -> {
                        if (state.stories.isEmpty()) {
                            EmptyState()
                        } else {
                            StoryListContent(
                                stories = mixedList,
                                contentPadding = paddingValues,
                                onRefresh = {
                                    viewModel.loadStories()
                                    viewModel.loadAds() },
                                onStoryClick = {
                                    onStoryClick(it)}
                            )
                        }
                    }
                    StoryListUiState.Idle -> {}
                }

            FiltersPanel(
                visible = filtersVisible,
                viewModel = searchViewModel,
                modifier = Modifier.padding(top = paddingValues.calculateTopPadding() + 8.dp)
            )

            StatusBarManager(
                appStatusBarUiState = appStatusBarUiState,
                onRetry = { viewModel.loadStories() }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun StoryListContent(
    stories: List<ListItem>,
    contentPadding: PaddingValues,
    onRefresh: () -> Unit,
    onStoryClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    PullToRefreshBox(
        isRefreshing = false,
        onRefresh = onRefresh,
        modifier = modifier
    ) {
        LazyVerticalGrid(
            modifier = Modifier.fillMaxSize(),
            columns = GridCells.Adaptive(minSize = 160.dp),
            state = rememberLazyGridState(),
            contentPadding = PaddingValues(
                top = contentPadding.calculateTopPadding() + MaterialTheme.spacing.small,
                bottom = contentPadding.calculateBottomPadding() + MaterialTheme.spacing.medium,
            ),
            horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.extraSmall/2),
            verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.extraSmall)
        ) {
            items(
                items = stories,
                key = { item ->
                    when(item) {
                        is ListItem.AdItem -> "ad_${item.nativeAd?.hashCode() ?: UUID.randomUUID()}"
                        is ListItem.StoryItem -> item.story.id
                    }
                },
                span = { item ->
                    when(item) {
                        is ListItem.AdItem -> GridItemSpan(2)
                        is ListItem.StoryItem -> GridItemSpan(1)
                    }
                }
            ) { item ->
                when(item) {
                    is ListItem.AdItem -> {

                        val loadedAd by rememberUpdatedState(item.nativeAd)

                        loadedAd?.let {
                            NativeAdCard(
                                nativeAd = it,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(200.dp)
                            )
                        }
                    }
                    is ListItem.StoryItem -> {
                        StoryListItem(
                            story = item.story,
                            onClick = { onStoryClick(item.story.id) }
                        )
                    }
                }
            }
        }
    }
}
