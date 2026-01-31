package com.wolfo.storycraft.presentation.ui.features.story_list.components.appbar

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.wolfo.storycraft.presentation.theme.spacing
import com.wolfo.storycraft.presentation.ui.components.AppCard
import com.wolfo.storycraft.presentation.ui.features.story_list.SearchAndFilterViewModel
import com.wolfo.storycraft.presentation.ui.features.story_list.components.filter.ActiveFiltersRow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StoryListTopAppBar(
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

    AppCard(
        modifier = modifier.fillMaxWidth(),
    ) {
        Column(
            modifier = Modifier
                .windowInsetsPadding(WindowInsets.statusBars)
                .padding(bottom = MaterialTheme.spacing.extraSmall)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .padding(horizontal = MaterialTheme.spacing.small),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small)
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
