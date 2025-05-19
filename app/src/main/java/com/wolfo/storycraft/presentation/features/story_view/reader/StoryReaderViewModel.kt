package com.wolfo.storycraft.presentation.features.story_view.reader

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wolfo.storycraft.domain.DataError
import com.wolfo.storycraft.domain.ResultM
import com.wolfo.storycraft.domain.model.StoryFull
import com.wolfo.storycraft.domain.usecase.story.GetStoryDetailsStreamUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class StoryReaderUiState<out T> {
    object Idle : StoryReaderUiState<Nothing>()
    object Loading : StoryReaderUiState<Nothing>()
    data class Success(val data: StoryFull) : StoryReaderUiState<StoryFull>()
    data class Error<out T>(val error: DataError) : StoryReaderUiState<T>()
}

class StoryReaderViewModel(
    private val getStoryDetailsStreamUseCase: GetStoryDetailsStreamUseCase,
    private val savedStateHandle: SavedStateHandle
): ViewModel() {
    private val _uiState = MutableStateFlow<StoryReaderUiState<StoryFull>>(StoryReaderUiState.Idle)
    val uiState = _uiState.asStateFlow()
    private var _storyId: String? = savedStateHandle.get<String>("storyId")
    val storyId: String get() = _storyId ?: ""

    init {
        observeStoryFullById()
//        savedStateHandle["storyId"] = _storyId
//        _storyId?.let { loadStoryFullById() }
    }

    fun attemptLoadStory() {
        savedStateHandle["storyId"] = _storyId
//        _storyId?.let { loadStoryFullById() }
    }

    private fun observeStoryFullById() {
        _uiState.value = StoryReaderUiState.Loading
        viewModelScope.launch {
            getStoryDetailsStreamUseCase(storyId = storyId)
                .collect { storyFull ->
                    when (storyFull) {
                        is ResultM.Success -> {
                            _uiState.value = StoryReaderUiState.Success(storyFull.data)
                        }
                        is ResultM.Failure -> {
                            _uiState.value = StoryReaderUiState.Error(storyFull.error)
                        }
                        ResultM.Loading -> {
                            // Уже обработали выше
                        }
                    }

                }
        }
    }
}