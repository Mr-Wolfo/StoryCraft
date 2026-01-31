package com.wolfo.storycraft.presentation.ui.features.story_list.components.filter

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.wolfo.storycraft.presentation.theme.spacing

@Composable
fun SortSection(
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
            modifier = Modifier.padding(bottom = MaterialTheme.spacing.small)
        )
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small)
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
                horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small)
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
            horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small),
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