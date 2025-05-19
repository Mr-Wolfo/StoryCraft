package com.wolfo.storycraft.domain.usecase.story

import com.wolfo.storycraft.domain.ResultM
import com.wolfo.storycraft.domain.repository.StoryRepository

/**
 * Use Case для удаления отзыва.
 */
class DeleteReviewUseCase(
    private val storyRepository: StoryRepository
) {
    suspend operator fun invoke(reviewId: String): ResultM<Unit> { // Используем Result из репозитория
        return storyRepository.deleteReview(reviewId)
    }
}