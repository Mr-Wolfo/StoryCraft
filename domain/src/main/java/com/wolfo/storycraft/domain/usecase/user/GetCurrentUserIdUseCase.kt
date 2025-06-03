package com.wolfo.storycraft.domain.usecase.user

import com.wolfo.storycraft.domain.repository.UserRepository

/**
 * Use Case для получения Id текущего авторизованного пользователя
 */
class GetCurrentUserIdUseCase(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(): String? {
        return userRepository.getCurrentUserId()
    }
}