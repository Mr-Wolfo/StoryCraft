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
class PublishStoryUseCase(
    private val storyRepository: StoryRepository,
    private val deleteStoryDraftUseCase: DeleteStoryDraftUseCase // Use Case для удаления локального черновика
) {
    suspend operator fun invoke(publishContent: PublishContent): ResultM<StoryFull> {

        // Доменная валидация PublishContent
        val validationErrors = publishContent.validateDomain()
        if (validationErrors.isNotEmpty()) {
            val errorMessage = "Ошибка доменной валидации:\n${validationErrors.joinToString("\n")}"
            return ResultM.failure(DataError.Validation.UI(errorMessage))
        }

        // Вызов метода публикации в StoryRepository
        // Репозиторий принимает Domain Model PublishContent, сам маппит в сетевой DTO и отправляет.
        val publishResult = storyRepository.publishStory(
            publishContent = publishContent
        )

        // 3. Обработка результата публикации
        return when (publishResult) {
            is ResultM.Success -> {
                deleteStoryDraftUseCase(publishContent.id) // Вызываем Use Case для удаления по ID черновика
                publishResult // Возвращаем успешный результат публикации (StoryFull Domain Model)
            }
            is ResultM.Failure -> {
                // Ошибка публикации. Возвращаем ошибку. Черновик остается.
                publishResult
            }
            ResultM.Loading -> {
                throw IllegalStateException("PublishStoryUseCase should not return Loading state")
            }
        }
    }
}