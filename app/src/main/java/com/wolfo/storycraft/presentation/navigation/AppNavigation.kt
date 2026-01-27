package com.wolfo.storycraft.presentation.navigation

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navigation
import androidx.navigation.toRoute
import com.wolfo.storycraft.presentation.common.AuthStateViewModel
import com.wolfo.storycraft.presentation.ui.features.profile.ProfileScreen
import com.wolfo.storycraft.presentation.ui.features.profile.auth.AuthScreen
import com.wolfo.storycraft.presentation.ui.features.profile.login.LoginScreen
import com.wolfo.storycraft.presentation.ui.features.profile.register.RegisterScreen
import com.wolfo.storycraft.presentation.ui.features.story_editor.StoryEditorScreen
import com.wolfo.storycraft.presentation.ui.features.story_list.StoryListScreen
import com.wolfo.storycraft.presentation.ui.features.story_view.details.StoryDetailsScreen
import com.wolfo.storycraft.presentation.ui.features.story_view.reader.StoryReaderScreen
import org.koin.androidx.compose.koinViewModel

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun AppNavigation(authStateViewModel: AuthStateViewModel = koinViewModel())
{
    val isLoggedIn by authStateViewModel.isLoggedIn.collectAsState()
    val labelScreen = remember { mutableStateOf("") }
    val navController = rememberNavController()

    LaunchedEffect(key1 = isLoggedIn) {
        val currentRoute = navController.currentBackStackEntry?.destination?.route

        when {
            !isLoggedIn && currentRoute == Screen.Profile.Profile::class.qualifiedName -> {
                navController.navigate(Screen.Profile.Auth) {
                    popUpTo(Screen.Profile.Profile) { inclusive = true }
                }
            }
            isLoggedIn && ((currentRoute == Screen.Profile.Login::class.qualifiedName) ||
                    (currentRoute == Screen.Profile.Register::class.qualifiedName))  -> {
                navController.navigate(Screen.Profile.Profile) {
                    popUpTo(Screen.Profile.Auth) { inclusive = true }
                }
            }
        }
    }

    Scaffold(
        bottomBar = {
            AppNavBottomBar(navController = navController,
                modifier = Modifier.windowInsetsPadding(WindowInsets.navigationBars)) },
//        topBar = {
//            AppTopBar(navController = navController, labelScreen.value)
//        }
    ) { padding ->
        NavHost(navController = navController, startDestination = Screen.StoryList) {
            composable<Screen.StoryList>() {
                StoryListScreen(
                    navPadding = padding
                ) {storyId -> navController.navigate(Screen.StoryView.Details(storyId)) {
                    defaultNavOptions(navController)
                }}
            }
            navigation<Screen.StoryView>(startDestination = Screen.StoryView.Details(null)) {
                composable<Screen.StoryView.Details> { navBackStackEntry ->
                    val storyDetails = navBackStackEntry.toRoute<Screen.StoryView.Details>()
                    StoryDetailsScreen(
                        storyId = storyDetails.storyId,
                        navPadding = padding,
                        onReadStory = { navController.navigate(route = Screen.StoryView.Reader(it) )
                        {
                            defaultNavOptions(navController)
                        }
                                      },
                        onNavigateToCreateStory = { navController.navigate(route = Screen.StoryEditor(null) )
                        {
                            defaultNavOptions(navController)
                        }},
                        onNavigateToStoryList = { navController.navigate(route = Screen.StoryList )
                        {
                            defaultNavOptions(navController)
                        }}
                    )
                }
                composable<Screen.StoryView.Reader> { navBackStackEntry ->
                    val storyReader = navBackStackEntry.toRoute<Screen.StoryView.Reader>()
                    StoryReaderScreen(
                        storyId = storyReader.storyId,
                        navPadding = padding,
                        onExploreStories = { navController.navigate(route = Screen.StoryList )
                        {
                            defaultNavOptions(navController)
                        } },
                        onCreateStory = {
                            navController.navigate(route = Screen.StoryEditor(null) )
                            {
                                defaultNavOptions(navController)
                            }
                        },
                        onReturnToStory = {
                            navController.navigate(route = Screen.StoryView.Details(it) )
                            {
                                defaultNavOptions(navController)
                            }
                        }
                    )
                }
            }

            navigation<Screen.Profile>(startDestination = Screen.Profile.Profile) {
                composable<Screen.Profile.Profile> {
                    if (isLoggedIn) {
                        ProfileScreen(onLogout = {authStateViewModel.logout()}, navPadding = padding, onEditProfile = {})

                    } else {
                        LaunchedEffect(isLoggedIn) {
                            navController.navigate(Screen.Profile.Auth) {
                                popUpTo(Screen.Profile.Profile) { inclusive = true }
                            }
                        }
                        Box {}
                    }
                }
                composable<Screen.Profile.Auth> { navBackStackEntry ->
                    AuthScreen({navController.navigate(route = Screen.Profile.Login) },
                        {navController.navigate(route = Screen.Profile.Register)})
                }
                composable<Screen.Profile.Register> { navBackStackEntry ->
                    RegisterScreen(onNavigateToLogin = {navController.navigate(route = Screen.Profile.Login)},
                        onBack = {navController.navigate(route = Screen.Profile.Auth)})
                }
                composable<Screen.Profile.Login> { navBackStackEntry ->
                    LoginScreen(onNavigateToRegister = {navController.navigate(route = Screen.Profile.Register)},
                        onBack = {navController.navigate(route = Screen.Profile.Auth)})
                }
            }


            composable<Screen.StoryEditor> {navBackStackEntry ->
                val storyEditor = navBackStackEntry.toRoute<Screen.StoryEditor>()
                StoryEditorScreen(
                    storyId = storyEditor.storyId,
                    onNavigateToProfile = {navController.navigate(route = Screen.Profile )
                    {
                        defaultNavOptions(navController)
                    }},
                    navPadding = padding,
                    onStoryPublished = {navController.navigate(route = Screen.StoryView.Details(it) )
                    {
                        defaultNavOptions(navController)
                    }})
            }
        }
    }
}

fun NavOptionsBuilder.defaultNavOptions(navController: NavController) {
    popUpTo(navController.graph.findStartDestination().id) {
        saveState = true
    }
    launchSingleTop = true
    restoreState = true
}

data class TopLevelRoute<T : Any>(val name: String, val route: T, val icon: ImageVector)