package com.wolfo.storycraft.presentation.ui.features.story_view.details.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.wolfo.storycraft.domain.model.Review
import com.wolfo.storycraft.domain.model.user.UserSimple
import com.wolfo.storycraft.presentation.common.GlassCard
import com.wolfo.storycraft.presentation.theme.StoryCraftTheme
import com.wolfo.storycraft.presentation.theme.extendedColors
import com.wolfo.storycraft.presentation.ui.components.StoryCraftCard
import com.wolfo.storycraft.presentation.ui.features.story_view.details.StoryReviewsUiState
import com.wolfo.storycraft.presentation.ui.utils.glass
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.rememberHazeState


@Composable
fun ReviewsSection(
    reviewsState: StoryReviewsUiState<*>,
    currentUserId: String?,
    modifier: Modifier = Modifier
) {
    GlassCard {
        Column(
            modifier = modifier
                .fillMaxWidth()
                .padding(vertical = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Отзывы",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.extendedColors.oppositeMain,
                modifier = Modifier.padding(horizontal = 24.dp)
            )

            when (reviewsState) {
                is StoryReviewsUiState.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                    }
                }

                is StoryReviewsUiState.Error -> {
                    Text(
                        "Не удалось загрузить отзывы",
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                }

                is StoryReviewsUiState.Success -> {
                    val reviews = reviewsState.data
                    if (reviews.isEmpty()) {
                        Text(
                            "Отзывов пока нет. Станьте первым!",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                                .padding(vertical = 16.dp)
                        )
                    } else {
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            reviews.forEach { review ->
                                ReviewItem(
                                    review = review,
                                    isOwnReview = review.user.id == currentUserId
                                )
                            }
                        }
                    }
                }

                is StoryReviewsUiState.Idle -> { /* Do nothing */ }
            }
        }
    }
}


@Preview
@Composable
private fun ReviewsSectionPreview() {
    val mockReviews = listOf(
        Review(id = "1",
            rating = 4,
            reviewText = "Разнообразный и богатый опыт социально-экономического развития требует от нас анализа...",
            storyId = "1",
            userId = "1",
            createdAt = "2023-10-25T10:00:00Z",
            updatedAt = null,
            user = UserSimple(id = "1", username = "Автор", avatarUrl = "")),
        Review(id = "1",
            rating = 4,
            reviewText = "Разнообразный и богатый опыт социально-экономического развития требует от нас анализа...",
            storyId = "1",
            userId = "1",
            createdAt = "2023-10-25T10:00:00Z",
            updatedAt = null,
            user = UserSimple(id = "1", username = "Автор", avatarUrl = ""))
    )
    val state = StoryReviewsUiState.Success(mockReviews)

    StoryCraftTheme {
        Box(modifier = Modifier.padding(16.dp)) {
            ReviewsSection(reviewsState = state, currentUserId = "1")
        }
    }
}