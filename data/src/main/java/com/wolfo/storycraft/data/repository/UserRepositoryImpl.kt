package com.wolfo.storycraft.data.repository

import android.util.Log
import com.wolfo.storycraft.data.local.data_store.AuthTokenManager
import com.wolfo.storycraft.data.local.db.LocalDataSource
import com.wolfo.storycraft.data.mapper.toDomain
import com.wolfo.storycraft.data.mapper.toDto
import com.wolfo.storycraft.data.mapper.toEntity
import com.wolfo.storycraft.data.remote.RemoteDataSource
import com.wolfo.storycraft.data.utils.NetworkHandler
import com.wolfo.storycraft.data.utils.RepositoryHandler
import com.wolfo.storycraft.domain.DataError
import com.wolfo.storycraft.domain.ResultM
import com.wolfo.storycraft.domain.model.User
import com.wolfo.storycraft.domain.model.UserSimple
import com.wolfo.storycraft.domain.model.UserUpdate
import com.wolfo.storycraft.domain.repository.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import java.io.File

class UserRepositoryImpl(
    private val localDataSource: LocalDataSource,
    private val remoteDataSource: RemoteDataSource,
    private val tokenManager: AuthTokenManager, // Нужен для получения ID текущего пользователя
    private val networkHandler: NetworkHandler,
    private val repositoryHandler: RepositoryHandler// Утилита для обработки сетевых вызовов
) : UserRepository {

    @OptIn(ExperimentalCoroutinesApi::class)
    override val currentUserFlow: Flow<ResultM<User>> = tokenManager.userIdFlow
        .flatMapLatest { userId ->
            Log.d("UserRepository", "User Flow Start")

            repositoryHandler.getData(
                localDataCall = { localDataSource.getUserFlow(userId!!) },
                networkResult = { refreshCurrentUser() },
                localTransform = { it!!.toDomain() },
                sourcesDataMergeTransform = { user, result -> user.copy(stories = result.stories) },
                checkOrErrorBefore = {
                    if (userId == null) ResultM.Failure(DataError.Authentication())
                    else null
                }
            ).flowOn(Dispatchers.IO)
        }

    override suspend fun refreshCurrentUser(): ResultM<User> = withContext(Dispatchers.IO) {
        networkHandler.handleNetworkCall(
            call = { remoteDataSource.getCurrentUser() },
            transform = { userDto -> userDto.toDomain() },
            onSuccess = { userDto ->
                val userEntity = userDto.toEntity()
                localDataSource.saveUser(userEntity)
                tokenManager.saveUserId(userEntity.id)
            },
            onError = { error ->
                if (error is DataError.Authentication) {
                    tokenManager.clearTokens()
                    tokenManager.clearUserId()
                }
                ResultM.failure(error)
            }
        )
    }

    // Получение простого профиля пользователя (без кэширования)
    override suspend fun getUserProfile(userId: String): ResultM<UserSimple> = withContext(Dispatchers.IO) {
        networkHandler.handleNetworkCall(
            call = { remoteDataSource.getUserProfile(userId) },
            transform = { userSimpleDto -> userSimpleDto.toDomain() }, // DTO -> Domain
            onSuccess = { /* Не кэшируем простые профили по умолчанию */ }
        )
    }

    // Обновление данных текущего пользователя
    override suspend fun updateCurrentUser(userData: UserUpdate): ResultM<User> = withContext(Dispatchers.IO) {
        networkHandler.handleNetworkCall(
            call = { remoteDataSource.updateUser(userData.toDto()) }, // Domain -> DTO
            transform = { userDto -> userDto.toDomain() }, // DTO -> Domain
            onSuccess = { updatedUserDto ->
                // Обновляем кэш в локальной БД
                localDataSource.saveUser(updatedUserDto.toEntity())
            }
        )
    }

    // Обновление аватара текущего пользователя
    override suspend fun updateCurrentUserAvatar(avatarFile: File): ResultM<User> = withContext(Dispatchers.IO) {
        networkHandler.handleNetworkCall(
            call = { remoteDataSource.updateUserAvatar(avatarFile) },
            transform = { userDto -> userDto.toDomain() }, // DTO -> Domain
            onSuccess = { updatedUserDto ->
                // Обновляем кэш в локальной БД
                localDataSource.saveUser(updatedUserDto.toEntity())
            }
        )
    }


    // --- Вспомогательные методы для AuthRepository ---

    /**
     * Сохраняет данные пользователя в локальную БД.
     * Вызывается из AuthRepository после успешного логина/регистрации.
     */
    override suspend fun saveCurrentUser(user: User) = withContext(Dispatchers.IO) {
        localDataSource.saveUser(user.toEntity())
        // Убеждаемся, что ID пользователя сохранен в менеджере токенов
        tokenManager.saveUserId(user.id)
    }

    /**
     * Очищает данные текущего пользователя из локальной БД.
     * Вызывается из AuthRepository при логауте.
     */
    override suspend fun clearCurrentUser() = withContext(Dispatchers.IO) {
        val userId = tokenManager.getUserId() // Получаем ID текущего юзера
        if (userId != null) {
            localDataSource.clearUsers() // Удаляем из Room
        }
        // ID уже очищен в tokenManager.clearUserId() внутри AuthRepository.logout()
    }

    /**
     * Получает ID текущего пользователя (если есть).
     * Может быть полезен в других частях приложения.
     */
    override suspend fun getCurrentUserId(): String? = withContext(Dispatchers.IO) {
        tokenManager.getUserId()
    }
}