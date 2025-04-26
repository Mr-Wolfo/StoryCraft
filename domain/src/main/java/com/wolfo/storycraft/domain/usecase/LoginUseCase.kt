package com.wolfo.storycraft.domain.usecase

import com.wolfo.storycraft.domain.model.AuthRequest
import com.wolfo.storycraft.domain.repository.UserRepository

class LoginUseCase(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(authRequest: AuthRequest) {
        userRepository.login(authRequest)
    }
}