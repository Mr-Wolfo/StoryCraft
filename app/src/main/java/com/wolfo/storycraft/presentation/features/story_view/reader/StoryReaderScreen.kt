package com.wolfo.storycraft.presentation.features.story_view.reader

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.SuggestionChipDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.wolfo.storycraft.domain.model.Choice
import com.wolfo.storycraft.domain.model.Page
import com.wolfo.storycraft.domain.model.Story
import com.wolfo.storycraft.presentation.features.story_list.Error
import com.wolfo.storycraft.presentation.features.story_list.Loading
import org.koin.androidx.compose.koinViewModel

@Composable
fun StoryReaderScreen(
    storyId: Long,
    viewModel: StoryReaderViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    uiState.story?.let {

        val currentPageId = remember { mutableIntStateOf(0) }

        Column(modifier = Modifier.fillMaxSize(),
            Arrangement.SpaceEvenly) {
            Text(text = uiState.story?.pages?.get(currentPageId.intValue)?.pageText ?: "Empty")
            uiState.story!!.pages[currentPageId.intValue].choices.forEach { choice ->
                Button(onClick = {
                    currentPageId.intValue = choice.targetPageId.toInt()-1
                }) { Text(choice.choiceText) }
            }
        }
    }
}