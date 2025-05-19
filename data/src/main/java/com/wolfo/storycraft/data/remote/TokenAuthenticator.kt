package com.wolfo.storycraft.data.remote

import com.wolfo.storycraft.data.local.data_store.AuthTokenManager
import com.wolfo.storycraft.data.local.data_store.AuthTokens
import com.wolfo.storycraft.data.remote.dto.RefreshTokenRequestDto
import com.wolfo.storycraft.data.remote.dto.TokenDto
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST

class TokenAuthenticator(
    private val tokenManager: AuthTokenManager,
    private val retrofitBuilder: Retrofit.Builder,
    private val baseUrl: String
) : Authenticator {

    override fun authenticate(route: Route?, response: Response): Request? {
        println("Authenticator triggered! Response code: ${response.code}")

        // 1. Проверяем refresh token
        val currentRefreshToken = tokenManager.getTokensSync().refreshToken ?: run {
            println("Refresh token is null. Clearing tokens...")
            runBlocking { tokenManager.clearTokens() }
            return null
        }

        // 2. Проверяем, не совпадают ли токены
//        if (isTokenRefreshed(response)) {
//            println("Token already refreshed. Clearing tokens...")
//            runBlocking { tokenManager.clearTokens() }
//            return null
//        }

        // 3. Пытаемся обновить токен
        val newTokens = runBlocking {
            try {
                println("Refreshing token...")
                val service = createRefreshService()
                val refreshResponse = service.refreshToken(RefreshTokenRequestDto(currentRefreshToken))

                when {
                    refreshResponse.isSuccessful -> {
                        refreshResponse.body()?.let { tokenDto ->
                            val tokens = AuthTokens(
                                accessToken = tokenDto.accessToken,
                                refreshToken = tokenDto.refreshToken ?: currentRefreshToken
                            )
                            println("Saving new tokens: $tokens")
                            tokenManager.saveTokens(tokens)
                            tokens
                        }
                    }
                    refreshResponse.code() == 401 -> {
                        tokenManager.clearTokens()
                        return@runBlocking null
                    }
                    else -> return@runBlocking null
                }
            } catch (e: Exception) {
                println("Refresh error: ${e.message}")
                return@runBlocking null
            }
        }

        // 4. Возвращаем обновленный запрос
        return newTokens?.accessToken?.let { token ->
            println("Retrying with new token: $token")
            response.request.newBuilder()
                .header("Authorization", "Bearer $token")
                .build()
        }
    }

    private fun isTokenRefreshed(response: Response): Boolean {
        val previousToken = response.request.header("Authorization")?.substringAfter("Bearer ")
        val currentToken = tokenManager.getTokensSync().accessToken
        println("Previous token: $previousToken, Current token: $currentToken")
        return previousToken == currentToken
    }

    private fun createRefreshService(): RefreshService {
        return retrofitBuilder
            .baseUrl(baseUrl)
            .client(OkHttpClient.Builder().build())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(RefreshService::class.java)
    }

    private interface RefreshService {
        @POST("api/v1/auth/refresh")
        suspend fun refreshToken(@Body request: RefreshTokenRequestDto): retrofit2.Response<TokenDto>
    }
}