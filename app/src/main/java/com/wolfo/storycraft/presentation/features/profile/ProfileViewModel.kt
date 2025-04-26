package com.wolfo.storycraft.presentation.features.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wolfo.storycraft.domain.model.User
import com.wolfo.storycraft.domain.usecase.ObserveUserProfileUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ProfileUiState(
    val isLoading: Boolean = false,
    val user: User? = null,
    val error: String? = null
)

class ProfileViewModel(
    private val observeUserProfileUseCase: ObserveUserProfileUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState = _uiState.asStateFlow()

    init {
        observeUserProfile()
    }

    private fun observeUserProfile() {
        viewModelScope.launch {
            observeUserProfileUseCase()
                .onStart { _uiState.update { it.copy(isLoading = true, error = null) } }
                .catch { throwable ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = throwable.message ?: "Unknown error"
                        )
                    }
                }
                .collect { user ->
                    _uiState.update { it.copy(isLoading = false, user = user) }
                }
        }
    }
}