package com.wolfo.storycraft.data.local.db

import com.wolfo.storycraft.data.local.db.dao.ChoiceDao
import com.wolfo.storycraft.data.local.db.dao.PageDao
import com.wolfo.storycraft.data.local.db.dao.ReviewDao
import com.wolfo.storycraft.data.local.db.dao.StoryBaseInfoDraft
import com.wolfo.storycraft.data.local.db.dao.StoryDao
import com.wolfo.storycraft.data.local.db.dao.StoryDraftDao
import com.wolfo.storycraft.data.local.db.dao.StoryTagCrossRefDao
import com.wolfo.storycraft.data.local.db.dao.TagDao
import com.wolfo.storycraft.data.local.db.dao.UserDao
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
import com.wolfo.storycraft.data.local.db.entity.StoryTagCrossRef
import com.wolfo.storycraft.data.local.db.entity.StoryWithAuthorAndTags
import com.wolfo.storycraft.data.local.db.entity.TagEntity
import com.wolfo.storycraft.data.local.db.entity.UserEntity
import kotlinx.coroutines.flow.Flow
import kotlin.collections.forEach

class LocalDataSourceImpl(
    private val database: StoryAppDatabase, // Инжектируем БД для транзакций
    private val userDao: UserDao,
    private val tagDao: TagDao,
    private val storyDao: StoryDao,
    private val storyDraftDao: StoryDraftDao,
    private val pageDao: PageDao,
    private val choiceDao: ChoiceDao,
    private val reviewDao: ReviewDao,
    private val storyTagCrossRefDao: StoryTagCrossRefDao // Или используем методы в StoryDao
) : LocalDataSource {

    override suspend fun <R> runInTransaction(block: suspend () -> R): R {
        return database.runInTransaction { block() }
    }

    // --- User / Author ---
    override suspend fun saveUser(user: UserEntity) = userDao.insertOrUpdateUser(user)
    override suspend fun saveUsers(users: List<UserEntity>) = userDao.insertOrUpdateUsers(users)
    override suspend fun getUser(userId: String): UserEntity? = userDao.getUserById(userId)
    override fun getUserFlow(userId: String): Flow<UserEntity?> = userDao.getUserByIdFlow(userId)
    override suspend fun clearUsers() = userDao.clearAllUsers()

    // --- Tag ---
    override suspend fun saveTags(tags: List<TagEntity>) = tagDao.insertTags(tags)
    override suspend fun getTagsByIds(tagIds: List<String>): List<TagEntity> = tagDao.getTagsByIds(tagIds)


    // --- Story ---
    override suspend fun saveStoriesWithDetails(stories: List<StoryWithAuthorAndTags>) {
        runInTransaction {
            storyDao.clearAllStories()

            // 1. Сохраняем авторов (они могут дублироваться, OnConflictStrategy.REPLACE справится)
            val authors = stories.mapNotNull { it.author }
            if (authors.isNotEmpty()) {
                userDao.insertOrUpdateUsers(authors)
            }
            // 2. Сохраняем теги (OnConflictStrategy.IGNORE справится с дубликатами)
            val tags = stories.flatMap { it.tags }.distinctBy { it.id }
            if (tags.isNotEmpty()) {
                tagDao.insertTags(tags)
            }
            // 3. Сохраняем истории
            val storyEntities = stories.map { it.story }
            storyDao.insertOrUpdateStories(storyEntities)
            // 4. Сохраняем связи Story-Tag
            stories.forEach { storyRelation ->
                // Сначала удаляем старые связи для этой истории
                storyTagCrossRefDao.deleteCrossRefsForStory(storyRelation.story.id)
                // Затем вставляем новые
                val crossRefs = storyRelation.tags.map { tag ->
                    StoryTagCrossRef(storyId = storyRelation.story.id, tagId = tag.id)
                }
                if (crossRefs.isNotEmpty()) {
                    storyTagCrossRefDao.insertCrossRefs(crossRefs)
                }
            }
        }
    }

    override suspend fun saveFullStory(
        story: StoryEntity,
        author: UserEntity,
        tags: List<TagEntity>,
        pages: List<PageEntity>,
        choices: List<ChoiceEntity>
    ) {
        runInTransaction {
            // 1. Сохраняем автора
            userDao.insertOrUpdateUser(author)
            // 2. Сохраняем теги
            if (tags.isNotEmpty()) {
                tagDao.insertTags(tags)
            }
            // 3. Сохраняем историю
            storyDao.insertOrUpdateStory(story)
            // 4. Удаляем старые и сохраняем новые связи Story-Tag
            storyTagCrossRefDao.deleteCrossRefsForStory(story.id)
            val crossRefs = tags.map { tag -> StoryTagCrossRef(storyId = story.id, tagId = tag.id) }
            if (crossRefs.isNotEmpty()) {
                storyTagCrossRefDao.insertCrossRefs(crossRefs)
            }
            // 5. Удаляем старые страницы и выборы (каскадно)
            pageDao.deletePagesForStory(story.id)
            // 6. Сохраняем новые страницы
            if (pages.isNotEmpty()) {
                pageDao.insertOrUpdatePages(pages)
            }
            // 7. Сохраняем новые выборы
            if (choices.isNotEmpty()) {
                choiceDao.insertOrUpdateChoices(choices)
            }
        }
    }


    override fun getStoriesStream(): Flow<List<StoryWithAuthorAndTags>> = storyDao.getStoriesWithAuthorAndTagsStream()

    override suspend fun getStoryWithAuthorAndTags(storyId: String): StoryWithAuthorAndTags? =
        storyDao.getStoryWithAuthorAndTagsById(storyId)

    override suspend fun getPagesWithChoices(storyId: String): List<PageWithChoices> =
        pageDao.getPagesWithChoicesForStory(storyId)

    override suspend fun deleteStory(storyId: String) = storyDao.deleteStoryById(storyId) // Каскадно удалит страницы, выборы, отзывы, связи
    override suspend fun clearStories() = storyDao.clearAllStories()


    // --- Draft Stories ---
    override suspend fun getStoryBaseInfoDraftsForUser(userId: String): List<StoryBaseInfoDraft> {
        return storyDraftDao.getStoryBaseInfoDraftsForUser(userId)
    }

    override suspend fun getStoryDraftWithDetails(draftId: String): StoryDraftWithPagesAndChoices? {
        return storyDraftDao.getStoryDraftWithDetails(draftId)
    }

    override suspend fun saveFullStoryDraft(
        story: StoryDraftEntity,
        pages: List<PageDraftEntity>,
        choices: List<ChoiceDraftEntity>
    ) {
        // Используем транзакционный метод DAO для сохранения всего черновика
        storyDraftDao.saveFullStoryDraft(story, pages, choices)
    }

    override suspend fun deleteStoryDraftById(draftId: String) {
        storyDraftDao.deleteStoryDraftById(draftId)
    }


    // --- Review ---
    override suspend fun saveReviewWithAuthor(review: ReviewEntity, author: UserEntity) {
        runInTransaction {
            userDao.insertOrUpdateUser(author) // Сохраняем/обновляем автора
            reviewDao.insertOrUpdateReview(review) // Сохраняем/обновляем отзыв
        }
    }
    override suspend fun saveReviewsWithAuthors(reviews: List<ReviewWithAuthor>) {
        runInTransaction {
            val authors = reviews.mapNotNull { it.author }
            if (authors.isNotEmpty()) {
                userDao.insertOrUpdateUsers(authors)
            }
            val reviewEntities = reviews.map { it.review }
            reviewDao.insertOrUpdateReviews(reviewEntities)
        }
    }


    override fun getReviewsForStoryStream(storyId: String): Flow<List<ReviewWithAuthor>> =
        reviewDao.getReviewsWithAuthorsForStoryStream(storyId)

    override suspend fun getReviewById(reviewId: String): ReviewWithAuthor? = reviewDao.getReviewWithAuthorById(reviewId)
    override suspend fun deleteReview(reviewId: String) = reviewDao.deleteReviewById(reviewId)
    override suspend fun clearReviewsForStory(storyId: String) = reviewDao.deleteReviewsForStory(storyId)
    override suspend fun clearAllReviews() = reviewDao.clearAllReviews()
}