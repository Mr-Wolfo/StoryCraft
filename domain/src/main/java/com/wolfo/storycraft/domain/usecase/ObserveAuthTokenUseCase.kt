package com.wolfo.storycraft.domain.usecase

import com.wolfo.storycraft.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow

class ObserveAuthTokenUseCase(
    private val userRepository: UserRepository
) {
    operator fun invoke(): Flow<Boolean> {
        return userRepository.observeAuthToken()
    }
}