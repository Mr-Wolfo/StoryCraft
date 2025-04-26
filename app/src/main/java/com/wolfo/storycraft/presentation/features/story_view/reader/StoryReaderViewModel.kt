package com.wolfo.storycraft.presentation.features.story_view.reader

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wolfo.storycraft.domain.model.Story
import com.wolfo.storycraft.domain.usecase.ObserveStoryFullByIdUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class StoryReaderUiState(
    val isLoading: Boolean = false,
    val story: Story? = null,
    val error: String? = null
)

class StoryReaderViewModel(
    private val observeStoryFullByIdUseCase: ObserveStoryFullByIdUseCase,
    private val savedStateHandle: SavedStateHandle
): ViewModel() {
    private val _uiState = MutableStateFlow(StoryReaderUiState())
    val uiState = _uiState.asStateFlow()
    private var _storyId: Long? = savedStateHandle.get<Long>("storyId")
    val storyId: Long get() = _storyId ?: -1L

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
        viewModelScope.launch {
            observeStoryFullByIdUseCase(storyId = storyId)
                .onStart { _uiState.update { it.copy(isLoading = true, error = null) } }
                .catch { throwable ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = throwable.message ?: "Unknown error"
                        )
                    }
                }
                .collect { story ->
                    Log.d("FullStory", "${story.pages.size}")
                    _uiState.update { it.copy(isLoading = false, story = story) }
                }
        }
    }

    /*private fun loadStoryFullById() {
        viewModelScope.launch {
            try {
                _uiState.update {
                    it.copy(
                        isLoading = true,
                        story = null,
                        error = null
                    )
                }

                Log.d("GetVM", "Loading")

                val story = loadStoryFullByIdUseCase(_storyId!!)

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        story = story,
                        error = if (story == null) "Story not found" else null
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "Failed to load story"
                    )
                }
            }
        }
    }*/
}