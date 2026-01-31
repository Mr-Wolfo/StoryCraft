package com.wolfo.storycraft.presentation.ui.features.story_view.details

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.wolfo.storycraft.presentation.theme.spacing

@Composable
internal fun EmptyStoryPlaceholder(
    onExploreStories: () -> Unit,
    onCreateStory: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(MaterialTheme.spacing.large),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.Menu,
            contentDescription = null,
            modifier = Modifier.size(100.dp),
            tint = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(MaterialTheme.spacing.large))

        Text(
            text = "История не выбрана",
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(MaterialTheme.spacing.medium))

        Text(
            text = "Выберите историю из коллекции или создайте свою собственную",
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(MaterialTheme.spacing.extraLarge))

        Button(
            onClick = onExploreStories,
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.medium
        ) {
            Text(text = "Исследовать истории")
        }

        Spacer(modifier = Modifier.height(MaterialTheme.spacing.medium))

        OutlinedButton(
            onClick = onCreateStory,
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.medium
        ) {
            Text(text = "Создать свою историю")
        }
    }
}