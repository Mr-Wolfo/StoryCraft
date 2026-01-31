package com.wolfo.storycraft.presentation.ui.features.story_list.components.filter

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.wolfo.storycraft.presentation.theme.spacing
import com.wolfo.storycraft.presentation.ui.features.story_list.SearchAndFilterViewModel

@Composable
fun ActiveFiltersRow(viewModel: SearchAndFilterViewModel) {
    val sortBy by viewModel.sortBy.collectAsState()
    val sortOrder by viewModel.sortOrder.collectAsState()
    val authorFilter by viewModel.authorFilter.collectAsState()
    val tagsFilter by viewModel.tagsFilter.collectAsState()

    val hasFilters = !authorFilter.isNullOrBlank() || !tagsFilter.isNullOrEmpty() || sortBy.isNotEmpty()

    if (!hasFilters) return

    LazyRow(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = MaterialTheme.spacing.extraSmall),
        contentPadding = PaddingValues(horizontal = MaterialTheme.spacing.medium),
        horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small),
        verticalAlignment = Alignment.CenterVertically
    ) {
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

        authorFilter?.let { author ->
            if (author.isNotBlank()) {
                item {
                    FilterInfoChip(text = "Автор: $author") { viewModel.updateAuthorFilter(null) }
                }
            }
        }

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
