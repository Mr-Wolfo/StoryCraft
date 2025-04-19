package com.wolfo.storycraft.domain.usecase

import com.wolfo.storycraft.domain.model.Story
import com.wolfo.storycraft.domain.repository.StoryRepository
import kotlinx.coroutines.flow.Flow

class ObserveStoryFullByIdUseCase(
    private val storyRepository: StoryRepository
) {
    suspend operator fun invoke(storyId: Long): Flow<Story> {
        return storyRepository.observeStoryFull(storyId = storyId)
    }
}