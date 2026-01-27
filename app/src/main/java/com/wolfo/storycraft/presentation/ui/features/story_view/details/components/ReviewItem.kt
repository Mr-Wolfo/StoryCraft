package com.wolfo.storycraft.presentation.ui.features.story_view.details.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.wolfo.storycraft.domain.model.Review
import com.wolfo.storycraft.domain.model.user.UserSimple
import com.wolfo.storycraft.presentation.common.GlassCard
import com.wolfo.storycraft.presentation.theme.NeonGreen
import com.wolfo.storycraft.presentation.theme.StoryCraftTheme
import com.wolfo.storycraft.presentation.theme.extendedColors
import com.wolfo.storycraft.presentation.ui.components.StoryCraftCard
import com.wolfo.storycraft.presentation.ui.utils.UiUtils
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun ReviewItem(
    review: Review,
    isOwnReview: Boolean,
    modifier: Modifier = Modifier
) {
    val formattedDate = remember(review.createdAt) {
        val dateTime = UiUtils.toLocaleDateTime(review.createdAt)
        DateTimeFormatter.ofPattern("dd MMMM yyyy", Locale.getDefault()).format(dateTime)
    }

    GlassCard(
        modifier = modifier.padding(horizontal = 5.dp),
        containerColor = MaterialTheme.colorScheme.onPrimaryContainer
    ) {
        Row(
            modifier = modifier.fillMaxWidth().padding(horizontal = 18.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            AsyncImage(
                model = review.user.avatarUrl,
                contentDescription = "Аватар",
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
                contentScale = ContentScale.Crop
            )

            Column(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = review.user.username,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        if (isOwnReview) {
                            Text(
                                text = "(Вы)",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.tertiary
                            )
                        }
                    }
                    Text(
                        text = formattedDate,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                RatingBar(rating = review.rating)

                if (!review.reviewText.isNullOrBlank()) {
                    Text(
                        text = review.reviewText!!,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = MaterialTheme.typography.bodyLarge.lineHeight
                    )
                }
            }
        }
    }


}

@Composable
private fun RatingBar(rating: Int) {
    Row {
        repeat(5) { index ->
            Icon(
                imageVector = Icons.Default.Star,
                contentDescription = null,
                modifier = Modifier.size(18.dp),
                tint = if (index < rating) MaterialTheme.extendedColors.star else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF1C1936)
@Composable
private fun ReviewItemPreview() {
    val mockReview = Review(
        id = "1",
        rating = 4,
        reviewText = "Очень продуманный мир и захватывающий сюжет. Не мог оторваться до самого конца. Персонажи живые, им сопереживаешь.",
        storyId = "1",
        userId = "1",
        createdAt = "2023-10-25T10:00:00Z",
        updatedAt = null,
        user = UserSimple(id = "1", username = "AlexWolf", avatarUrl = "")
    )
    StoryCraftTheme {
        ReviewItem(review = mockReview, isOwnReview = true, modifier = Modifier.padding(16.dp))
    }
}