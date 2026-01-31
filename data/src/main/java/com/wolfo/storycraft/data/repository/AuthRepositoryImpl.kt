package com.wolfo.storycraft.data.repository

import com.wolfo.storycraft.data.local.data_store.AuthTokenManager
import com.wolfo.storycraft.data.local.data_store.AuthTokens
import com.wolfo.storycraft.data.mapper.toDomain
import com.wolfo.storycraft.data.mapper.toDto
import com.wolfo.storycraft.data.remote.RemoteDataSource
import com.wolfo.storycraft.data.utils.NetworkHandler
import com.wolfo.storycraft.domain.DataError
import com.wolfo.storycraft.domain.ResultM
import com.wolfo.storycraft.domain.model.auth.UserRegisterRequest
import com.wolfo.storycraft.domain.model.user.User
import com.wolfo.storycraft.domain.repository.AuthRepository
import com.wolfo.storycraft.domain.repository.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class AuthRepositoryImpl (
    private val remoteDataSource: RemoteDataSource,
    private val tokenManager: AuthTokenManager,
    private val userRepository: UserRepository,
    private val networkHandler: NetworkHandler
    // TODO: Рассмотреть возможность инжектирования LocalDataSource для очистки других кэшей при логауте
    // private val localDataSource: LocalDataSource
) : AuthRepository {

    // Статус входа определяется наличием access токена
    override val isLoggedInFlow: Flow<Boolean> = tokenManager.tokensFlow
        .map { it.accessToken != null }

    override suspend fun register(registrationData: UserRegisterRequest): ResultM<User> = withContext(Dispatchers.IO) {
        networkHandler.handleNetworkCall(
            call = { remoteDataSource.registerUser(registrationData.toDto()) },
            transform = { authResponse -> authResponse.user.toDomain() },
            onSuccess = { authResponse ->
                // Сохраняем токены
                val tokens = AuthTokens(
                    accessToken = authResponse.token.accessToken,
                    refreshToken = authResponse.token.refreshToken
                )
                tokenManager.saveTokens(tokens)
                // Сохраняем пользователя через UserRepository
                val userDomain = authResponse.user.toDomain()
                tokenManager.saveUserId(userDomain.id)
                userRepository.saveCurrentUser(userDomain)
            },
            onError = { error ->
                val finalError = if (error is DataError.Network && error.code == 422) {
                    DataError.Authentication("Registration failed: ${error.message ?: "Validation error"}")
                } else {
                    error
                }
                ResultM.failure(finalError)
            }
        )
    }

    override suspend fun login(username: String, password: String): ResultM<User> = withContext(Dispatchers.IO) {
        networkHandler.handleNetworkCall(
            call = { remoteDataSource.loginUser(username, password) },
            transform = { authResponse -> authResponse.user.toDomain() },
            onSuccess = { authResponse ->
                // Сохраняем токены
                val tokens = AuthTokens(
                    accessToken = authResponse.token.accessToken,
                    refreshToken = authResponse.token.refreshToken
                )
                tokenManager.saveTokens(tokens)
                // Сохраняем пользователя через UserRepository
                val userDomain = authResponse.user.toDomain()
                tokenManager.saveUserId(userDomain.id)
                userRepository.saveCurrentUser(userDomain)
            },
            onError = { error ->
                val finalError = if ((error is DataError.Network && (error.code == 400 || error.code == 422)) || error is DataError.Authentication) {
                    DataError.Authentication("Login failed: Invalid credentials or validation error.")
                } else {
                    error
                }
                ResultM.failure(finalError)
            }
        )
    }

    override suspend fun logout() = withContext(Dispatchers.IO) {
        tokenManager.clearTokens()
        tokenManager.clearUserId()
        userRepository.clearCurrentUser()
    }

    override suspend fun getAccessToken(): String? = withContext(Dispatchers.IO) {
        tokenManager.getAccessToken()
    }
}