package com.wolfo.storycraft.presentation.features.story_list

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wolfo.storycraft.domain.DataError
import com.wolfo.storycraft.domain.ResultM
import com.wolfo.storycraft.domain.model.StoryBaseInfo
import com.wolfo.storycraft.domain.usecase.story.GetStoriesStreamUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class AppStatusBarUiState<out T> {
    object Idle : AppStatusBarUiState<Nothing>()
    object Loading : AppStatusBarUiState<Nothing>()
    data class Error<out T>(val error: DataError) : AppStatusBarUiState<T>()
}

sealed class StoryListUiState<out T> {
    object Idle : StoryListUiState<Nothing>()
    object Loading : StoryListUiState<Nothing>()
    data class Success(val data: List<StoryBaseInfo>) : StoryListUiState<List<StoryBaseInfo>>()
    data class Error<out T>(val error: DataError) : StoryListUiState<T>()
}

class StoryListViewModel(
    private val getStoriesStreamUseCase: GetStoriesStreamUseCase
): ViewModel() {
    private val _uiState = MutableStateFlow<StoryListUiState<List<StoryBaseInfo>>>(StoryListUiState.Idle)
    val uiState = _uiState.asStateFlow()

    private val _appStatusBarUiState = MutableStateFlow<AppStatusBarUiState<DataError>>(AppStatusBarUiState.Idle)
    val appStatusBarUiState = _appStatusBarUiState.asStateFlow()

    private var currentJob: Job? = null

    init {
        storiesStream()
    }

    fun refreshStoriesCatalog() {
        storiesStream(true)
    }

    private fun storiesStream(forceRefresh: Boolean = false) {
        Log.d("SL_VM", "THERE")
        _uiState.value = StoryListUiState.Loading
        _appStatusBarUiState.value = AppStatusBarUiState.Loading

        currentJob?.cancel()

        currentJob = viewModelScope.launch {
            getStoriesStreamUseCase(forceRefresh)
                .collect { stories ->
                    when (stories) {
                        is ResultM.Success -> {
                            _uiState.value = StoryListUiState.Success(stories.data)
                            _appStatusBarUiState.value = AppStatusBarUiState.Idle
                        }
                        is ResultM.Failure -> {
                            if (stories.cachedData != null) {
                                _uiState.value = StoryListUiState.Success(
                                    data = stories.cachedData as List<StoryBaseInfo>
                                )
                            }
                            _appStatusBarUiState.value = AppStatusBarUiState.Error(error = stories.error)

                        }
                        ResultM.Loading -> {
                            _appStatusBarUiState.value = AppStatusBarUiState.Loading
                        }
                    }
                }
        }
    }
}