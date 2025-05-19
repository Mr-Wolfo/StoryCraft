package com.wolfo.storycraft.data.utils

import com.wolfo.storycraft.domain.DataError
import java.io.IOException

/**
 * Обертка для ответа API, если ваш бэкенд всегда возвращает стандартную структуру
 * (например, { "success": true, "data": {...}, "message": null }).
 * Если API просто возвращает нужные данные (DTO) или их список напрямую при успехе (2xx),
 * и сообщение об ошибке в теле при 4xx/5xx, то этот класс НЕ НУЖЕН.
 * Судя по вашему OpenAPI, он не используется повсеместно, API возвращает DTO напрямую.
 */
// data class BaseApiResponse<T>(
//     val success: Boolean,
//     val data: T?,
//     val message: String?,
//     val errorCode: Int?
// )

/**
 * Безопасно выполняет suspend-функцию Retrofit и оборачивает результат в NetworkResult.
 *
 * @param T Тип ожидаемых данных в теле успешного ответа.
 * @param apiCall Suspend-лямбда, выполняющая сетевой вызов Retrofit.
 * @return NetworkResult<T> с результатом вызова.
 */
suspend fun <T : Any> safeApiCall(
    apiCall: suspend () -> retrofit2.Response<T>
): NetworkResult<T> {
    return try {
        val response = apiCall()
        if (response.isSuccessful) {
            val body = response.body()
            if (body != null) {
                NetworkResult.Success(body)
            } else {
                // Успешный ответ, но тело пустое (например, 204 No Content)
                // Обрабатываем как ошибку, если ожидалось тело
                if (response.code() == 204) {
                    // Можно создать специальный тип Success для No Content, если это важно
                    // Пока что считаем это ошибкой, если ожидали тело T
                    NetworkResult.Error(response.code(), "Response body is null (No Content)")
                } else {
                    NetworkResult.Error(response.code(), "Response body is null")
                }
            }
        } else {
            // Ошибка сервера (4xx, 5xx)
            val errorBody = response.errorBody()?.string() ?: "Unknown error body"
            // Здесь можно парсить errorBody, если бэкенд возвращает структурированную ошибку (напр., HTTPValidationError)
            NetworkResult.Error(response.code(), errorBody)
        }
    } catch (e: IOException) {
        // Ошибка сети (нет подключения, таймаут и т.д.)
        NetworkResult.Exception(e)
    } catch (e: Exception) { // Ловим все остальные исключения (напр., kotlinx.serialization, Gson)
        // Другие ошибки (парсинг JSON, непредвиденные исключения)
        NetworkResult.Exception(e)
    }
}

// Версия для вызовов, которые не возвращают тело при успехе (например, DELETE с ответом 204)
suspend fun safeEmptyApiCall(
    apiCall: suspend () -> retrofit2.Response<Unit> // Ожидаем Unit или Void
): NetworkResult<Unit> {
    return try {
        val response = apiCall()
        if (response.isSuccessful) {
            NetworkResult.Success(Unit) // Успех без тела
        } else {
            val errorBody = response.errorBody()?.string() ?: "Unknown error body"
            NetworkResult.Error(response.code(), errorBody)
        }
    } catch (e: IOException) {
        NetworkResult.Exception(e)
    } catch (e: Exception) {
        NetworkResult.Exception(e)
    }
}

fun <T : Any> NetworkResult<T>.toDataResult(): Result<T> {
    return when (this) {
        is NetworkResult.Success -> Result.success(this.data)
        is NetworkResult.Error -> {
            val error = when (this.code) {
                401, 403 -> DataError.Authentication(this.message) // Коды ошибок авторизации/аутентификации
                else -> DataError.Network(this.code, this.message) // Другие сетевые ошибки
            }
            Result.failure(error)
        }
        is NetworkResult.Exception -> {
            // Можно добавить более специфичную обработку IOException
            if (this.e is IOException) {
                Result.failure(DataError.Network(0, "Network connection error: ${this.e.message}"))
            } else {
                Result.failure(DataError.Unknown(this.e.message, this.e))
            }
        }
    }
}

val <T> Result<T>.dataError: DataError?
    get() = exceptionOrNull() as? DataError // Безопасное приведение к DataError