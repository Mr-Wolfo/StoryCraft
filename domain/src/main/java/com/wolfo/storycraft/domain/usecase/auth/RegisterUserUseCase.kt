package com.wolfo.storycraft.domain.usecase.auth

import com.wolfo.storycraft.domain.ResultM
import com.wolfo.storycraft.domain.model.user.User
import com.wolfo.storycraft.domain.model.auth.UserRegisterRequest
import com.wolfo.storycraft.domain.repository.AuthRepository

/**
 * Use Case для регистрации пользователя.
 */
class RegisterUserUseCase(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(registrationData: UserRegisterRequest): ResultM<User> {
        return authRepository.register(registrationData)
    }
}