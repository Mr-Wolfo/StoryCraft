package com.wolfo.storycraft.presentation.ui.features.story_list.components.filter

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.text.input.KeyboardCapitalization

@Composable
fun AuthorFilter(
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
        )
    }
}
