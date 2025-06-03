package com.wolfo.storycraft.domain.usecase.user

import com.wolfo.storycraft.domain.ResultM
import com.wolfo.storycraft.domain.model.user.UserSimple
import com.wolfo.storycraft.domain.repository.UserRepository

/**
 * Use Case для получения публичного профиля пользователя по ID.
 */
class GetUserProfileUseCase(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(userId: String): ResultM<UserSimple> {
        return userRepository.getUserProfile(userId)
    }
}