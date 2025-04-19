package com.wolfo.storycraft.domain.usecase

import com.wolfo.storycraft.domain.repository.StoryRepository

class LoadStoryFullByIdUseCase(
    private val storyRepository: StoryRepository
) {
    suspend operator fun invoke(storyId: Long) {
        storyRepository.loadStoryFull(storyId)
    }
}