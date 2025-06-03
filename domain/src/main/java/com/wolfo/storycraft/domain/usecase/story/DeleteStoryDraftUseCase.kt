package com.wolfo.storycraft.domain.usecase.story

import com.wolfo.storycraft.domain.ResultM
import com.wolfo.storycraft.domain.repository.StoryRepository

/**
 * Use Case для удаления черновика истории.
 * Зависит от StoryRepository.
 */
// @Single // Пример Koin annotation
class DeleteStoryDraftUseCase(
    private val storyRepository: StoryRepository
) {
    // Suspend функция, т.к. это однократное удаление
    suspend operator fun invoke(draftId: String): ResultM<Unit> {
        return storyRepository.deleteStoryDraft(draftId)
    }
}