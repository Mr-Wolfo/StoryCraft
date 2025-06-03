package com.wolfo.storycraft.domain

import java.io.Serializable

sealed class DataError(override val message: String?) : Throwable(message), Serializable {

    /** Ошибка сети или HTTP-ошибка от сервера. */
    data class Network(val code: Int, val errorMessage: String?) : DataError("Network error ($code): $errorMessage"), Serializable {
        // data class API_ERROR(val details: Any? = null): Network(code, message) // Для структурных ошибок от API
    }

    /** Ошибка при работе с локальной базой данных Room. */
    data class Database(val errorMessage: String?) : DataError("Database error: $errorMessage"), Serializable {
        // data object NOT_FOUND : Database("Item not found in database") // Более специфичные ошибки БД
        // data object DUPLICATE_ENTRY : Database("Duplicate entry in database")
    }

    /** Ошибка, связанная с аутентификацией или авторизацией (например, неверный токен, 401, 403). */
    data class Authentication(val errorMessage: String? = "Authentication failed") : DataError(errorMessage), Serializable {
        // data object UNAUTHENTICATED : Authentication("User is not authenticated") // Пользователь не вошел
        // data class INVALID_CREDENTIALS(val message: String? = null) : Authentication(message) // Ошибка логина/пароля
        // data class SESSION_EXPIRED(val message: String? = null) : Authentication(message) // Токен просрочен
    }

    /** Ошибка валидации данных. */
    // Делаем Validation sealed class, чтобы можно было добавить подтипы (UI vs Domain)
    sealed class Validation(message: String?) : DataError(message), Serializable {
        data class UI(val validationMessage: String) : Validation(validationMessage) // UI-специфичная валидация (пустые поля и т.п.)
        data class Domain(val validationMessage: String) : Validation(validationMessage) // Domain-специфичная валидация (связность, логика)
        data class GENERAL(val validationMessage: String? = "Validation failed") : Validation(validationMessage) // Общая валидация (если не хотим различать)
    }


    /** Локальная ошибка, не связанная с БД, Сетью, Аутентификацией или Валидацией. */
    sealed class Local(message: String?) : DataError(message), Serializable {
        data class FILE_ERROR(val errorMessage: String? = "File processing error", val throwable: Throwable? = null) : Local(errorMessage), Serializable // Ошибки при работе с файлами (изображениями)
        data class RESOURCE_NOT_FOUND(val errorMessage: String? = "Resource not found") : Local(errorMessage), Serializable // Например, Uri не найден ContentResolver-ом
        data object UNKNOWN : Local("Unknown local error"), Serializable // Неизвестная локальная ошибка
        // Добавьте другие типы локальных ошибок по мере необходимости
    }

    /** Неизвестная или непредвиденная ошибка. */
    data class Unknown(val errorMessage: String? = "An unknown error occurred", val causeError: Throwable? = null) : DataError(errorMessage), Serializable
}

// TODO Зарефакторить DataError на более детальное разделение типов ошибок

//sealed interface DataError : java.io.Serializable {
//    sealed interface Network : DataError {
//        data class BAD_REQUEST(val code: Int, val message: String? = null) : Network
//        data class UNAUTHORIZED(val code: Int, val message: String? = null) : Network // 401
//        data class FORBIDDEN(val code: Int, val message: String? = null) : Network // 403
//        data class NOT_FOUND(val code: Int, val message: String? = null) : Network // 404
//        data class CONFLICT(val code: Int, val message: String? = null) : Network // 409
//        data class PAYLOAD_TOO_LARGE(val code: Int, val message: String? = null) : Network // 413
//        data class UNSUPPORTED_MEDIA_TYPE(val code: Int, val message: String? = null) : Network // 415
//        data class TOO_MANY_REQUESTS(val code: Int, val message: String? = null) : Network // 429
//        data class SERVER_ERROR(val code: Int, val message: String? = null) : Network // 5xx
//        data class CONNECTION_ERROR(val message: String? = null) : Network
//        data class UNKNOWN(val code: Int, val message: String? = null) : Network // Для других кодов
//        data class SERIALIZATION_ERROR(val message: String? = null) : Network // Ошибки парсинга
//        // Можно добавить Specific API errors based on response body (e.g., ValidationError)
//        data class API_ERROR(val code: Int, val message: String?, val details: Any? = null): Network // Для структурных ошибок от API
//    }
//
//    sealed interface Authentication : DataError {
//        data object UNAUTHENTICATED : Authentication // Пользователь не вошел
//        data class INVALID_CREDENTIALS(val message: String? = null) : Authentication // Ошибка логина/пароля
//        data class SESSION_EXPIRED(val message: String? = null) : Authentication // Токен просрочен и не обновился
//        data object REGISTRATION_FAILED : Authentication // Ошибка регистрации
//        data class AUTH_ERROR(val message: String? = null) : Authentication // Прочие ошибки аутентификации
//    }
//
//    sealed interface Database : DataError {
//        data class DATABASE_ERROR(val throwable: Throwable? = null) : Database // Общие ошибки БД
//        data object NOT_FOUND : Database // Запись не найдена
//        data object DUPLICATE_ENTRY : Database // Дубликат при вставке
//        data object CONSTRAIN_ERROR : Database // Ошибка ограничений
//    }
//
//    sealed interface Local : DataError {
//        data class VALIDATION_ERROR(val message: String) : Local // Ошибки валидации на клиенте
//        data class FILE_ERROR(val message: String? = null, val throwable: Throwable? = null) : Local // Ошибки при работе с файлами (изображениями)
//        data object UNKNOWN : Local // Неизвестная локальная ошибка
//        // Добавьте другие типы локальных ошибок по мере необходимости
//    }
//
//    data class Unknown(val message: String? = null, val throwable: Throwable? = null) : DataError // catch(e: Exception) -> DataError.Unknown(e)
//}
