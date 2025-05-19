package com.wolfo.storycraft.domain.usecase.story

import com.wolfo.storycraft.domain.ResultM
import com.wolfo.storycraft.domain.repository.StoryRepository

/**
 * Use Case для удаления истории.
 */
class DeleteStoryUseCase(
    private val storyRepository: StoryRepository
) {
    suspend operator fun invoke(storyId: String): ResultM<Unit> { // Используем Result из репозитория
        return storyRepository.deleteStory(storyId)
    }
}