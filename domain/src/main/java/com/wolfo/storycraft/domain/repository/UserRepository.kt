package com.wolfo.storycraft.domain.repository

import com.wolfo.storycraft.domain.model.AuthRequest
import com.wolfo.storycraft.domain.model.RegisterRequest
import com.wolfo.storycraft.domain.model.User
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    suspend fun register(registerRequest: RegisterRequest)
    suspend fun login(authRequest: AuthRequest)
    suspend fun observeProfile(): Flow<User>
}