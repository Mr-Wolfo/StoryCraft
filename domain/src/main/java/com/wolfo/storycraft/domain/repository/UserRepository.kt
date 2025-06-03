package com.wolfo.storycraft.domain.repository

import com.wolfo.storycraft.domain.ResultM
import com.wolfo.storycraft.domain.model.user.User
import com.wolfo.storycraft.domain.model.user.UserSimple
import com.wolfo.storycraft.domain.model.user.UserUpdate
import kotlinx.coroutines.flow.Flow
import java.io.File

interface UserRepository {
    /**
     * Поток с данными текущего аутентифицированного пользователя из локальной БД.
     * null, если пользователь не вошел или данные еще не загружены.
     */
    val currentUserFlow: Flow<ResultM<User>>

    /**
     * Получает актуальные данные текущего пользователя.
     * Пытается загрузить из сети и обновить кэш.
     *
     * @return Result<UserEntity> - Успех или ошибка DataError.
     */
    suspend fun refreshCurrentUser(): ResultM<User>

    /**
     * Получает базовый профиль пользователя по ID.
     * Сначала проверяет кэш, потом сеть.
     *
     * @param userId ID пользователя.
     * @return Flow<Result<UserSimpleDto>> Поток с результатом (может содержать кэш, потом обновленный результат).
     */
    // TODO: Реализовать кэширование для профилей других юзеров, если нужно
    suspend fun getUserProfile(userId: String): ResultM<UserSimple> // Пока без Flow и кэша для простоты

    /**
     * Обновляет данные текущего пользователя (например, email).
     *
     * @param userData Новые данные для обновления.
     * @return Result<UserEntity> - Успех с обновленным пользователем или ошибка DataError.
     */
    suspend fun updateCurrentUser(userData: UserUpdate): ResultM<User>

    /**
     * Обновляет аватар текущего пользователя.
     *
     * @param avatarFile Файл нового аватара.
     * @return Result<UserEntity> - Успех с обновленным пользователем или ошибка DataError.
     */
    suspend fun updateCurrentUserAvatar(avatarFile: File): ResultM<User>

    // Методы для AuthRepository (или используем LocalDataSource напрямую в Auth)
    suspend fun saveCurrentUser(user: User)
    suspend fun clearCurrentUser()
    suspend fun getCurrentUserId(): String? // Может быть полезно
}