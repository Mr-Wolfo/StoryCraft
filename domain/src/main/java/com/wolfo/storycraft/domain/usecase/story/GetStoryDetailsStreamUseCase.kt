package com.wolfo.storycraft.domain.usecase.story

import com.wolfo.storycraft.domain.ResultM
import com.wolfo.storycraft.domain.model.StoryFull
import com.wolfo.storycraft.domain.repository.StoryRepository
import kotlinx.coroutines.flow.Flow

/**
 * Use Case для получения потока деталей истории.
 */
class GetStoryDetailsStreamUseCase(
    private val storyRepository: StoryRepository
) {
    operator fun invoke(
        storyId: String,
        forceRefresh: Boolean = false
    ): Flow<ResultM<StoryFull>> { // Используем Result из репозитория
        return storyRepository.getStoryDetailsStream(storyId, forceRefresh)
    }
}