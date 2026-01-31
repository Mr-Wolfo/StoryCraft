package com.wolfo.storycraft.presentation.ui.features.story_list.components.filter

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.InputChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.wolfo.storycraft.presentation.theme.spacing

@Composable
fun TagsFilter(
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
            shape = MaterialTheme.shapes.medium
        )

        if (!currentTags.isNullOrEmpty()) {
            FlowRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = MaterialTheme.spacing.small),
                horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.extraSmall),
                verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.extraSmall)
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
                        }
                    )
                }
            }
        }
    }
}
