package com.wolfo.storycraft.presentation.ui.features.story_list

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
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
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.InputChip
import androidx.compose.material3.InputChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.SuggestionChipDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.wolfo.storycraft.R
import com.wolfo.storycraft.domain.DataError
import com.wolfo.storycraft.domain.model.story.StoryBaseInfo
import com.wolfo.storycraft.domain.model.story.Tag
import com.wolfo.storycraft.domain.model.user.UserSimple
import com.wolfo.storycraft.presentation.common.BackgroundImage
import com.wolfo.storycraft.presentation.common.ErrorState
import com.wolfo.storycraft.presentation.common.StatusBottomMessage
import com.wolfo.storycraft.presentation.common.GlassCard
import com.wolfo.storycraft.presentation.common.LoadingBar
import com.wolfo.storycraft.presentation.common.StatusBarManager
import com.wolfo.storycraft.presentation.common.TagChip
import com.wolfo.storycraft.presentation.ui.utils.UiUtils
import com.wolfo.storycraft.presentation.common.formatNumber
import com.wolfo.storycraft.presentation.theme.extendedColors
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale
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
                                    Log.d("SLScreen", it)
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
private fun StoryListTopAppBar(
    searchQuery: String,
    filtersVisible: Boolean,
    onSearchQueryChanged: (String) -> Unit,
    onFiltersToggle: () -> Unit,
    scrollBehavior: TopAppBarScrollBehavior,
    viewModel: SearchAndFilterViewModel,
    modifier: Modifier = Modifier
) {

    val density = LocalDensity.current
    val secondRowHeight = 36.dp
    val secondRowHeightPx = with(density) { secondRowHeight.toPx() }

    SideEffect {
        if (scrollBehavior.state.heightOffsetLimit != -secondRowHeightPx) {
            scrollBehavior.state.heightOffsetLimit = -secondRowHeightPx
        }
    }

    val currentSecondRowHeight = secondRowHeight + with(density) {
        scrollBehavior.state.heightOffset.toDp()
    }

    GlassCard(
        modifier = modifier.fillMaxWidth(),
    ) {
        Column(
            modifier = Modifier
                .windowInsetsPadding(WindowInsets.statusBars)
                .padding(bottom = 4.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .padding(horizontal = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                SearchTextField(
                    value = searchQuery,
                    onValueChange = onSearchQueryChanged,
                    modifier = Modifier.weight(1f)
                )
                FilterToggleButton(
                    filtersVisible = filtersVisible,
                    onToggle = onFiltersToggle,
                    modifier = Modifier.size(40.dp)
                )
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(currentSecondRowHeight.coerceAtLeast(0.dp))
                    .clipToBounds()
                    .alpha((currentSecondRowHeight / secondRowHeight).coerceIn(0f, 1f))
            ) {
                ActiveFiltersRow(viewModel)
            }
        }
    }
}

@Composable
private fun ActiveFiltersRow(viewModel: SearchAndFilterViewModel) {
    val sortBy by viewModel.sortBy.collectAsState()
    val sortOrder by viewModel.sortOrder.collectAsState()
    val authorFilter by viewModel.authorFilter.collectAsState()
    val tagsFilter by viewModel.tagsFilter.collectAsState()

    val hasFilters = !authorFilter.isNullOrBlank() || !tagsFilter.isNullOrEmpty() || sortBy.isNotEmpty()

    if (!hasFilters) return

    LazyRow(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 2.dp),
        contentPadding = PaddingValues(horizontal = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Чип сортировки
        item {
            val sortLabel = when(sortBy) {
                "published_time" -> "По дате"
                "rating" -> "По рейтингу"
                "views" -> "По просмотрам"
                "title" -> "По названию"
                else -> "Сортировка"
            }
            val orderIcon = if (sortOrder == "asc") "↑" else "↓"

            FilterInfoChip(text = "$sortLabel $orderIcon")
        }

        // Чип автора
        authorFilter?.let { author ->
            if (author.isNotBlank()) {
                item {
                    FilterInfoChip(text = "Автор: $author") { viewModel.updateAuthorFilter(null) }
                }
            }
        }

        // Чип тегов
        tagsFilter?.let { tags ->
            if (tags.isNotEmpty()) {
                tags.forEach {
                    item {
                        FilterInfoChip(text = it) { viewModel.updateTagsFilter(tags - it) }
                    }
                }
            }
        }
    }
}

@Composable
private fun FilterInfoChip(
    text: String,
    onClick: () -> Unit = {}
) {
    Surface(
        shape = CircleShape,
        color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.7f),
        modifier = Modifier
            .height(26.dp)
            .clickable {
                onClick()
            }
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SearchTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }

    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier
            .height(40.dp),
        interactionSource = interactionSource,
        singleLine = true,
        textStyle = MaterialTheme.typography.bodyMedium.copy(
            color = MaterialTheme.extendedColors.oppositeMain
        ),
        cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
        decorationBox = { innerTextField ->
            TextFieldDefaults.DecorationBox(
                value = value,
                innerTextField = innerTextField,
                enabled = true,
                singleLine = true,
                visualTransformation = VisualTransformation.None,
                interactionSource = interactionSource,
                placeholder = {
                    Text(
                        "Поиск историй...",
                        style = MaterialTheme.typography.bodyMedium
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                },
                shape = MaterialTheme.shapes.large,
                colors = TextFieldDefaults.colors(
                    unfocusedContainerColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    focusedContainerColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    unfocusedIndicatorColor = MaterialTheme.colorScheme.outlineVariant,
                    focusedIndicatorColor = Color.Transparent
                ),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp),
                container = {
                    OutlinedCard(
                        shape = MaterialTheme.shapes.large,
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                        colors = CardDefaults.outlinedCardColors(
                            containerColor = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    ) { Box(Modifier.fillMaxSize()) }
                }
            )
        }
    )
}

