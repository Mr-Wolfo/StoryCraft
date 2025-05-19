package com.wolfo.storycraft.data.local.data_store

import kotlinx.coroutines.flow.Flow

// Модель для хранения токенов
data class AuthTokens(
    val accessToken: String?,
    val refreshToken: String?
)

interface AuthTokenManager {
    // Потоки для наблюдения за токенами (например, для UI)
    val tokensFlow: Flow<AuthTokens>
    val userIdFlow: Flow<String?>

    suspend fun saveUserId(userId: String)
    suspend fun getUserId(): String?
    suspend fun clearUserId()

    // Функции для получения текущих токенов (могут блокировать поток)
    suspend fun getAccessToken(): String?
    suspend fun getRefreshToken(): String?
    suspend fun getTokens(): AuthTokens

    // Функция для синхронного получения токенов (для Authenticator)
    fun getTokensSync(): AuthTokens

    // Функции для сохранения и очистки токенов
    suspend fun saveTokens(tokens: AuthTokens)
    suspend fun clearTokens()
}