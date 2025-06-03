package com.wolfo.storycraft.domain.usecase.user

import com.wolfo.storycraft.domain.ResultM
import com.wolfo.storycraft.domain.model.user.User
import com.wolfo.storycraft.domain.repository.UserRepository

/**
 * Use Case для принудительного обновления данных текущего пользователя с сервера.
 */
class RefreshCurrentUserUseCase(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(): ResultM<User> {
        return userRepository.refreshCurrentUser()
    }
}