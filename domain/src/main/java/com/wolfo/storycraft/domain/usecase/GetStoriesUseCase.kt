package com.wolfo.storycraft.domain.usecase

import com.wolfo.storycraft.domain.model.Story
import com.wolfo.storycraft.domain.model.StoryBase
import com.wolfo.storycraft.domain.repository.StoryRepository
import kotlinx.coroutines.flow.Flow

class GetStoriesUseCase(private val storyRepository: StoryRepository) {

    suspend operator fun invoke(): Flow<List<StoryBase>> {
        return storyRepository.getStories()
    }
}