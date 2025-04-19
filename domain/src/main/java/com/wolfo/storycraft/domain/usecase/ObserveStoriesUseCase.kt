package com.wolfo.storycraft.domain.usecase

import com.wolfo.storycraft.domain.model.StoryBase
import com.wolfo.storycraft.domain.repository.StoryRepository
import kotlinx.coroutines.flow.Flow

class ObserveStoriesUseCase(private val storyRepository: StoryRepository) {

    suspend operator fun invoke(): Flow<List<StoryBase>> {
        return storyRepository.observeStoryList()
    }
}