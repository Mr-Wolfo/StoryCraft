package com.wolfo.storycraft.presentation.features.story_view.details

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wolfo.storycraft.domain.DataError
import com.wolfo.storycraft.domain.ResultM
import com.wolfo.storycraft.domain.model.Review
import com.wolfo.storycraft.domain.model.StoryBaseInfo
import com.wolfo.storycraft.domain.usecase.story.GetReviewsStreamUseCase
import com.wolfo.storycraft.domain.usecase.story.GetStoryBaseUseCase
import com.wolfo.storycraft.domain.usecase.story.GetStoryDetailsStreamUseCase
import com.wolfo.storycraft.presentation.features.story_list.AppStatusBarUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class StoryDetailsUiState<out T> {
    object Idle : StoryDetailsUiState<Nothing>()
    object Loading : StoryDetailsUiState<Nothing>()
    data class Success(val data: StoryBaseInfo) : StoryDetailsUiState<StoryBaseInfo>()
    data class Error<out T>(val error: DataError) : StoryDetailsUiState<T>()
}

sealed class StoryReviewsUiState<out T> {
    object Idle : StoryReviewsUiState<Nothing>()
    object Loading : StoryReviewsUiState<Nothing>()
    data class Success(val data: List<Review>) : StoryReviewsUiState<List<Review>>()
    data class Error<out T>(val error: DataError) : StoryReviewsUiState<T>()
}

sealed class FullStoryLoadState<out T> {
    object Idle : FullStoryLoadState<Nothing>()
    object Loading : FullStoryLoadState<Nothing>()
    object Success : FullStoryLoadState<Nothing>()
    data class Error<out T>(val error: DataError) : FullStoryLoadState<T>()
}

class StoryDetailsViewModel(
    private val getStoryBaseUseCase: GetStoryBaseUseCase,
    private val getReviewsStreamUseCase: GetReviewsStreamUseCase,
    private val getStoryDetailsStreamUseCase: GetStoryDetailsStreamUseCase,
    private val savedStateHandle: SavedStateHandle
): ViewModel() {
    private val _uiState = MutableStateFlow<StoryDetailsUiState<StoryBaseInfo>>(StoryDetailsUiState.Idle)
    val uiState = _uiState.asStateFlow()

    private val _loadFullState = MutableStateFlow<FullStoryLoadState<Nothing>>(FullStoryLoadState.Idle)
    val loadFullState = _loadFullState.asStateFlow()

    private val _loadReviewsState = MutableStateFlow<StoryReviewsUiState<List<Review>>>(StoryReviewsUiState.Idle)
    val loadReviewsState = _loadReviewsState.asStateFlow()

    private val _appStatusBarUiState = MutableStateFlow<AppStatusBarUiState<DataError>>(AppStatusBarUiState.Idle)
    val appStatusBarUiState = _appStatusBarUiState.asStateFlow()

    private var _storyId: String? = savedStateHandle.get<String>("storyId")
    val storyId: String get() = _storyId ?: ""

    init {
        attemptLoadBaseStory(storyId)
        attemptLoadStoryReviews(storyId)
    }

    fun attemptLoadBaseStory(storyId: String?) {
        storyId?.let { storyBaseStream(storyId) }
    }

    fun attemptLoadStoryReviews(storyId: String?) {
        storyId?.let { storyReviewsStreamByStoryId(storyId) }
    }

    private fun storyBaseStream(storyId: String) {
        viewModelScope.launch {
            Log.d("SR_VM", storyId)
            when (val result = getStoryBaseUseCase(storyId = storyId)) {
                is ResultM.Success -> {
                    Log.d("SR_VM", "SUCCESS")
                    _uiState.value = StoryDetailsUiState.Success(result.data)
                }

                is ResultM.Failure -> {
                    _uiState.value = StoryDetailsUiState.Error(error = result.error)
                }

                is ResultM.Loading -> { }
            }
        }
    }

    fun loadStoryFullById() {
       _loadFullState.value = FullStoryLoadState.Loading
        _appStatusBarUiState.value = AppStatusBarUiState.Loading

        viewModelScope.launch {
            getStoryDetailsStreamUseCase(storyId)
                .collect { storyFull ->
                    when (storyFull) {
                        is ResultM.Success -> {
                            _loadFullState.value = FullStoryLoadState.Success
                            _appStatusBarUiState.value = AppStatusBarUiState.Idle
                        }
                        is ResultM.Failure -> {
                            _loadFullState.value = FullStoryLoadState.Error(storyFull.error)
                            _appStatusBarUiState.value = AppStatusBarUiState.Error(storyFull.error)
                        }
                        ResultM.Loading -> {
                            _loadReviewsState.value = StoryReviewsUiState.Loading
                        }
                    }
                }
            }
    }

    private fun storyReviewsStreamByStoryId(storyId: String) {
        _loadReviewsState.value = StoryReviewsUiState.Loading
        viewModelScope.launch {
            getReviewsStreamUseCase(storyId)
                .collect { reviews ->
                    when (reviews) {
                        is ResultM.Success -> {
                            _loadReviewsState.value = StoryReviewsUiState.Success(reviews.data)
                        }
                        is ResultM.Failure -> {
                            _loadReviewsState.value = StoryReviewsUiState.Error(reviews.error)
                            _appStatusBarUiState.value = AppStatusBarUiState.Error(reviews.error)
                        }
                        ResultM.Loading -> {
                            _loadReviewsState.value = StoryReviewsUiState.Loading
                        }
                    }
                }
        }
    }

    fun resetLoadState() {
        _loadFullState.value = FullStoryLoadState.Idle
    }
}