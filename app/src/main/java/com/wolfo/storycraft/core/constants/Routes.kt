package com.wolfo.storycraft.core.constants

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.PlayArrow
import com.wolfo.storycraft.presentation.navigation.Screen
import com.wolfo.storycraft.presentation.navigation.TopLevelRoute

object Routes
{
    val routeList = listOf(
        TopLevelRoute(
            name = "StoryList",
            route = Screen.StoryList,
            icon = Icons.Filled.Menu
        ),
        TopLevelRoute(
            name = "StoryView",
            route = Screen.StoryView,
            icon = Icons.Filled.PlayArrow
        ),
        TopLevelRoute(
            name = "StoryEditor",
            route = Screen.StoryEditor(null),
            icon = Icons.Filled.Add
        ),
        TopLevelRoute(
            name = "Profile",
            route = Screen.Profile,
            icon = Icons.Filled.Face
        )
    )
}