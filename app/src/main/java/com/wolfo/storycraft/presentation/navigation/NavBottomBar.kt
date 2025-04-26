package com.wolfo.storycraft.presentation.navigation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.wolfo.storycraft.core.constants.Routes

@Composable
fun AppNavBottomBar(navController: NavHostController,
                    modifier: Modifier) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination

    NavigationBar(
        modifier = modifier
            .fillMaxWidth()
            .height(60.dp)
            .shadow(8.dp, shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)).background(Color.Transparent),
        containerColor = MaterialTheme.colorScheme.surfaceContainer,
    ) {
        Routes.routeList.forEach { topLevelRoute ->
            val selected = currentRoute?.hierarchy?.any { it.hasRoute(topLevelRoute.route::class) } == true
            NavigationBarItem(
                selected = selected,
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
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = topLevelRoute.icon,
                            contentDescription = topLevelRoute.name,
                            modifier = Modifier.size(24.dp),
                            tint = if (selected) {
                                MaterialTheme.colorScheme.tertiary
                            } else {
                                MaterialTheme.colorScheme.onSurfaceVariant
                            }
                        )

                        AnimatedVisibility(visible = selected) {
                            Text(
                                text = topLevelRoute.name,
                                color = MaterialTheme.colorScheme.tertiary,
                                style = MaterialTheme.typography.labelSmall,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                    }
                },
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = Color.Transparent,
                    selectedIconColor = Color.Unspecified,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
        }
    }
}