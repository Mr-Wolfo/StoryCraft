package com.wolfo.storycraft.domain

sealed class DataError(override val message: String?) : Throwable(message) {

    /** Ошибка сети или HTTP-ошибка от сервера. */
    data class Network(val code: Int, val errorMessage: String?) : DataError("Network error ($code): $errorMessage")

    /** Ошибка при работе с локальной базой данных Room. */
    data class Database(val errorMessage: String?) : DataError("Database error: $errorMessage")

    /** Ошибка, связанная с аутентификацией или авторизацией (например, неверный токен, 401, 403). */
    data class Authentication(val errorMessage: String? = "Authentication failed") : DataError(errorMessage)

    /** Ошибка валидации данных. */
    data class Validation(val errorMessage: String? = "Validation failed") : DataError(errorMessage)

    /** Неизвестная или непредвиденная ошибка. */
    data class Unknown(val errorMessage: String?, val causeError: Throwable? = null) : DataError("Unknown error: $errorMessage")
}