package com.wolfo.storycraft.domain.usecase.story

import com.wolfo.storycraft.domain.ResultM
import com.wolfo.storycraft.domain.model.story.StoryBaseInfo
import com.wolfo.storycraft.domain.repository.StoryRepository

/**
 * Use Case для получения базовой информации об истории по id.
 */
class GetStoryBaseUseCase(
    private val storyRepository: StoryRepository
) {
    suspend operator fun invoke(
        storyId: String
    ): ResultM<StoryBaseInfo> { // Используем Result из репозитория
        return storyRepository.getBaseStoryById(storyId)
    }
}