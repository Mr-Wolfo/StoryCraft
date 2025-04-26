package com.wolfo.storycraft.domain.usecase

import com.wolfo.storycraft.domain.repository.UserRepository

class LogoutUseCase(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke() {
        userRepository.logout()
    }
}