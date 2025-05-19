package com.wolfo.storycraft.domain.usecase.story

import com.wolfo.storycraft.domain.ResultM
import com.wolfo.storycraft.domain.model.Review
import com.wolfo.storycraft.domain.repository.StoryRepository
import kotlinx.coroutines.flow.Flow

/**
 * Use Case для получения потока отзывов для истории.
 */
class GetReviewsStreamUseCase(
    private val storyRepository: StoryRepository
) {
    operator fun invoke(
        storyId: String,
        forceRefresh: Boolean = false
    ): Flow<ResultM<List<Review>>> {
        return storyRepository.getReviewsStream(storyId, forceRefresh)
    }
}