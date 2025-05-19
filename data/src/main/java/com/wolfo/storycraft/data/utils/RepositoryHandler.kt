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
        checkOrErrorBefore().also { error ->
            error?.let {
                logError("CheckBefore FAIL: ${it.error}")
                emit(it)
                return@flow
            }
        }

        logDebug("CheckBefore SUCCESS")

        val cachedData = localDataCall().first()
        val transformCachedData = cachedData?.let(localTransform)

        if (firstEmit) {
            emit(
                transformCachedData?.let { ResultM.success(it) }
                    ?: ResultM.failure(dataNullError)
            )
        }

        logDebug("First Emit Success")

        var networkError: DataError? = null
        var freshData: NETWORK_MODEL? = null

        if (needToRefresh(cachedData)) {
            emit(ResultM.loading())
            when (val fetchResult = networkResult()) {
                is ResultM.Failure -> {
                    logError("Refresh Failure")
                    networkError = fetchResult.error
                    emit(ResultM.failure(fetchResult.error, transformCachedData))
                }
                is ResultM.Success -> {
                    logDebug("Refresh Success")
                    freshData = fetchResult.data
                }
                is ResultM.Loading -> {
                    emit(ResultM.loading())
                }
            }
        }

        localDataCall().collect { data ->
            checkOrErrorAfter(data, freshData)?.let { error ->
                logError("CheckAfter FAIL")
                emit(error)
                return@collect
            }

            val currentDomainData = data?.let(localTransform) ?: run {
                emit(ResultM.failure(dataNullError))
                return@collect
            }

            logDebug("CheckAfter SUCCESS")

            emit(
                when {
                    networkError != null -> ResultM.failure(networkError, currentDomainData)
                    freshData != null -> ResultM.success(sourcesDataMergeTransform(currentDomainData, freshData))
                    else -> ResultM.success(currentDomainData)
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