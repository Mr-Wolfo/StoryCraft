package com.wolfo.storycraft.domain.usecase.story

import com.wolfo.storycraft.domain.ResultM
import com.wolfo.storycraft.domain.model.Review
import com.wolfo.storycraft.domain.repository.StoryRepository

/**
 * Use Case для создания отзыва.
 */
class CreateReviewUseCase(
    private val storyRepository: StoryRepository
) {
    suspend operator fun invoke(storyId: String, rating: Int, comment: String?): ResultM<Review> { // Используем Result из репозитория
        return storyRepository.createReview(storyId, rating, comment)
    }
}