@Composable
private fun FilterToggleButton(
    filtersVisible: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    IconButton(
        onClick = onToggle,
        modifier = modifier.size(48.dp),
        colors = IconButtonDefaults.iconButtonColors(
            containerColor = if (filtersVisible) {
                MaterialTheme.colorScheme.primaryContainer
            } else {
                Color.Transparent
            }
        )
    ) {
        Icon(
            imageVector = Icons.Default.Menu,
            contentDescription = if (filtersVisible) {
                "Скрыть фильтры"
            } else {
                "Показать фильтры"
            },
            tint = if (filtersVisible) {
                MaterialTheme.colorScheme.onPrimaryContainer
            } else {
                MaterialTheme.colorScheme.onSurfaceVariant
            }
        )
    }
}

@Composable
private fun FiltersPanel(
    visible: Boolean,
    viewModel: SearchAndFilterViewModel,
    modifier: Modifier = Modifier
) {
    val sortBy by viewModel.sortBy.collectAsState()
    val sortOrder by viewModel.sortOrder.collectAsState()
    val authorFilter by viewModel.authorFilter.collectAsState()
    val tagsFilter by viewModel.tagsFilter.collectAsState()

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn() + expandVertically(),
        exit = fadeOut() + shrinkVertically(),
        modifier = modifier
    ) {
        GlassCard(
            modifier = Modifier.fillMaxWidth(),
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                SortSection(
                    sortBy = sortBy,
                    sortOrder = sortOrder,
                    onSortByChanged = viewModel::updateSortBy,
                    onSortOrderChanged = viewModel::updateSortOrder
                )

                AuthorFilter(
                    currentAuthor = authorFilter,
                    onAuthorChanged = viewModel::updateAuthorFilter
                )

                TagsFilter(
                    currentTags = tagsFilter,
                    onTagsChanged = viewModel::updateTagsFilter
                )
            }
        }
    }
}

