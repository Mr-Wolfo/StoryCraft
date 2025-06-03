package com.wolfo.storycraft.domain.usecase.story

import com.wolfo.storycraft.domain.DataError
import com.wolfo.storycraft.domain.ResultM
import com.wolfo.storycraft.domain.model.story.StoryBaseInfo
import com.wolfo.storycraft.domain.repository.StoryRepository
import com.wolfo.storycraft.domain.usecase.user.GetCurrentUserIdUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/**
 * Use Case для получения списка черновиков историй текущего пользователя.
 * Зависит от StoryRepository и GetCurrentUserIdUseCase.
 * Возвращает поток ResultM с Domain моделью списка StoryBaseInfo.
 */
// @Single // Пример Koin annotation
class GetDraftStoriesUseCase(
    private val storyRepository: StoryRepository,
    private val getCurrentUserIdUseCase: GetCurrentUserIdUseCase
) {
    // Оператор invoke позволяет вызывать Use Case как функцию: getDraftStoriesUseCase()
    operator fun invoke(): Flow<ResultM<List<StoryBaseInfo>>> {
        return flow {
            emit(ResultM.Loading)
            val userId = getCurrentUserIdUseCase()
            if (userId == null) {
                emit(ResultM.failure(DataError.Authentication())) // Используй правильный класс ошибки
                return@flow
            }
            // Репозиторий возвращает Flow<ResultM<List<StoryBaseInfo>>> (Domain model)
            storyRepository.getDraftStoriesForUser(userId).collect { emit(it) }
        }
    }
}
