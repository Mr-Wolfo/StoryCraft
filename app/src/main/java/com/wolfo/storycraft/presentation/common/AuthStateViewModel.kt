package com.wolfo.storycraft.presentation.common

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wolfo.storycraft.domain.usecase.LogoutUseCase
import com.wolfo.storycraft.domain.usecase.ObserveAuthTokenUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AuthStateViewModel(
    private val observeAuthTokenUseCase: ObserveAuthTokenUseCase,
    private val logoutUseCase: LogoutUseCase
): ViewModel() {
    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn = _isLoggedIn.asStateFlow()

    init {
        viewModelScope.launch {
            observeAuthTokenUseCase()
                .collect { available ->
                    _isLoggedIn.value = available
                }
        }
    }

    fun logout() {
        viewModelScope.launch {
            logoutUseCase()
        }
    }
}