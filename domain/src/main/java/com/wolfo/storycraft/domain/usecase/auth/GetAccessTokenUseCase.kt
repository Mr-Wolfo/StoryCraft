package com.wolfo.storycraft.domain.usecase.auth

import com.wolfo.storycraft.domain.repository.AuthRepository

/**
 * Use Case для получения access token (может быть полезно в редких случаях).
 */
class GetAccessTokenUseCase(
    private val authRepository: AuthRepository
){
    suspend operator fun invoke(): String? {
        return authRepository.getAccessToken()
    }
}