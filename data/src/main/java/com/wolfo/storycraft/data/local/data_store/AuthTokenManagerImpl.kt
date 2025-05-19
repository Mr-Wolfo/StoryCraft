package com.wolfo.storycraft.data.local.data_store

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking

class AuthTokenManagerImpl(
    private val context: Context
) : AuthTokenManager {

    // Объявляем DataStore на уровне top-level
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "auth_tokens")

    // Ключи для Preferences DataStore
    private object PreferencesKeys {
        val ACCESS_TOKEN = stringPreferencesKey("access_token")
        val REFRESH_TOKEN = stringPreferencesKey("refresh_token")
        val USER_ID = stringPreferencesKey("user_id")
    }

    override val userIdFlow: Flow<String?> = context.dataStore.data.map { preferences ->
                preferences[PreferencesKeys.USER_ID]
            }

    override val tokensFlow: Flow<AuthTokens> = context.dataStore.data
        .map { preferences ->
            AuthTokens(
                accessToken = preferences[PreferencesKeys.ACCESS_TOKEN],
                refreshToken = preferences[PreferencesKeys.REFRESH_TOKEN]
            )
        }

    override suspend fun getUserId(): String? {
        return userIdFlow.first()
    }

    override suspend fun saveUserId(userId: String) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.USER_ID] = userId
        }
    }

    override suspend fun clearUserId() {
        context.dataStore.edit { preferences ->
            preferences.remove(PreferencesKeys.USER_ID)
        }
    }

    override suspend fun getAccessToken(): String? {
        return tokensFlow.first().accessToken
    }

    override suspend fun getRefreshToken(): String? {
        return tokensFlow.first().refreshToken
    }

    override suspend fun getTokens(): AuthTokens {
        return tokensFlow.first()
    }

    /**
     * ВНИМАНИЕ: Эта функция использует runBlocking и должна вызываться только там,
     * где невозможно использовать suspend-функции.
     * Не использовать ее в основном потоке или корутинах без крайней необходимости.
     */
    override fun getTokensSync(): AuthTokens {
        return runBlocking { // Блокируем поток для получения результата из Flow
            tokensFlow.first()
        }
    }

    override suspend fun saveTokens(tokens: AuthTokens) {
        context.dataStore.edit { preferences ->
            if (tokens.accessToken != null) {
                preferences[PreferencesKeys.ACCESS_TOKEN] = tokens.accessToken
            } else {
                preferences.remove(PreferencesKeys.ACCESS_TOKEN)
            }
            if (tokens.refreshToken != null) {
                preferences[PreferencesKeys.REFRESH_TOKEN] = tokens.refreshToken
            } else {
                preferences.remove(PreferencesKeys.REFRESH_TOKEN)
            }
        }
    }

    override suspend fun clearTokens() {
        context.dataStore.edit { preferences ->
            preferences.remove(PreferencesKeys.ACCESS_TOKEN)
            preferences.remove(PreferencesKeys.REFRESH_TOKEN)
            preferences.remove(PreferencesKeys.USER_ID)
        }
    }
}