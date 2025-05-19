package com.wolfo.storycraft.presentation.features.profile.auth

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wolfo.storycraft.domain.DataError
import com.wolfo.storycraft.domain.ResultM
import com.wolfo.storycraft.domain.model.User
import com.wolfo.storycraft.domain.model.UserRegisterRequest
import com.wolfo.storycraft.domain.usecase.auth.LoginUserUseCase
import com.wolfo.storycraft.domain.usecase.auth.RegisterUserUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class AuthUiState<out T> {
    object Idle : AuthUiState<Nothing>()
    object Loading : AuthUiState<Nothing>()
    object Success : AuthUiState<Nothing>()
    data class Error<out T>(val error: DataError) : AuthUiState<T>()
}

class AuthViewModel(
    private val registerUserUseCase: RegisterUserUseCase,
    private val loginUserUseCase: LoginUserUseCase
): ViewModel() {

    private val _uiState = MutableStateFlow<AuthUiState<Nothing>>(AuthUiState.Idle)
    val uiState = _uiState.asStateFlow()

    fun register(userName: String, email: String, password: String) {

        if (userName.isBlank() || email.isBlank() || password.isBlank()) {
            _uiState.value =
                AuthUiState.Error(error = DataError.Validation("Fields cannot be empty"))
            return
        }

        handleAuth {
            registerUserUseCase(UserRegisterRequest(userName, email, password))
        }
    }


    fun login(userName: String, password: String) {
        if (userName.isBlank() || password.isBlank()) {
            _uiState.value = AuthUiState.Error(error = DataError.Validation("Fields cannot be empty"))
            return
        }

        handleAuth {
            loginUserUseCase(userName, password)
        }
    }

    private fun handleAuth(
        authOperation: suspend () -> ResultM<User>
    ) {
        _uiState.value = AuthUiState.Loading
        viewModelScope.launch {
            when (val result = authOperation()) {
                is ResultM.Success -> {
                    _uiState.value = AuthUiState.Success
                }
                is ResultM.Failure -> {
                    _uiState.value = AuthUiState.Error(result.error)
                    Log.d("AuthVM", "Error: ${result.error.message}, cause: ${result.error.cause}")
                }
                ResultM.Loading -> {
                    // Уже обработали выше
                }
            }
        }
    }
}