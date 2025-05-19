package com.wolfo.storycraft.domain.usecase.user

import com.wolfo.storycraft.domain.ResultM
import com.wolfo.storycraft.domain.model.User
import com.wolfo.storycraft.domain.repository.UserRepository
import java.io.File


/**
 * Use Case для обновления аватара текущего пользователя.
 */
class UpdateCurrentUserAvatarUseCase(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(avatarFile: File): ResultM<User> {
        return userRepository.updateCurrentUserAvatar(avatarFile)
    }
}