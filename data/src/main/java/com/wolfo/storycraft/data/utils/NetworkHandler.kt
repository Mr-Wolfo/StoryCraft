package com.wolfo.storycraft.data.utils

import android.util.Log
import com.wolfo.storycraft.data.constants.DataConstants
import com.wolfo.storycraft.domain.DataError
import com.wolfo.storycraft.domain.ResultM

class NetworkHandler {

    /**
     * Обрабатывает сетевой вызов с преобразованием результата и дополнительными действиями.
     *
     * @param REMOTE_DTO Тип DTO, получаемого из сети
     * @param DOMAIN_MODEL Тип доменной модели для возврата
     *
     * @param call Suspending-функция для выполнения сетевого запроса
     * @param transform Функция преобразования DTO → DOMAIN_MODEL
     * @param onSuccess Дополнительное действие при успешном ответе (например, сохранение в БД)
     * @param onError Обработчик ошибок с возможностью кастомного преобразования
     *
     * @return ResultM<DOMAIN_MODEL> Результат с доменной моделью или ошибкой:
     *         - Success с преобразованными данными при успехе
     *         - Failure с DataError при ошибке сети или обработки
     *
     * Логика обработки:
     * 1. Выполняет сетевой вызов
     * 2. При успехе:
     *    - Вызывает onSuccess (например, для сохранения в БД)
     *    - Преобразует данные через transform
     * 3. При ошибке:
     *    - Конвертирует NetworkResult.Error/Exception в DataError
     *    - Может использовать кастомный обработчик onError
     *
     * Пример:
     * ```
     * val result = handleNetworkCall(
     *     call = { api.getUserData() },
     *     transform = { it.toUserDomain() },
     *     onSuccess = { dto -> dao.insert(dto) }
     * )
     * ```
     */

    suspend fun <REMOTE_DTO : Any, DOMAIN_MODEL> handleNetworkCall(
        call: suspend () -> NetworkResult<REMOTE_DTO>,
        transform: (REMOTE_DTO) -> DOMAIN_MODEL,
        onSuccess: suspend (REMOTE_DTO) -> Unit = {},
        onError: suspend (DataError) -> ResultM<DOMAIN_MODEL> = { error -> ResultM.failure(error) }
    ): ResultM<DOMAIN_MODEL> {
        return when (val result = call()) {
            is NetworkResult.Success -> {
                try {
                    onSuccess(result.data)
                    ResultM.success(transform(result.data))
                } catch (e: Exception) {
                    ResultM.failure(DataError.Database("Failed to handle successful response data: $e"))
                }
            }
            is NetworkResult.Error -> {
                // Преобразуем сетевую ошибку в DataError
                val error = when (result.code) {
                    401, 403 -> DataError.Authentication(result.message ?: "Unauthorized/Forbidden")
                    404 -> DataError.Network(result.code, result.message ?: "Not Found")
                    422 -> DataError.Network(result.code, result.message ?: "Validation Error")
                    else -> DataError.Network(result.code, result.message)
                }
                ResultM.failure(error)
            }
            is NetworkResult.Exception -> {
                // Преобразуем исключение в DataError
                val error = if (result.e is java.io.IOException) {
                    DataError.Network(0, "Network connection error: ${result.e.message}")
                } else {
                    DataError.Unknown(result.e.message ?: "Unknown error", result.e)
                }
                ResultM.failure(error)
            }
        }
    }

    /**
     * Проверяет необходимость обновления данных на основе времени последнего обновления.
     *
     * @param currentTime Текущее время (мс), по умолчанию System.currentTimeMillis()
     * @param lastRefresh Время последнего обновления (мс)
     *
     * @return Boolean true если данные устарели (превысили CACHE_LIFETIME)
     */

    fun needToRefresh(
        currentTime: Long = System.currentTimeMillis(),
        lastRefresh: Long
    ): Boolean {
        Log.d("NetworkHandler", "${currentTime - lastRefresh > DataConstants.CACHE_LIFETIME}")
        return (currentTime - lastRefresh > DataConstants.CACHE_LIFETIME)
    }
}