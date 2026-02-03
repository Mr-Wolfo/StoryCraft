package com.wolfo.storycraft.presentation.ui.features.story_view.reader.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wolfo.storycraft.domain.model.story.Page
import com.wolfo.storycraft.domain.model.story.StoryFull
import com.wolfo.storycraft.presentation.theme.extendedColors
import com.wolfo.storycraft.presentation.theme.spacing
import com.wolfo.storycraft.presentation.ui.components.AppCard
import com.wolfo.storycraft.presentation.ui.features.story_view.details.components.StoryImage

@Composable
fun LastPageContent(
    story: StoryFull,
    page: Page,
    onExploreStories: () -> Unit,
    onCreateStory: () -> Unit,
    onReturnToStory: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.extraLarge),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        StoryImage(imageUrl = page.imageUrl)

        AppCard {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(MaterialTheme.spacing.extraLarge),
                verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.large),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Иконка завершения
                Icon(
                    imageVector = Icons.Outlined.Star,
                    contentDescription = null,
                    modifier = Modifier.size(64.dp),
                    tint = MaterialTheme.extendedColors.star
                )

                // Заголовок
                Text(
                    text = "История завершена!",
                    style = MaterialTheme.typography.headlineMedium,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )

                // Текст страницы
                Text(
                    text = page.pageText,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        lineHeight = 28.sp,
                        textAlign = TextAlign.Center
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                // Рейтинг истории
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small)
                ) {
                    RatingBar(
                        rating = story.averageRating,
                        modifier = Modifier.height(24.dp)
                    )
                    Text(
                        text = "%.1f".format(story.averageRating),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        AppCard {
            // Кнопки действий
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(MaterialTheme.spacing.extraSmall),
                verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.large)
            ) {
                AppCard(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                ) {
                    TextButton(
                        onClick = onExploreStories,
                        modifier = Modifier.fillMaxWidth(),
                        shape = MaterialTheme.shapes.medium,
                    ) {
                        Text(
                            text = "Исследовать другие истории",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                AppCard(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(MaterialTheme.spacing.extraSmall)
                    ) {
                        TextButton(
                            onClick = onCreateStory,
                            modifier = Modifier.fillMaxWidth(),
                            shape = MaterialTheme.shapes.medium,
                        ) {
                            Text(
                                text = "Создать свою историю",
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        TextButton(
                            onClick = onReturnToStory,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "Вернуться к истории",
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}