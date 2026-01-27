package com.wolfo.storycraft.presentation.ui.features.profile

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wolfo.storycraft.domain.DataError
import com.wolfo.storycraft.domain.ResultM
import com.wolfo.storycraft.domain.model.user.User
import com.wolfo.storycraft.domain.model.user.UserUpdate
import com.wolfo.storycraft.domain.usecase.story.DeleteStoryUseCase
import com.wolfo.storycraft.domain.usecase.user.GetCurrentUserStreamUseCase
import com.wolfo.storycraft.domain.usecase.user.UpdateCurrentUserAvatarUseCase
import com.wolfo.storycraft.domain.usecase.user.UpdateCurrentUserUseCase
import com.wolfo.storycraft.presentation.ui.utils.compressImage
import com.wolfo.storycraft.presentation.ui.utils.toFile
import com.wolfo.storycraft.presentation.ui.features.story_list.AppStatusBarUiState
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
    private val appContext: Context,
    private val getCurrentUserStreamUseCase: GetCurrentUserStreamUseCase,
    private val updateCurrentUserAvatarUseCase: UpdateCurrentUserAvatarUseCase,
    private val updateCurrentUserUseCase: UpdateCurrentUserUseCase,
    private val deleteStoryUseCase: DeleteStoryUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow<ProfileUiState<User>>(ProfileUiState.Idle)
    val uiState = _uiState.asStateFlow()

    private val _appStatusBarUiState = MutableStateFlow<AppStatusBarUiState>(AppStatusBarUiState.Idle)
    val appStatusBarUiState = _appStatusBarUiState.asStateFlow()

    private val _editMode = MutableStateFlow(false)
    val editMode = _editMode.asStateFlow()

    private var currentJob: Job? = null

    init {
    }

    fun toggleEditMode() {
        _editMode.value = !_editMode.value
    }

    fun updateAvatar(uri: Uri) {
        viewModelScope.launch {
            _appStatusBarUiState.value = AppStatusBarUiState.Loading
            try {
                val file = uri.toFile(appContext)?.compressImage() // Добавляем сжатие

                if (file != null) {
                    when (val result = updateCurrentUserAvatarUseCase(file)) {
                        is ResultM.Success -> {
                            _appStatusBarUiState.value = AppStatusBarUiState.Success("Аватар обновлен")
                            loadProfile()
                        }
                        is ResultM.Failure -> {
                            _appStatusBarUiState.value = AppStatusBarUiState.Error(result.error)
                        }
                        ResultM.Loading -> {
                            _appStatusBarUiState.value = AppStatusBarUiState.Loading
                        }
                    }
                } else {
                    _appStatusBarUiState.value = AppStatusBarUiState.Error(DataError.Validation.UI("Не удалось обработать изображение"))
                }
            } catch (e: Exception) {
                _appStatusBarUiState.value = AppStatusBarUiState.Error(DataError.Validation.GENERAL(e.message ?: "Ошибка загрузки аватара"))
            }
        }
    }

    fun updateSignature(newSignature: String) {
        viewModelScope.launch {
            _appStatusBarUiState.value = AppStatusBarUiState.Loading
            val userUpdate = UserUpdate(
                email = null,
                signature = newSignature
            )
            when (val result = updateCurrentUserUseCase(userUpdate)) {
                is ResultM.Success -> {
                    _appStatusBarUiState.value = AppStatusBarUiState.Success("Подпись обновлена")
                    loadProfile()
                }
                is ResultM.Failure -> {
                    _appStatusBarUiState.value = AppStatusBarUiState.Error(result.error)
                }
                ResultM.Loading -> {
                    _appStatusBarUiState.value = AppStatusBarUiState.Loading
                }
            }
        }
    }

    fun deleteStory(storyId: String) {
        viewModelScope.launch {
            _appStatusBarUiState.value = AppStatusBarUiState.Loading
            when (val result = deleteStoryUseCase(storyId)) {
                is ResultM.Success -> {
                    _appStatusBarUiState.value = AppStatusBarUiState.Success("История удалена")
                    loadProfile()
                }
                is ResultM.Failure -> {
                    _appStatusBarUiState.value = AppStatusBarUiState.Error(result.error)
                }
                ResultM.Loading -> {
                    _appStatusBarUiState.value = AppStatusBarUiState.Loading
                }
            }
        }
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