package com.wolfo.storycraft.presentation.features.storylist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wolfo.storycraft.domain.model.StoryBase
import com.wolfo.storycraft.domain.usecase.GetStoriesUseCase
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
    private val getStoriesUseCase: GetStoriesUseCase
): ViewModel() {
    private val _uiState = MutableStateFlow(StoryListUiState(isLoading = true))
    val uiState: StateFlow<StoryListUiState> = _uiState.asStateFlow()

    init {
        loadStoriesCatalog()
    }

    fun updateStoriesCatalog() {
        loadStoriesCatalog()
    }

    private fun loadStoriesCatalog() {
        viewModelScope.launch {
            getStoriesUseCase()
                .onStart { _uiState.update { it.copy(isLoading = true, error = null) } }
                .catch { throwable ->
                    _uiState.update { it.copy(isLoading = false, error = throwable.message ?: "Unknown error") }
                }
                .collect { stories ->
                    _uiState.update { it.copy(isLoading = false, stories = stories) }
                }
        }
    }
}