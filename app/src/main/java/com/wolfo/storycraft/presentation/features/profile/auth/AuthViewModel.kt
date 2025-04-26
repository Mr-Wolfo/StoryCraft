package com.wolfo.storycraft.presentation.features.profile.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wolfo.storycraft.domain.model.AuthRequest
import com.wolfo.storycraft.domain.model.RegisterRequest
import com.wolfo.storycraft.domain.usecase.LoginUseCase
import com.wolfo.storycraft.domain.usecase.RegisterUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AuthUiState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val error: String? = null
)

sealed class AuthError {
    data class ValidationError(val message: String) : AuthError()
    data class ServerError(val message: String) : AuthError()
    object NetworkError : AuthError()
}

class AuthViewModel(
    private val registerUseCase: RegisterUseCase,
    private val loginUseCase: LoginUseCase
): ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState = _uiState.asStateFlow()

    fun register(userName: String, email: String, password: String) {
        if (userName.isBlank() || email.isBlank() || password.isBlank()) {
            _uiState.update { it.copy(error = "Fields cannot be empty") }
            return
        }

        performAuthOperation {
            registerUseCase(RegisterRequest(userName, email, password))
        }
    }

    fun login(userName: String, password: String) {
        if (userName.isBlank() || password.isBlank()) {
            _uiState.update { it.copy(error = "Fields cannot be empty") }
            return
        }

        performAuthOperation {
            loginUseCase(AuthRequest(userName, password))
        }
    }

    private fun performAuthOperation(block: suspend () -> Unit) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                block()
                _uiState.update { it.copy(isLoading = false, isSuccess = true) }
            } catch (e: Throwable) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "Auth failed"
                    )
                }
            }
        }
    }
}