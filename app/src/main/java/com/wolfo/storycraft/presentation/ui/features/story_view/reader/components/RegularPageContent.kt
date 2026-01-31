package com.wolfo.storycraft.presentation.ui.features.story_view.reader.components

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wolfo.storycraft.domain.model.story.Choice
import com.wolfo.storycraft.domain.model.story.Page
import com.wolfo.storycraft.presentation.theme.spacing
import com.wolfo.storycraft.presentation.ui.components.AppCard
import com.wolfo.storycraft.presentation.ui.features.story_view.details.components.StoryImage

@Composable
fun RegularPageContent(
    page: Page,
    pageNumber: Int,
    onChoiceSelected: (Choice) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .animateContentSize(),
        verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small)
    ) {

        StoryImage(imageUrl = page.imageUrl)

        // Текст страницы
        AppCard {
            Text(
                text = page.pageText,
                style = MaterialTheme.typography.bodyLarge.copy(
                    lineHeight = 28.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                ),
                modifier = Modifier.fillMaxWidth().padding(MaterialTheme.spacing.medium)
            )
        }
/*
        if (page.choices.isNotEmpty()) {
            HorizontalDivider(
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f),
                thickness = 1.dp
            )
        }*/

        // Варианты выбора
        AppCard {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth().padding(MaterialTheme.spacing.extraSmall)
            ) {
                page.choices.forEach { choice ->
                    ChoiceButton(
                        text = choice.choiceText,
                        onClick = { onChoiceSelected(choice) }
                    )
                }
            }
        }
    }
}