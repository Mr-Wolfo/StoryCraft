package com.wolfo.storycraft.presentation.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wolfo.storycraft.presentation.theme.StoryCraftTheme

@Composable
fun AppChip(tag: String) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(4.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(horizontal = 6.dp, vertical = 2.dp)
    ) {
        Text(
            text = tag,
            style = MaterialTheme.typography.labelSmall.copy(
                fontSize = 10.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            ),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}


@Preview
@Composable
private fun StoryCraftChipPreview() {
    StoryCraftTheme {
        AppChip(tag = "Фэнтези")
    }
}

@Preview
@Composable
private fun StoryCraftChipSelectedPreview() {
    StoryCraftTheme {
        AppChip(tag = "Хоррор")
    }
}