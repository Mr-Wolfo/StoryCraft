package com.wolfo.storycraft.presentation.ui.features.story_list.components.list

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.wolfo.storycraft.presentation.common.formatNumber
import com.wolfo.storycraft.presentation.theme.extendedColors
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun StoryMetaInfo(
    rating: Float,
    views: Int,
    date: LocalDateTime,
    modifier: Modifier = Modifier
) {

    val formattedDate = remember(date) {
        DateTimeFormatter
            .ofPattern("dd.MM.yy", Locale.getDefault())
            .format(date)
    }
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            Column {
                // Дата
                Text(
                    text = formattedDate,
                    style = MaterialTheme.typography.labelMedium
                )
                Spacer(modifier = Modifier.height(4.dp))

                // Просмотры
                Text(
                    text = "${formatNumber(views)} просмотров",
                    style = MaterialTheme.typography.labelMedium
                )
            }

            Row{
                // Рейтинг
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = null,
                    tint = MaterialTheme.extendedColors.star,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "%.1f".format(rating),
                    style = MaterialTheme.typography.labelMedium
                )
            }
        }
    }
}