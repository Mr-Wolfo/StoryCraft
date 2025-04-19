package com.wolfo.storycraft.domain.usecase

import com.wolfo.storycraft.domain.repository.StoryRepository

class RefreshStoriesUseCase(
    private val storyRepository: StoryRepository
) {
    suspend operator fun invoke() {
        storyRepository.refreshStoryList()
    }
}