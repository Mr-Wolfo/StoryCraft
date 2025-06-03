package com.wolfo.storycraft.domain.usecase.story

import com.wolfo.storycraft.domain.ResultM
import com.wolfo.storycraft.domain.model.draft.DraftContent
import com.wolfo.storycraft.domain.repository.StoryRepository

/**
 * Use Case для получения конкретного черновика истории по ID.
 * Зависит от StoryRepository.
 * Возвращает ResultM с Domain моделью DraftContent.
 */
// @Single // Пример Koin annotation
class GetStoryDraftUseCase(
    private val storyRepository: StoryRepository
) {
    // Suspend функция, т.к. это однократное получение данных
    suspend operator fun invoke(draftId: String): ResultM<DraftContent> {
        // Репозиторий получает Entity и маппит в Domain DraftContent.
        return storyRepository.getStoryDraft(draftId)
    }
}