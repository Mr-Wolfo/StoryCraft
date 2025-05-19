package com.wolfo.storycraft.data.remote

import android.util.Log
import com.wolfo.storycraft.data.local.data_store.AuthTokenManager
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(
    private val tokenManager: AuthTokenManager
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        // Получаем текущий запрос
        val originalRequest = chain.request()

        // Получаем токен доступа СИНХРОННО (т.к. интерцептор работает синхронно)
        // Важно: используем runBlocking здесь, т.к. другого простого способа нет.
        // Это нормально для OkHttp Interceptor, он работает в своем потоке.
        val accessToken = tokenManager.getTokensSync().accessToken

        // Строим новый запрос
        val requestBuilder = originalRequest.newBuilder()

        // Добавляем заголовок Authorization, если токен есть и эндпоинт не требует его отсутствия
        if (accessToken != null && !isAuthRequest(originalRequest.url.encodedPath)) {
            requestBuilder.header("Authorization", "Bearer $accessToken")
        }

        Log.d("AuthInterceptor", "Send request")

        val response = chain.proceed(requestBuilder.build())

        Log.d("AuthInterceptor", "Code: ${response.code}")
        // Выполняем модифицированный запрос
        return response
    }

    // Проверяем, является ли запрос к эндпоинтам, не требующим токен
    private fun isAuthRequest(path: String): Boolean {
        // Добавьте сюда пути, которые НЕ должны содержать токен
        return path.contains("/auth/login", ignoreCase = true) ||
                path.contains("/auth/register", ignoreCase = true) ||
                path.contains("/auth/refresh", ignoreCase = true) // Refresh тоже не должен содержать старый Access токен
    }
}