package com.wolfo.storycraft.data.utils

import android.util.Log
import com.wolfo.storycraft.domain.DataError
import com.wolfo.storycraft.domain.ResultM
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow

class RepositoryHandler {

    /**
     * Универсальный обработчик репозитория для работы с локальными и сетевыми данными.
     * Реализует стратегию "сначала кэш, потом сеть" с возможностью гибкой настройки.
     *
     * @param ENTITY Тип локальных данных (например, Entity Room)
     * @param DOMAIN_MODEL Тип доменной модели, возвращаемый наружу
     * @param NETWORK_MODEL Тип модели из сети
     *
     * @param localDataCall Suspending-функция, возвращающая Flow с локальными данными
     * @param networkResult Suspending-функция для получения сетевых данных
     * @param localTransform Функция преобразования ENTITY → DOMAIN_MODEL
     * @param sourcesDataMergeTransform Функция слияния локальных и сетевых данных (по умолчанию отдает локальные)
     * @param firstEmit Нужно ли сразу эмитить данные из кэша (default = true)
     * @param needToRefresh Функция-предикат для проверки необходимости обновления
     * @param checkOrErrorBefore Проверка условий перед выполнением (если вернет Failure - поток завершится)
     * @param checkOrErrorAfter Проверка условий после получения данных
     * @param dataNullError Кастомная ошибка для случая null-данных
     *
     * @return Flow<ResultM<DOMAIN_MODEL>> Поток данных с состояниями:
     *         1. При firstEmit=true: сразу данные из кэша (Success или Failure если null)
     *         2. При необходимости обновления: Loading состояние
     *         3. Результат сети (Success/Failure с кэшированными данными)
     *         4. Объединенные данные после успешного обновления
     *
     * Логика работы:
     * 1. Проверяет условия checkOrErrorBefore()
     * 2. Получает данные из кэша и эмитит их (если firstEmit=true)
     * 3. При необходимости обновляет данные из сети
     * 4. Объединяет результаты с помощью sourcesDataMergeTransform
     * 5. В случае ошибок возвращает последние валидные данные (если есть)
     *
     * Пример использования:
     * ```
     * repositoryHandler.getData(
     *     localDataCall = { localDataSource.getDataFlow() },
     *     networkResult = { remoteDataSource.fetchData() },
     *     localTransform = { it.toDomainModel() },
     *     sourcesDataMergeTransform = { cached, fresh -> fresh.toDomainModel() }
     * ).collect { ... }
     * ```
     */

    fun <ENTITY : Any?, DOMAIN_MODEL : Any, NETWORK_MODEL : Any> getData(
        localDataCall: suspend () -> Flow<ENTITY>,
        networkResult: suspend () -> ResultM<NETWORK_MODEL>,
        localTransform: (ENTITY) -> DOMAIN_MODEL,
        sourcesDataMergeTransform: (DOMAIN_MODEL, NETWORK_MODEL) -> DOMAIN_MODEL = { domain, _ -> domain },
        firstEmit: Boolean = true,
        needToRefresh: (ENTITY) -> Boolean = { true },
        checkOrErrorBefore: () -> ResultM.Failure? = { null },
        checkOrErrorAfter: (ENTITY?, NETWORK_MODEL?) -> ResultM.Failure? = { _, _ -> null },
        dataNullError: DataError = DataError.Unknown(errorMessage = "Empty data error")
    ): Flow<ResultM<DOMAIN_MODEL>> = flow {
        logDebug("Process Started")

        // 1. Проверка ошибок
        checkOrErrorBefore()?.let { error ->
            logError("CheckBefore FAIL: ${error.error}")
            emit(error)
            return@flow
        }
        logDebug("CheckBefore SUCCESS")

        // 2. Первый эмит только если есть данные
        val cachedData = localDataCall().first()
        if (firstEmit && cachedData != null) {
            localTransform(cachedData).let {
                emit(ResultM.success(it))
            }
        }

        // 3. Загрузка из сети только если нужно
        var networkError: DataError? = null
        var freshData: NETWORK_MODEL? = null

        if (needToRefresh(cachedData)) {
            emit(ResultM.loading())
            when (val fetchResult = networkResult()) {
                is ResultM.Success -> {
                    freshData = fetchResult.data
                    logDebug("Refresh Success")
                }
                is ResultM.Failure -> {
                    networkError = fetchResult.error
                    logError("Refresh Failure")
                    // Не эмитим ошибку здесь, ждем актуальных данных из БД
                }
                ResultM.Loading -> emit(ResultM.loading())
            }
        }

        // 4. Основной поток данных из БД
        localDataCall().collect { data ->
            val currentDomain = data?.let(localTransform) ?: run {
                emit(ResultM.failure(dataNullError))
                return@collect
            }

            checkOrErrorAfter(data, freshData)?.let { error ->
                emit(error)
                return@collect
            }

            emit(
                when {
                    freshData != null -> ResultM.success(
                        sourcesDataMergeTransform(currentDomain, freshData)
                    )
                    networkError != null -> ResultM.failure(
                        error = networkError,
                        cachedData = currentDomain // Всегда передаем актуальные данные
                    )
                    else -> ResultM.success(currentDomain)
                }
            )
        }
    }.catch { e ->
        emit(
            ResultM.failure(
                error = DataError.Database("Error reading cache: ${e.message}"),
                cachedData = null
            )
        )
    }

    private fun logDebug(message: String) {
        Log.d("RepoHandler", message)
    }

    private fun logError(message: String) {
        Log.e("RepoHandler", message)
    }
}