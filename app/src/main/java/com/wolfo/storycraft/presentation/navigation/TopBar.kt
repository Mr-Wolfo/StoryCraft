package com.wolfo.storycraft.presentation.navigation

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopBar(navController: NavHostController,
              labelScreen: String)
{
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val currentTopLevelRoute = null // ScreenBase.findTitleResIdByRoute(currentRoute)

    TopAppBar(
        title = { Text(currentTopLevelRoute?.let { stringResource(id = it) } ?: labelScreen )}// stringResource(R.string.app_name)) },
    )
}