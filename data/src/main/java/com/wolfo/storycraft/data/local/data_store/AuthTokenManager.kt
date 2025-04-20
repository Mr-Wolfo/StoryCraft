package com.wolfo.storycraft.data.local.data_store

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class AuthTokenManager(private val context: Context) {
    private val Context.dataStore by preferencesDataStore(name = "auth_token")

    private val authTokenKey = stringPreferencesKey("auth_token")

    suspend fun saveToken(token: String) {
        context.dataStore.edit { preferences ->
            preferences[authTokenKey] = token
        }
    }

    suspend fun getToken(): Flow<String?> {
        val token = context.dataStore.data.map { preferences ->
            preferences[authTokenKey]
        }
        return token
    }

    suspend fun clearToken() {
        context.dataStore.edit { preferences ->
            preferences.remove(authTokenKey)
        }
    }
}