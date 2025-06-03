package com.wolfo.storycraft.domain.usecase.story

import com.wolfo.storycraft.domain.ResultM
import com.wolfo.storycraft.domain.model.draft.DraftContent
import com.wolfo.storycraft.domain.repository.StoryRepository

/**
 * Use Case для сохранения черновика истории.
 * Зависит от StoryRepository.
 * Принимает Domain модель DraftContent.
 */
// @Single // Пример Koin annotation
class SaveStoryDraftUseCase(
    private val storyRepository: StoryRepository
    // userId должен быть уже в DraftContent, который создан в ViewModel.
) {
    // Suspend функция, т.к. это однократное сохранение
    suspend operator fun invoke(draftContent: DraftContent): ResultM<Unit> {
        // Доменная валидация DraftContent, если нужна логика не связанная с UI формой.
        // Например, проверка связей между страницами на уровне домена.
        // if (!draftContent.isValidDomain()) { return ResultM.failure(...) }

        // Репозиторий принимает Domain Model DraftContent, маппит в Draft Entities и сохраняет.
        return storyRepository.saveStoryDraft(draftContent)
    }
}