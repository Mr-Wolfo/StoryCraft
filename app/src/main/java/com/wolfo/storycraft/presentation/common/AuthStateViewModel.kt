package com.wolfo.storycraft.presentation.common

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wolfo.storycraft.domain.usecase.auth.CheckLoginStatusUseCase
import com.wolfo.storycraft.domain.usecase.auth.LogoutUserUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AuthStateViewModel(
    private val checkLoginStatusUseCase: CheckLoginStatusUseCase,
    private val logoutUserUseCase: LogoutUserUseCase
): ViewModel() {
    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn = _isLoggedIn.asStateFlow()

    init {
        viewModelScope.launch {
            checkLoginStatusUseCase()
                .collect { available ->
                    _isLoggedIn.value = available
                }
        }
    }

    fun logout() {
        viewModelScope.launch {
            logoutUserUseCase()
        }
    }
}