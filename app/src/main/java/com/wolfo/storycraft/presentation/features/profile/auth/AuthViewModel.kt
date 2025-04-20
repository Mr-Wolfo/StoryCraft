package com.wolfo.storycraft.presentation.features.profile.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wolfo.storycraft.domain.model.RegisterRequest
import com.wolfo.storycraft.domain.usecase.RegisterUseCase
import kotlinx.coroutines.launch

class AuthViewModel(
    private val registerUseCase: RegisterUseCase
): ViewModel() {
    fun register(name: String, email: String, password: String) {
        viewModelScope.launch {
            try {
                registerUseCase(RegisterRequest(name, email, password))
            } catch (e: Throwable) {

            }
        }
    }
}