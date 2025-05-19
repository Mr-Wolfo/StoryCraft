package com.wolfo.storycraft.domain.usecase.auth

import com.wolfo.storycraft.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow

/**
 * Use Case для проверки статуса авторизации пользователя.
 */
class CheckLoginStatusUseCase(
    private val authRepository: AuthRepository
) {
    operator fun invoke(): Flow<Boolean> = authRepository.isLoggedInFlow
}
