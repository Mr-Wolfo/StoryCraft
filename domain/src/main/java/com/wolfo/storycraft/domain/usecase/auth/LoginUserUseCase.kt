package com.wolfo.storycraft.domain.usecase.auth

import com.wolfo.storycraft.domain.ResultM
import com.wolfo.storycraft.domain.model.user.User
import com.wolfo.storycraft.domain.repository.AuthRepository


/**
 * Use Case для входа пользователя.
 */
class LoginUserUseCase(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(username: String, password: String): ResultM<User> {
        return authRepository.login(username, password)
    }
}