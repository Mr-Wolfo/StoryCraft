package com.wolfo.storycraft.domain

/**
 * Обертка для возвращаемых значений операций, которая может содержать:
 * - Успешные данные (Success)
 * - Ошибку с опциональными кэшированными данными (Failure)
 * - Состояние загрузки (Loading)
 *
 * @param T Тип успешных данных
 */
sealed class ResultM<out T> {
    data class Success<out T>(val data: T) : ResultM<T>()
    data class Failure(val error: DataError, val cachedData: Any? = null) : ResultM<Nothing>()
    data object Loading : ResultM<Nothing>()

    companion object {
        fun <T> success(data: T): ResultM<T> = Success(data)
        fun <T> failure(error: DataError, cachedData: Any? = null): ResultM<T> = Failure(error, cachedData)
        fun <T> loading(): ResultM<T> = Loading as ResultM<T>
    }
}
