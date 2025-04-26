package com.wolfo.storycraft.presentation.navigation

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navigation
import androidx.navigation.toRoute
import com.wolfo.storycraft.presentation.common.AuthStateViewModel
import com.wolfo.storycraft.presentation.features.profile.ProfileScreen
import com.wolfo.storycraft.presentation.features.profile.auth.AuthScreen
import com.wolfo.storycraft.presentation.features.profile.login.LoginScreen
import com.wolfo.storycraft.presentation.features.profile.register.RegisterScreen
import com.wolfo.storycraft.presentation.features.story_editor.StoryEditorScreen
import com.wolfo.storycraft.presentation.features.story_list.StoryListScreen
import com.wolfo.storycraft.presentation.features.story_view.details.StoryDetailsScreen
import com.wolfo.storycraft.presentation.features.story_view.reader.StoryReaderScreen
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

        Log.d("LAUNCHED-Test", "${currentRoute}")
        Log.d("LAUNCHED-Test", "${Screen.Profile.Login::class.qualifiedName}")
        when {
            !isLoggedIn && currentRoute == Screen.Profile.Profile::class.qualifiedName -> {
                Log.d("LAUNCHED1", "${isLoggedIn}")
                navController.navigate(Screen.Profile.Auth) {
                    popUpTo(Screen.Profile.Profile) { inclusive = true }
                }
            }
            isLoggedIn && ((currentRoute == Screen.Profile.Login::class.qualifiedName) ||
                    (currentRoute == Screen.Profile.Register::class.qualifiedName))  -> {
                Log.d("LAUNCHED2", "${isLoggedIn}")
                navController.navigate(Screen.Profile.Profile) {
                    popUpTo(Screen.Profile.Auth) { inclusive = true }
                }
            }
        }
    }

    Scaffold(
        bottomBar = {
            AppNavBottomBar(navController = navController, modifier = Modifier.windowInsetsPadding(
                WindowInsets.navigationBars)) },
//        topBar = {
//            AppTopBar(navController = navController, labelScreen.value)
//        }
    ) {  padding ->
        NavHost(navController = navController, startDestination = Screen.StoryList,
            modifier = Modifier.padding(bottom = padding.calculateBottomPadding()-16.dp)) {
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

            navigation<Screen.Profile>(startDestination = Screen.Profile.Profile) {
                composable<Screen.Profile.Profile> {
                    Log.d("NAVIGATION", "${isLoggedIn}")
                    if (isLoggedIn) {
                        ProfileScreen(onLogout = {authStateViewModel.logout()}, onEditProfile = {})

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
                    Log.d("NAVIGATION", "Auth")
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
                StoryEditorScreen(storyId = storyEditor.storyId)
            }
        }
    }
}

data class TopLevelRoute<T : Any>(val name: String, val route: T, val icon: ImageVector)