package com.wolfo.storycraft.domain.usecase.user

import com.wolfo.storycraft.domain.ResultM
import com.wolfo.storycraft.domain.model.User
import com.wolfo.storycraft.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow

/**
 * Use Case для получения потока данных текущего пользователя из локального кэша.
 */
class GetCurrentUserStreamUseCase(
    private val userRepository: UserRepository
) {
    operator fun invoke(): Flow<ResultM<User>> = userRepository.currentUserFlow
}