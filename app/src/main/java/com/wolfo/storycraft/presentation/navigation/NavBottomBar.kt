package com.wolfo.storycraft.presentation.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.wolfo.storycraft.core.constants.Routes

@Composable
fun AppNavBottomBar(navController: NavHostController)
{
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination

    NavigationBar(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.07f)
            .background(color = MaterialTheme.colorScheme.primary)
    ) {
        Routes.routeList.forEach{ topLevelRoute  ->
            NavigationBarItem(
                selected = currentRoute?.hierarchy?.any { it.hasRoute(topLevelRoute.route::class) } == true,
                onClick = {
                    navController.navigate(topLevelRoute.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                icon = {
                    Icon(imageVector = topLevelRoute.icon, contentDescription = topLevelRoute.name)
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color.Black,
                    unselectedIconColor = Color.Black,
                    selectedTextColor = Color.Black,
                    indicatorColor = Color(0xFFFF9800)
                )
            )
        }
    }
}