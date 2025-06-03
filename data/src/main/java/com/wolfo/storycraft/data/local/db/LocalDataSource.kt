package com.wolfo.storycraft.data.local.db

import com.wolfo.storycraft.data.local.db.dao.StoryBaseInfoDraft
import com.wolfo.storycraft.data.local.db.entity.ChoiceDraftEntity
import com.wolfo.storycraft.data.local.db.entity.ChoiceEntity
import com.wolfo.storycraft.data.local.db.entity.PageDraftEntity
import com.wolfo.storycraft.data.local.db.entity.PageEntity
import com.wolfo.storycraft.data.local.db.entity.PageWithChoices
import com.wolfo.storycraft.data.local.db.entity.ReviewEntity
import com.wolfo.storycraft.data.local.db.entity.ReviewWithAuthor
import com.wolfo.storycraft.data.local.db.entity.StoryDraftEntity
import com.wolfo.storycraft.data.local.db.entity.StoryDraftWithPagesAndChoices
import com.wolfo.storycraft.data.local.db.entity.StoryEntity
import com.wolfo.storycraft.data.local.db.entity.StoryWithAuthorAndTags
import com.wolfo.storycraft.data.local.db.entity.TagEntity
import com.wolfo.storycraft.data.local.db.entity.UserEntity
import kotlinx.coroutines.flow.Flow

interface LocalDataSource {

    // --- Транзакция ---
    suspend fun <R> runInTransaction(block: suspend () -> R): R

    // --- User / Author ---
    suspend fun saveUser(user: UserEntity)
    suspend fun saveUsers(users: List<UserEntity>) // Для сохранения авторов списком
    suspend fun getUser(userId: String): UserEntity?
    fun getUserFlow(userId: String): Flow<UserEntity?>
    suspend fun clearUsers()

    // --- Tag ---
    suspend fun saveTags(tags: List<TagEntity>)
    suspend fun getTagsByIds(tagIds: List<String>): List<TagEntity>

    // --- Story ---
    suspend fun saveStoriesWithDetails(stories: List<StoryWithAuthorAndTags>) // Сохранение списка историй
    suspend fun saveFullStory(
        story: StoryEntity,
        author: UserEntity,
        tags: List<TagEntity>,
        pages: List<PageEntity>,
        choices: List<ChoiceEntity>
    ) // Сохранение полной истории
    fun getStoriesStream(): Flow<List<StoryWithAuthorAndTags>>
    suspend fun getStoryWithAuthorAndTags(storyId: String): StoryWithAuthorAndTags?
    suspend fun getPagesWithChoices(storyId: String): List<PageWithChoices>
    suspend fun deleteStory(storyId: String)
    suspend fun clearStories() // Удаляет истории, страницы, выборы, отзывы, связи

    // --- Draft Stories ---
    // Возвращает Projection Entity для списка
    suspend fun getStoryBaseInfoDraftsForUser(userId: String): List<StoryBaseInfoDraft>
    // Возвращает Entity с отношениями для деталей
    suspend fun getStoryDraftWithDetails(draftId: String): StoryDraftWithPagesAndChoices?
    // Принимает Entity для сохранения
    suspend fun saveFullStoryDraft(
        story: StoryDraftEntity,
        pages: List<PageDraftEntity>,
        choices: List<ChoiceDraftEntity>
    )
    suspend fun deleteStoryDraftById(draftId: String) // Удаление черновика


    // --- Review ---
    suspend fun saveReviewWithAuthor(review: ReviewEntity, author: UserEntity)
    suspend fun saveReviewsWithAuthors(reviews: List<ReviewWithAuthor>) // Принимает ReviewWithAuthor для удобства
    fun getReviewsForStoryStream(storyId: String): Flow<List<ReviewWithAuthor>>
    suspend fun getReviewById(reviewId: String): ReviewWithAuthor?
    suspend fun deleteReview(reviewId: String)
    suspend fun clearReviewsForStory(storyId: String)
    suspend fun clearAllReviews()
}
