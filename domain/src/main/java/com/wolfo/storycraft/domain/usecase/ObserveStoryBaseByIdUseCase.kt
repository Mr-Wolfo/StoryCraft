package com.wolfo.storycraft.domain.usecase

import com.wolfo.storycraft.domain.model.StoryBase
import com.wolfo.storycraft.domain.repository.StoryRepository
import kotlinx.coroutines.flow.Flow

class ObserveStoryBaseByIdUseCase(
    private val storyRepository: StoryRepository
) {
    suspend operator fun invoke(storyId: Long): Flow<StoryBase> {
        return storyRepository.observeStoryBase(storyId = storyId)
    }
}