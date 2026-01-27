// Copyright 2023, Christopher Banes and the Haze project contributors
// SPDX-License-Identifier: Apache-2.0

package com.wolfo.storycraft.presentation.ui.features.story_view.details.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animate
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.wolfo.storycraft.R
import com.wolfo.storycraft.presentation.theme.StoryCraftTheme
import dev.chrisbanes.haze.HazeDefaults
import dev.chrisbanes.haze.HazeStyle
import dev.chrisbanes.haze.HazeTint
import dev.chrisbanes.haze.hazeEffect
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.rememberHazeState

@Composable
fun HazeTestScreen() {
    val hazeState = rememberHazeState()

    Box(modifier = Modifier.fillMaxSize()) {
        // 1. Источник
        Image(painterResource(R.drawable.abstraction_profile), modifier = Modifier.fillMaxSize().hazeSource(hazeState), contentScale = ContentScale.Crop, contentDescription = "")

        // 2. Элемент с эффектом
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .size(200.dp)
                .border(1.dp, Color.White, CircleShape)
                .hazeEffect(
                    state = hazeState,
                    style = HazeStyle(
                        tint = HazeTint(Color.White.copy(alpha = 0.4f)),
                        blurRadius = 20.dp
                    )
                )
                .clip(CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text("Это тест", color = Color.White)
        }
    }
}

@Preview
@Composable
fun TestHaze(){
    HazeTestScreen()
}