package com.wolfo.storycraft.domain.usecase

import com.wolfo.storycraft.domain.model.Story
import com.wolfo.storycraft.domain.repository.StoryRepository

class GetStoryFullUseCase(
    private val storyRepository: StoryRepository
) {
    suspend operator fun invoke(storyId: Long): Story? {
        return storyRepository.getStoryFull(storyId)
    }
}