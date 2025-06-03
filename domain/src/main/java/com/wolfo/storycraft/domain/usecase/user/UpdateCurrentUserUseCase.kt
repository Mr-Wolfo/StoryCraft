package com.wolfo.storycraft.domain.usecase.user

import com.wolfo.storycraft.domain.ResultM
import com.wolfo.storycraft.domain.model.user.User
import com.wolfo.storycraft.domain.model.user.UserUpdate
import com.wolfo.storycraft.domain.repository.UserRepository

/**
 * Use Case для обновления данных текущего пользователя.
 */
class UpdateCurrentUserUseCase(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(userData: UserUpdate): ResultM<User> {
        return userRepository.updateCurrentUser(userData)
    }
}