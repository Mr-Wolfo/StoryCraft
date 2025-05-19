package com.wolfo.storycraft.domain.usecase.story

import com.wolfo.storycraft.domain.ResultM
import com.wolfo.storycraft.domain.model.Review
import com.wolfo.storycraft.domain.repository.StoryRepository

/**
 * Use Case для обновления отзыва.
 */
class UpdateReviewUseCase(
    private val storyRepository: StoryRepository
) {
    suspend operator fun invoke(reviewId: String, rating: Int?, comment: String?): ResultM<Review> { // Используем Result из репозитория
        return storyRepository.updateReview(reviewId, rating, comment)
    }
}