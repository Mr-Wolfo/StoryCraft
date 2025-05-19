package com.wolfo.storycraft.data.local.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.wolfo.storycraft.data.local.db.entity.ReviewEntity
import com.wolfo.storycraft.data.local.db.entity.ReviewWithAuthor
import kotlinx.coroutines.flow.Flow

@Dao
interface ReviewDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateReview(review: ReviewEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateReviews(reviews: List<ReviewEntity>)

    @Transaction
    @Query("SELECT * FROM reviews WHERE story_id = :storyId ORDER BY created_at DESC") // Уточните поле
    fun getReviewsWithAuthorsForStoryStream(storyId: String): Flow<List<ReviewWithAuthor>>

    @Transaction
    @Query("SELECT * FROM reviews WHERE id = :reviewId LIMIT 1")
    suspend fun getReviewWithAuthorById(reviewId: String): ReviewWithAuthor?

    @Query("DELETE FROM reviews WHERE id = :reviewId")
    suspend fun deleteReviewById(reviewId: String)

    @Query("DELETE FROM reviews WHERE story_id = :storyId")
    suspend fun deleteReviewsForStory(storyId: String)

    @Query("DELETE FROM reviews")
    suspend fun clearAllReviews()
}