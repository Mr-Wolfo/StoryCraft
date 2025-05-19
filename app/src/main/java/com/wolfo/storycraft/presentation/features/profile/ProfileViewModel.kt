package com.wolfo.storycraft.presentation.features.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wolfo.storycraft.domain.DataError
import com.wolfo.storycraft.domain.ResultM
import com.wolfo.storycraft.domain.model.User
import com.wolfo.storycraft.domain.usecase.user.GetCurrentUserStreamUseCase
import com.wolfo.storycraft.presentation.features.story_list.AppStatusBarUiState
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class ProfileUiState<out T> {
    object Idle : ProfileUiState<Nothing>()
    object Loading : ProfileUiState<Nothing>()
    data class Success (val data: User) : ProfileUiState<User>()
    data class Error<out T>(val error: DataError) : ProfileUiState<T>()
}

class ProfileViewModel(
    private val getCurrentUserStreamUseCase: GetCurrentUserStreamUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow<ProfileUiState<User>>(ProfileUiState.Idle)
    val uiState = _uiState.asStateFlow()

    private val _appStatusBarUiState = MutableStateFlow<AppStatusBarUiState<DataError>>(AppStatusBarUiState.Idle)
    val appStatusBarUiState = _appStatusBarUiState.asStateFlow()

    private var currentJob: Job? = null

    init {
        userProfileStream()
    }

    fun loadProfile() = userProfileStream()

    private fun userProfileStream() {
        _uiState.value = ProfileUiState.Loading
        _appStatusBarUiState.value = AppStatusBarUiState.Loading

        currentJob?.cancel()

        viewModelScope.launch {

            getCurrentUserStreamUseCase()
                .collect { result ->
                    when (result) {
                        is ResultM.Success -> {
                            _uiState.value = ProfileUiState.Success(result.data)
                            _appStatusBarUiState.value = AppStatusBarUiState.Idle
                        }

                        is ResultM.Failure -> {
                            if (result.cachedData != null) {
                                _uiState.value = ProfileUiState.Success(
                                    data = result.cachedData as User
                                )
                            }

                            _appStatusBarUiState.value =
                                AppStatusBarUiState.Error(error = result.error)

                        }

                        ResultM.Loading -> {
                            _appStatusBarUiState.value = AppStatusBarUiState.Loading
                        }
                    }
                }
        }
    }
}