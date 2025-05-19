package com.wolfo.storycraft.domain.usecase.auth

import com.wolfo.storycraft.domain.repository.AuthRepository

/**
 * Use Case для выхода пользователя.
 */
class LogoutUserUseCase(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke() {
        // Result не нужен, но можно обернуть в Result<Unit>, если нужна обработка ошибок логаута
        try {
            authRepository.logout()
        } catch (e: Exception) {
            // Логирование или обработка ошибки, если репозиторий не возвращает Result
            println("Error during logout: ${e.message}") // Пример логирования
        }
    }
}