@Composable
private fun SortSection(
    sortBy: String,
    sortOrder: String,
    onSortByChanged: (String) -> Unit,
    onSortOrderChanged: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = "Сортировка",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 8.dp)
        )
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    SortChip(
                        label = "По дате",
                        selected = sortBy == "published_time",
                        onClick = { onSortByChanged("published_time") }
                    )

                    SortChip(
                        label = "По рейтингу",
                        selected = sortBy == "rating",
                        onClick = { onSortByChanged("rating") }
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    SortChip(
                        label = "По просмотрам",
                        selected = sortBy == "views",
                        onClick = { onSortByChanged("views") }
                    )

                    SortChip(
                        label = "По названию",
                        selected = sortBy == "title",
                        onClick = { onSortByChanged("title") }
                    )
                }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            SortOrderChip(
                label = "По возрастанию",
                selected = sortOrder == "asc",
                onClick = { onSortOrderChanged("asc") }
            )

            SortOrderChip(
                label = "По убыванию",
                selected = sortOrder == "desc",
                onClick = { onSortOrderChanged("desc") }
            )
        }
    }
}

@Composable
private fun SortChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    StoryFilterChip(
        label = label,
        selected = selected,
        onClick = onClick,
        modifier = modifier
    )
}

@Composable
private fun SortOrderChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    StoryFilterChip(
        label = label,
        selected = selected,
        onClick = onClick,
        modifier = modifier
    )
}

@Composable
private fun StoryFilterChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = {
            Text(
                text = label,
                style = MaterialTheme.typography.labelLarge
            )
        },
        modifier = modifier,
        shape = MaterialTheme.shapes.medium,
        border = null,
        colors = FilterChipDefaults.filterChipColors(
            containerColor = MaterialTheme.colorScheme.onPrimaryContainer,
            labelColor = MaterialTheme.colorScheme.onSurfaceVariant,
            selectedContainerColor = MaterialTheme.colorScheme.secondary,
            selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer,
            disabledContainerColor = MaterialTheme.extendedColors.main.copy(alpha = 0.5f),
            disabledLabelColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
        )
    )
}


@Composable
private fun AuthorFilter(
    currentAuthor: String?,
    onAuthorChanged: (String?) -> Unit,
    modifier: Modifier = Modifier
) {
    var authorText by remember { mutableStateOf(currentAuthor ?: "") }

    Column(modifier = modifier) {
        OutlinedTextField(
            value = authorText,
            onValueChange = { authorText = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Фильтр по автору") },
            trailingIcon = {
                if (authorText.isNotEmpty()) {
                    IconButton(
                        onClick = {
                            authorText = ""
                            onAuthorChanged(null)
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Очистить"
                        )
                    }
                }
            },
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Done,
                capitalization = KeyboardCapitalization.Words
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    onAuthorChanged(authorText.takeIf { it.isNotBlank() })
                }
            ),
            singleLine = true,
            shape = MaterialTheme.shapes.medium,
            colors = TextFieldDefaults.colors(
                unfocusedContainerColor = Color.Transparent,
                focusedContainerColor = Color.Transparent
            )
        )
    }
}

