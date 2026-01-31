package com.wolfo.storycraft.domain.usecase.story

import com.wolfo.storycraft.domain.ResultM
import com.wolfo.storycraft.domain.model.StoryQuery
import com.wolfo.storycraft.domain.model.story.StoryBaseInfo
import com.wolfo.storycraft.domain.repository.StoryRepository
import kotlinx.coroutines.flow.Flow

/**
 * Use Case для получения потока списка историй.
 */
class GetStoriesStreamUseCase(
    private val storyRepository: StoryRepository
) {
    operator fun invoke(
        forceRefresh: Boolean = false,
        query: StoryQuery = StoryQuery()
    ): Flow<ResultM<List<StoryBaseInfo>>> { // Используем Result из репозитория
        return storyRepository.getStoriesStream(forceRefresh, query)
    }
}