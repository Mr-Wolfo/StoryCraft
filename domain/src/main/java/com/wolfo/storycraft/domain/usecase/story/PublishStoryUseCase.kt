package com.wolfo.storycraft.domain.usecase.story

import com.wolfo.storycraft.domain.DataError
import com.wolfo.storycraft.domain.ResultM
import com.wolfo.storycraft.domain.model.PublishContent
import com.wolfo.storycraft.domain.model.story.StoryFull
import com.wolfo.storycraft.domain.repository.StoryRepository

/**
 * Use Case для публикации черновика истории.
 * Зависит от StoryRepository и DeleteStoryDraftUseCase.
 * Принимает Domain модель PublishContent.
 * Вызывает репозиторий для публикации, затем удаляет локальный черновик (из PublishContent.id) при успехе.
 * Возвращает ResultM с Domain моделью StoryFull опубликованной истории.
 */
// @Single // Пример Koin annotation
class PublishStoryUseCase(
    private val storyRepository: StoryRepository,
    private val deleteStoryDraftUseCase: DeleteStoryDraftUseCase // Use Case для удаления локального черновика
) {
    // Suspend функция, т.к. это однократное действие
    suspend operator fun invoke(publishContent: PublishContent): ResultM<StoryFull> {

        // 1. Доменная валидация PublishContent
        // Валидация, не зависящая от UI (напр., связность графа)
        val validationErrors = publishContent.validateDomain() // Пример метода валидации в PublishContent
        if (validationErrors.isNotEmpty()) {
            val errorMessage = "Ошибка доменной валидации:\n${validationErrors.joinToString("\n")}"
            return ResultM.failure(DataError.Validation.UI(errorMessage)) // Возвращаем ошибку валидации (используй правильный класс ошибки)
        }

        // 2. Вызов метода публикации в StoryRepository
        // Репозиторий принимает Domain Model PublishContent, сам маппит в сетевой DTO и отправляет.
        val publishResult = storyRepository.publishStory(
            publishContent = publishContent // Передаем Domain модель
        )

        // 3. Обработка результата публикации
        return when (publishResult) {
            is ResultM.Success -> {
                // Успех! Удаляем локальный черновик по ID, который был в PublishContent.
                deleteStoryDraftUseCase(publishContent.id) // Вызываем Use Case для удаления по ID черновика
                publishResult // Возвращаем успешный результат публикации (StoryFull Domain Model)
            }
            is ResultM.Failure -> {
                // Ошибка публикации. Возвращаем ошибку. Черновик остается.
                // Репозиторий или RemoteDataSource должны позаботиться об очистке временных файлов File.
                publishResult
            }
            ResultM.Loading -> {
                // Suspend функция не должна возвращать Loading
                throw IllegalStateException("PublishStoryUseCase should not return Loading state")
            }
        }
    }
}