@Composable
private fun TagsFilter(
    currentTags: List<String>?,
    onTagsChanged: (List<String>?) -> Unit,
    modifier: Modifier = Modifier
) {
    var tagsText by remember { mutableStateOf(currentTags?.joinToString(", ") ?: "") }

    Column(modifier = modifier) {
        OutlinedTextField(
            value = tagsText,
            onValueChange = { tagsText = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Фильтр по тегам") },
            supportingText = { Text("Разделяйте теги запятыми") },
            trailingIcon = {
                if (tagsText.isNotEmpty()) {
                    IconButton(
                        onClick = {
                            tagsText = ""
                            onTagsChanged(null)
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Очистить"
                        )
                    }
                }
            },
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    onTagsChanged(
                        tagsText.split(",")
                            .map { it.trim() }
                            .filter { it.isNotBlank() }
                            .takeIf { it.isNotEmpty() }
                    )
                }
            ),
            singleLine = false,
            shape = MaterialTheme.shapes.medium,
            colors = TextFieldDefaults.colors(
                unfocusedContainerColor = Color.Transparent,
                focusedContainerColor = Color.Transparent
            )
        )

        if (!currentTags.isNullOrEmpty()) {
            FlowRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                currentTags.forEach { tag ->
                    InputChip(
                        selected = true,
                        onClick = {
                            onTagsChanged(currentTags - tag)
                        },
                        label = { Text(tag) },
                        trailingIcon = {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Удалить тег",
                                modifier = Modifier.size(16.dp)
                            )
                        },
                        colors = InputChipDefaults.inputChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.tertiaryContainer,
                            selectedLabelColor = MaterialTheme.colorScheme.onTertiaryContainer
                        )
                    )
                }
            }
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
                top = contentPadding.calculateTopPadding() + 8.dp,
                bottom = contentPadding.calculateBottomPadding() + 16.dp,
            ),
            horizontalArrangement = Arrangement.spacedBy(2.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
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

                        Log.d("Loaded Ad", loadedAd?.adAssets?.title ?: "nothing")

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

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun StoryListItem(
    story: StoryBaseInfo,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val dateTime = remember { UiUtils.toLocaleDateTime(story.publishedTime) }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(300.dp)
            .clickable(onClick = onClick),
        shape = MaterialTheme.shapes.large,
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            AsyncImage(
                model = story.coverImageUrl,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
                placeholder = painterResource(R.drawable.ic_launcher_background),
                error = painterResource(R.drawable.story_placeholder)
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.7f)),
                            startY = 0.4f
                        )
                    )
            )

            Column(modifier = Modifier.fillMaxSize()) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(16.dp)
                ) {
                    Text(
                        text = story.title,
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        ),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = story.description ?: "",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = Color.White.copy(alpha = 0.9f)
                        ),
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp)
                        .background(color = MaterialTheme.colorScheme.primaryContainer)
                        .padding(horizontal = 12.dp)
                        .padding(top = 4.dp, bottom = 8.dp),
                    verticalArrangement = Arrangement.Center
                ) {
                    Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.CenterStart) {
                        if (story.tags.isNotEmpty()) {
                            FlowRow(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                maxItemsInEachRow = 2,
                                maxLines = 1
                            ) {
                                story.tags.take(2).forEach { tag ->
                                    TagChip(tag = tag.name)
                                }
                            }
                        } else {
                            TagChip(tag = "Тегов нет")
                        }
                    }

                    StoryMetaInfo(
                        rating = story.averageRating,
                        views = story.viewCount,
                        date = dateTime
                    )
                }
            }
        }
    }
}


@Composable
fun StoryMetaInfo(
    rating: Float,
    views: Int,
    date: LocalDateTime,
    modifier: Modifier = Modifier
) {

    val formattedDate = remember(date) {
        DateTimeFormatter
            .ofPattern("dd.MM.yy", Locale.getDefault())
            .format(date)
    }
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            
            Column {
                // Дата
                Text(
                    text = formattedDate,
                    style = MaterialTheme.typography.labelMedium
                )
                Spacer(modifier = Modifier.height(4.dp))

                // Просмотры
                Text(
                    text = "${formatNumber(views)} просмотров",
                    style = MaterialTheme.typography.labelMedium
                )
            }

            Row{
                // Рейтинг
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = null,
                    tint = MaterialTheme.extendedColors.star,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "%.1f".format(rating),
                    style = MaterialTheme.typography.labelMedium
                )
            }
        }
    }
}

@Composable
fun PremiumBadge(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .padding(8.dp)
            .background(
                color = MaterialTheme.colorScheme.primary,
                shape = CircleShape
            )
            .size(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Default.Star,
            contentDescription = "Премиум",
            tint = MaterialTheme.colorScheme.onPrimary,
            modifier = Modifier.size(16.dp)
        )
    }
}

@Composable
private fun FullScreenLoading() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
private fun EmptyState() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.Warning,
            contentDescription = null,
            modifier = Modifier.size(48.dp),
            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Историй пока нет",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        )
    }
}

@Preview
@Composable
fun ItemPreview() {
    StoryListItem(
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
        onClick = {}
    )
}