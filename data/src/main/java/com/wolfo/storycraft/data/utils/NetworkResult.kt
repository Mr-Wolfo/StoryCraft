package com.wolfo.storycraft.data.utils

sealed class NetworkResult<out T : Any> {
    /** Успешный ответ с данными. */
    data class Success<out T : Any>(val data: T) : NetworkResult<T>()
    /** Ошибка от сервера (HTTP статус не 2xx). */
    data class Error(val code: Int, val message: String?) : NetworkResult<Nothing>()
    /** Исключение во время выполнения запроса (сетевое, парсинг и т.д.). */
    data class Exception(val e: Throwable) : NetworkResult<Nothing>()
}
