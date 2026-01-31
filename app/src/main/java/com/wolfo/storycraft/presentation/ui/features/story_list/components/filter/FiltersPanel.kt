package com.wolfo.storycraft.presentation.ui.features.story_list.components.filter

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.wolfo.storycraft.presentation.theme.spacing
import com.wolfo.storycraft.presentation.ui.components.AppCard
import com.wolfo.storycraft.presentation.ui.features.story_list.SearchAndFilterViewModel


@Composable
fun FiltersPanel(
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
        AppCard(
            modifier = Modifier.fillMaxWidth(),
        ) {
            Column(
                modifier = Modifier.padding(MaterialTheme.spacing.medium),
                verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small)
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