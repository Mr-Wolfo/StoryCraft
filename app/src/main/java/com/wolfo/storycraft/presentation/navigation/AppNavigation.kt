package com.wolfo.storycraft.presentation.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navigation
import androidx.navigation.toRoute
import com.wolfo.storycraft.presentation.features.profile.ProfileScreen
import com.wolfo.storycraft.presentation.features.profile.register.RegisterScreen
import com.wolfo.storycraft.presentation.features.story_editor.StoryEditorScreen
import com.wolfo.storycraft.presentation.features.story_list.StoryListScreen
import com.wolfo.storycraft.presentation.features.story_view.details.StoryDetailsScreen
import com.wolfo.storycraft.presentation.features.story_view.reader.StoryReaderScreen

@Composable
fun AppNavigation()
{
    val labelScreen = remember { mutableStateOf("") }
    val navController = rememberNavController()
    Scaffold(
        bottomBar = {
            AppNavBottomBar(navController = navController) },
        topBar = {
            AppTopBar(navController = navController, labelScreen.value)
        }
    ) { padding ->
        NavHost(navController = navController, startDestination = Screen.StoryList,
            modifier = Modifier.padding(padding)) {
            composable<Screen.StoryList>() {
                StoryListScreen() {storyId -> navController.navigate(Screen.StoryView.Details(storyId)) {
                    popUpTo(navController.graph.findStartDestination().id) {
                        saveState = true
                    }
                    launchSingleTop = true
                    restoreState = true
                }}
            }
            navigation<Screen.StoryView>(startDestination = Screen.StoryView.Details(null)) {
                composable<Screen.StoryView.Details> { navBackStackEntry ->
                    val storyDetails = navBackStackEntry.toRoute<Screen.StoryView.Details>()
                    StoryDetailsScreen(storyDetails.storyId) { navController.navigate(route = Screen.StoryView.Reader(it) ) }
                }
                composable<Screen.StoryView.Reader> { navBackStackEntry ->
                    val storyReader = navBackStackEntry.toRoute<Screen.StoryView.Reader>()
                    StoryReaderScreen(storyId = storyReader.storyId)
                }
            }
            composable<Screen.StoryEditor> {navBackStackEntry ->
                val storyEditor = navBackStackEntry.toRoute<Screen.StoryEditor>()
                StoryEditorScreen(storyId = storyEditor.storyId)
            }
            composable<Screen.Profile> {
                RegisterScreen()
            }
        }
    }
}

data class TopLevelRoute<T : Any>(val name: String, val route: T, val icon: ImageVector)