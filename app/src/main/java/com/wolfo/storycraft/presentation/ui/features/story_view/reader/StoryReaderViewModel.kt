package com.wolfo.storycraft.presentation.ui.features.story_view.reader

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wolfo.storycraft.domain.DataError
import com.wolfo.storycraft.domain.ResultM
import com.wolfo.storycraft.domain.model.story.StoryFull
import com.wolfo.storycraft.domain.usecase.story.GetStoryDetailsStreamUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class StoryReaderUiState {
    object Idle : StoryReaderUiState()
    object Loading : StoryReaderUiState()
    data class Success(val data: StoryFull) : StoryReaderUiState()
    data class Error(val error: DataError) : StoryReaderUiState()
}

class StoryReaderViewModel(
    private val getStoryDetailsStreamUseCase: GetStoryDetailsStreamUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val storyId: String = savedStateHandle.get<String>("storyId")
        ?: throw IllegalArgumentException("storyId is required")

    private val _uiState = MutableStateFlow<StoryReaderUiState>(StoryReaderUiState.Idle)
    val uiState = _uiState.asStateFlow()

    private val _currentPageIndex = MutableStateFlow(0)
    val currentPageIndex = _currentPageIndex.asStateFlow()

    private val navigationHistory = mutableListOf<Int>()

    private val _canGoBack = MutableStateFlow(false)
    val canGoBack = _canGoBack.asStateFlow()

    init {
        loadStory()
    }

    private fun loadStory() {
        viewModelScope.launch {
            _uiState.value = StoryReaderUiState.Loading
            getStoryDetailsStreamUseCase(storyId = storyId)
                .collect { result ->
                    when (result) {
                        is ResultM.Success -> {
                            _uiState.value = StoryReaderUiState.Success(result.data)
                        }
                        is ResultM.Failure -> {
                            _uiState.value = StoryReaderUiState.Error(result.error)
                        }
                        ResultM.Loading -> {
                            _uiState.value = StoryReaderUiState.Loading
                        }
                    }
                }
        }
    }

    /**
     * Переход на новую страницу по ID
     */
    fun navigateToPage(targetPageId: String) {
        val currentStory = (uiState.value as? StoryReaderUiState.Success)?.data ?: return

        val targetIndex = currentStory.pages.indexOfFirst { it.id == targetPageId }

        if (targetIndex != -1) {
            navigationHistory.add(_currentPageIndex.value)
            _currentPageIndex.value = targetIndex
            updateBackState()
        }
    }

    /**
     * Возврат на предыдущую страницу
     */
    fun goBack() {
        if (navigationHistory.isNotEmpty()) {
            val previousIndex = navigationHistory.removeAt(navigationHistory.lastIndex)
            _currentPageIndex.value = previousIndex
            updateBackState()
        }
    }

    private fun updateBackState() {
        _canGoBack.value = navigationHistory.isNotEmpty()
    }

    fun attemptLoadStory() {
        loadStory()
    }
}