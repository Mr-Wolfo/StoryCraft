package com.wolfo.storycraft.domain.repository

import com.wolfo.storycraft.domain.ResultM
import com.wolfo.storycraft.domain.model.auth.UserRegisterRequest
import com.wolfo.storycraft.domain.model.user.User
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    /**
     * Поток, указывающий, аутентифицирован ли пользователь в данный момент.
     * Основан на наличии токена доступа.
     */
    val isLoggedInFlow: Flow<Boolean>

    /**
     * Выполняет регистрацию пользователя.
     * При успехе сохраняет токены и данные пользователя.
     *
     * @param registrationData Данные для регистрации.
     * @return Result<UserEntity> - Успех с данными пользователя или ошибка DataError.
     */
    suspend fun register(registrationData: UserRegisterRequest): ResultM<User>

    /**
     * Выполняет вход пользователя.
     * При успехе сохраняет токены и данные пользователя.
     *
     * @param username Имя пользователя.
     * @param password Пароль.
     * @return Result<UserEntity> - Успех с данными пользователя или ошибка DataError.
     */
    suspend fun login(username: String, password: String): ResultM<User>

    /**
     * Выполняет выход пользователя, очищая токены и, возможно, локальные данные.
     */
    suspend fun logout()

    /**
     * Получает текущий access token.
     */
    suspend fun getAccessToken(): String?
}