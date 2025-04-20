package com.wolfo.storycraft.domain.usecase

import com.wolfo.storycraft.domain.model.RegisterRequest
import com.wolfo.storycraft.domain.repository.UserRepository

class RegisterUseCase(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(registerRequest: RegisterRequest) {
        userRepository.register(registerRequest)
    }
}