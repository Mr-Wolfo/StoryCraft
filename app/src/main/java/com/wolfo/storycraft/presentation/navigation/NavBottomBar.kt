package com.wolfo.storycraft.presentation.navigation

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
            .height(50.dp)
            .background(Color.Transparent),
        containerColor = MaterialTheme.colorScheme.primaryContainer,
    ) {

//        val animatedColor by animateColorAsState(
//            targetValue = MaterialTheme.extendedColors.mainYellow,
//            animationSpec = tween(
//                1000, easing = LinearEasing
//            )
//        )

        Routes.routeList.forEachIndexed { index, topLevelRoute ->
            val selected = currentRoute?.hierarchy?.any { it.hasRoute(topLevelRoute.route::class) } == true

            val iconSize by animateDpAsState(
                targetValue = if (selected) 28.dp else 20.dp,
                animationSpec = tween(durationMillis = 300)
            )

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
                            modifier = Modifier.size(iconSize)
                        )
                    }
                },
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = Color.Transparent,
                    selectedIconColor = MaterialTheme.colorScheme.primary,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
        }
    }
}