package com.wolfo.storycraft.presentation.features.story_view.details

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wolfo.storycraft.domain.model.StoryBase
import com.wolfo.storycraft.domain.usecase.LoadStoryFullByIdUseCase
import com.wolfo.storycraft.domain.usecase.ObserveStoryBaseByIdUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class StoryDetailsUiState(
    val isLoading: Boolean = false,
    val story: StoryBase? = null,
    val error: String? = null
)

data class StoryDetailsLoadFullUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val success: Boolean = false
)

class StoryDetailsViewModel(
    private val observeStoryBaseByIdUseCase: ObserveStoryBaseByIdUseCase,
    private val loadStoryFullByIdUseCase: LoadStoryFullByIdUseCase,
    private val savedStateHandle: SavedStateHandle
): ViewModel() {
    private val _uiState = MutableStateFlow(StoryDetailsUiState())
    val uiState = _uiState.asStateFlow()

    private val _loadFullUiState = MutableStateFlow(StoryDetailsLoadFullUiState())
    val loadFullUiState = _loadFullUiState.asStateFlow()

    private var _storyId: Long? = savedStateHandle.get<Long>("storyId")
    val storyId: Long get() = _storyId ?: -1L

    init {
        Log.d(
            "StoryDetailsVM",
            "Current storyId: $_storyId, SavedStateHandle: ${savedStateHandle.get<Long>("storyId")}"
        )
//        Log.d("load", "Current storyId: $_storyId, SavedStateHandle: ${savedStateHandle.get<Long>("storyId")}")

        savedStateHandle["storyId"] = _storyId
        _storyId?.let { observeStoryBaseByIdCatalog(it) }
    }

    fun attemptLoadStory() {
        Log.d(
            "StoryDetailsVM",
            "Current storyId: $_storyId, SavedStateHandle: ${savedStateHandle.get<Long>("storyId")}"
        )
        savedStateHandle["storyId"] = _storyId
        _storyId?.let { observeStoryBaseByIdCatalog(storyId) }
    }

    private fun observeStoryBaseByIdCatalog(storyId: Long) {
        viewModelScope.launch {
            observeStoryBaseByIdUseCase(storyId = storyId)
                .onStart { _uiState.update { it.copy(isLoading = true, error = null) } }
                .catch { throwable ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = throwable.message ?: "Unknown error"
                        )
                    }
                }
                .collect { storyBase ->
                    _uiState.update { it.copy(isLoading = false, story = storyBase) }
                }
        }
    }

    fun loadStoryFullById() {
        if (storyId == -1L) {
            _loadFullUiState.update { it.copy(error = "Invalid story ID") }
            return
        }

        viewModelScope.launch {
            _loadFullUiState.update { it.copy(isLoading = true, error = null) } // ВНИМАНИЕ --> ПОМЕНЯТЬ!!!

            try {
                loadStoryFullByIdUseCase(storyId)
                Log.d("StoryFullSuccess", "SUCCESS")
                _loadFullUiState.update { it.copy(isLoading = false, success = true) }
            } catch (e: Throwable) {
                _loadFullUiState.update {
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "Failed to load story"
                    )
                }
            }
        }
    }
}