package com.wolfo.storycraft.presentation.features.story_list

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wolfo.storycraft.domain.model.StoryBase
import com.wolfo.storycraft.domain.usecase.ObserveStoriesUseCase
import com.wolfo.storycraft.domain.usecase.RefreshStoriesUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class StoryListUiState(
    val isLoading: Boolean = false,
    val stories: List<StoryBase> = emptyList(),
    val error: String? = null
)

class StoryListViewModel(
    private val observeStoriesUseCase: ObserveStoriesUseCase,
    private val refreshStoriesUseCase: RefreshStoriesUseCase
): ViewModel() {
    private val _uiState = MutableStateFlow(StoryListUiState(isLoading = true))
    val uiState: StateFlow<StoryListUiState> = _uiState.asStateFlow()

    init {
        loadStoriesCatalog()
    }

    fun refreshStoriesCatalog() {
        viewModelScope.launch {
            try {
                refreshStoriesUseCase()
            } catch (e: Exception) {
                Log.e("StoryList", "RefreshError: $e")
            }
        }
    }

    private fun loadStoriesCatalog() {
        viewModelScope.launch {
            observeStoriesUseCase()
                .onStart { _uiState.update { it.copy(isLoading = true, error = null) } }
                .catch { throwable ->
                    Log.d("ObserveStoryListVM", "Error $throwable")
                    _uiState.update { it.copy(isLoading = false, error = throwable.message ?: "Unknown error") }
                }
                .collect { stories ->
                    _uiState.update { it.copy(isLoading = false, stories = stories) }
                }
        }
    }
}