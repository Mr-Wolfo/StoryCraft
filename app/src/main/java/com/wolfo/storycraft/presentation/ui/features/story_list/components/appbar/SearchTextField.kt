package com.wolfo.storycraft.presentation.ui.features.story_list.components.appbar

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.wolfo.storycraft.presentation.theme.spacing
import com.wolfo.storycraft.presentation.ui.components.AppCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchTextField(
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
            color = MaterialTheme.colorScheme.onSurfaceVariant
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
                contentPadding = PaddingValues(horizontal = MaterialTheme.spacing.small, vertical = 0.dp),
                container = {
                    AppCard(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    ) { Box(Modifier.fillMaxSize()) }
                }
            )
        }
    )
}