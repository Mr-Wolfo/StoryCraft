package com.wolfo.storycraft.data.repository

import com.wolfo.storycraft.data.local.data_store.AuthTokenManager
import com.wolfo.storycraft.data.local.db.LocalDataSource
import com.wolfo.storycraft.data.mapper.toDomain
import com.wolfo.storycraft.data.mapper.toDto
import com.wolfo.storycraft.data.mapper.toEntity
import com.wolfo.storycraft.data.remote.RemoteDataSource
import com.wolfo.storycraft.data.utils.BaseApiResponse
import com.wolfo.storycraft.data.utils.NetworkResult
import com.wolfo.storycraft.domain.model.AuthRequest
import com.wolfo.storycraft.domain.model.RegisterRequest
import com.wolfo.storycraft.domain.model.User
import com.wolfo.storycraft.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

class UserRepositoryImpl(
    private val remoteDataSource: RemoteDataSource,
    private val localDataSource: LocalDataSource,
    private val authTokenManager: AuthTokenManager
) : UserRepository, BaseApiResponse() {
    override suspend fun register(
        registerRequest: RegisterRequest
    ) {
        val result = safeApiCall { remoteDataSource.register(registerRequest.toDto()) }
        when(result) {
            is NetworkResult.Success -> {
                val token = result.data?.token
                token?.let { authTokenManager.saveToken(token) } ?: authTokenManager.clearToken()
                val user = result.data?.user
                user?.let { localDataSource.saveUser(user.toEntity()) }
            }
            is NetworkResult.Error -> {
                throw Throwable(result.message)
            }
            is NetworkResult.Loading -> {}
        }
    }

    override suspend fun login(
        authRequest: AuthRequest
    ) {
        val result = safeApiCall { remoteDataSource.login(authRequest.toDto()) }
        when(result) {
            is NetworkResult.Success -> {
                val token = result.data?.token
                token?.let { authTokenManager.saveToken(token) } ?: authTokenManager.clearToken()
                val user = result.data?.user
                user?.let { localDataSource.saveUser(user.toEntity()) }
            }
            is NetworkResult.Error -> {
                throw Throwable(result.message)
            }
            is NetworkResult.Loading -> {}
        }
    }

    override suspend fun observeProfile(): Flow<User> = flow {

        val localDataFlow = localDataSource.observeProfile().map { it.toDomain() }
        localDataFlow.collect { user ->
            emit(user)
        }
    }

    suspend fun loadProfile() {
        val token = authTokenManager.getToken().first() ?: ""
        val result = safeApiCall { remoteDataSource.getProfile(token) }
        when(result) {
            is NetworkResult.Success -> {
                localDataSource
            }
            is NetworkResult.Error -> {
                throw Throwable(result.message)
            }
            is NetworkResult.Loading -> {}
        }
    }
}