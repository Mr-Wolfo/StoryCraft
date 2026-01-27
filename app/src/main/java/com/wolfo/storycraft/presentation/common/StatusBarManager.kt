package com.wolfo.storycraft.presentation.common

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.wolfo.storycraft.domain.DataError
import com.wolfo.storycraft.presentation.ui.features.story_list.AppStatusBarUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatusBarManager(
    modifier: Modifier = Modifier,
    appStatusBarUiState: AppStatusBarUiState,
    onRetry: () -> Unit
) {
    var showDetailedError by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    LaunchedEffect(appStatusBarUiState) {
        if(appStatusBarUiState !is AppStatusBarUiState.Error) {
            showDetailedError = false
        }
    }

    Box(modifier = modifier
        .windowInsetsPadding(WindowInsets.navigationBars)
        .fillMaxSize(),
        contentAlignment = Alignment.BottomCenter)
    {
        when (val barState = appStatusBarUiState) {
            is AppStatusBarUiState.Loading -> LoadingBar(isVisible = true)

            is AppStatusBarUiState.Success -> {
                SuccessBottomMessage(message = barState.message, isVisible = true) { /* dismiss */ }
            }

            is AppStatusBarUiState.Error -> {
                println("Статус бар: ошибка")
                StatusBottomMessage(
                    message = barState.error.message ?: "Unknown error",
                    isVisible = !showDetailedError,
                    onExpand = {
                        println("Expand!")
                        showDetailedError = true }
                )
            }

            else -> Unit
        }

        if (showDetailedError && appStatusBarUiState is AppStatusBarUiState.Error) {
            ModalBottomSheet(
                onDismissRequest = { showDetailedError = false },
                sheetState = sheetState,
                dragHandle = { BottomSheetDefaults.DragHandle() },
                containerColor = MaterialTheme.colorScheme.surface
            ) {
                ErrorState(
                    error = appStatusBarUiState.error,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 24.dp),
                    onRetry = {
                        showDetailedError = false
                        onRetry()
                    }
                )
            }
        }
    }
}

@Preview()
@Composable
fun StatusBarManagerPreview() {
    StatusBarManager(appStatusBarUiState = AppStatusBarUiState.Error(DataError.Network(errorMessage = "Нет подключения к сети, повторите позже", code = 404))) {}
}