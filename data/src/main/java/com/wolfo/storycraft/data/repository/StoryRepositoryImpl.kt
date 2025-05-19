package com.wolfo.storycraft.data.repository

import com.google.gson.Gson
import com.wolfo.storycraft.data.local.db.LocalDataSource
import com.wolfo.storycraft.data.local.db.entity.ReviewWithAuthor
import com.wolfo.storycraft.data.local.db.entity.StoryWithAuthorAndTags
import com.wolfo.storycraft.data.mapper.mapToStoryFullDomain
import com.wolfo.storycraft.data.mapper.toAuthorEntity
import com.wolfo.storycraft.data.mapper.toDomain
import com.wolfo.storycraft.data.mapper.toDomainBaseInfo
import com.wolfo.storycraft.data.mapper.toEntity
import com.wolfo.storycraft.data.mapper.toStoryEntity
import com.wolfo.storycraft.data.remote.RemoteDataSource
import com.wolfo.storycraft.data.remote.dto.ReviewCreateRequestDto
import com.wolfo.storycraft.data.remote.dto.ReviewUpdateRequestDto
import com.wolfo.storycraft.data.remote.dto.StoryCreateJsonDataDto
import com.wolfo.storycraft.data.remote.dto.StoryUpdateJsonDataDto
import com.wolfo.storycraft.data.utils.NetworkHandler
import com.wolfo.storycraft.data.utils.RepositoryHandler
import com.wolfo.storycraft.domain.DataError
import com.wolfo.storycraft.domain.ResultM
import com.wolfo.storycraft.domain.model.Review
import com.wolfo.storycraft.domain.model.StoryBaseInfo
import com.wolfo.storycraft.domain.model.StoryFull
import com.wolfo.storycraft.domain.model.StoryQuery
import com.wolfo.storycraft.domain.repository.StoryRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import java.io.File

class StoryRepositoryImpl(
    private val localDataSource: LocalDataSource,
    private val remoteDataSource: RemoteDataSource,
    private val networkHandler: NetworkHandler, // Используем предоставленный NetworkHandler
    private val repositoryHandler: RepositoryHandler,
    private val gson: Gson
) : StoryRepository {

    override fun getStoriesStream(
        forceRefresh: Boolean,
        query: StoryQuery
    ): Flow<ResultM<List<StoryBaseInfo>>> =
        flow {
            repositoryHandler.getData(
                localDataCall = { localDataSource.getStoriesStream() },
                networkResult = {
                    networkHandler.handleNetworkCall(
                        call = {
                            remoteDataSource.getStories(
                                skip = query.skip,
                                limit = query.limit,
                                sortBy = query.sortBy,
                                sortOrder = query.sortOrder,
                                searchQuery = query.searchQuery,
                                authorUsername = query.authorUsername,
                                tagNames = query.tagNames
                            )
                        },
                        transform = { response -> response.stories.map { it.toDomain() } },
                        onSuccess = { response ->
                            val storyRelations = response.stories.map { storyDto ->
                                StoryWithAuthorAndTags(
                                    story = storyDto.toEntity(),
                                    author = storyDto.author.toAuthorEntity(),
                                    tags = storyDto.tags.map { it.toEntity() }
                                )
                            }
                            localDataSource.saveStoriesWithDetails(storyRelations)
                        }
                    )
                },
                localTransform = { it.map { it.toDomainBaseInfo() } },
            ).flowOn(Dispatchers.IO)
        }

    override fun getStoryDetailsStream(
        storyId: String,
        forceRefresh: Boolean
    ): Flow<ResultM<StoryFull>> = flow<ResultM<StoryFull>> {
            repositoryHandler.getData(
                localDataCall = {
                    flow {
                        val cachedStory = localDataSource.getStoryWithAuthorAndTags(storyId)
                        val cachedPages = localDataSource.getPagesWithChoices(storyId)
                        emit(
                            if (cachedStory != null && cachedPages.isNotEmpty()) {
                                mapToStoryFullDomain(cachedStory, cachedPages)
                            } else {
                                null
                            }
                        )
                    }
                },
                networkResult = {
                    networkHandler.handleNetworkCall(
                        call = { remoteDataSource.getStoryById(storyId) },
                        transform = { it.toDomain() },
                        onSuccess = { storyFullDto ->
                            // Сохраняем полную историю
                            val storyEntity = storyFullDto.toStoryEntity()
                            val authorEntity = storyFullDto.author.toAuthorEntity()
                            val tagEntities = storyFullDto.tags.map { it.toEntity() }
                            val pageEntities = storyFullDto.pages.map { it.toEntity() }
                            val choiceEntities = storyFullDto.pages.flatMap { pageDto ->
                                pageDto.choices.map { choiceDto -> choiceDto.toEntity(pageDto.id) }
                            }

                            localDataSource.saveFullStory(
                                story = storyEntity,
                                author = authorEntity,
                                tags = tagEntities,
                                pages = pageEntities,
                                choices = choiceEntities
                            )
                        }
                    )
                },
                localTransform = { },
                needToRefresh = { cachedData ->
                    forceRefresh || cachedData == null || networkHandler.needToRefresh(lastRefresh = System.currentTimeMillis()) // ДОБАВИТЬ ПРОВЕРКУ
                },
                dataNullError = DataError.Unknown("Story not found")
            ).flowOn(Dispatchers.IO)
        }


    // --- Остальные методы репозитория (create, update, delete, reviews) ---
    // Реализуются аналогично, используя networkHandler и обновляя/удаляя данные
    // в localDataSource при успешных сетевых операциях.

    override suspend fun getBaseStoryById(storyId: String): ResultM<StoryBaseInfo> {
        return try {
            val story = localDataSource.getStoryWithAuthorAndTags(storyId)
            if (story != null) {
                ResultM.success(story.toDomainBaseInfo())
            } else {
                // Возможно, стоит попытаться загрузить из сети, если в кэше нет?
                ResultM.failure(DataError.Database("Story not found in cache"))
            }
        } catch (e: Exception) {
            ResultM.failure(DataError.Database("Failed to get base story: $e"))
        }
    }

    override suspend fun createStory(
        title: String,
        description: String?,
        tags: List<String>,
        coverImageFile: File?
    ): ResultM<StoryFull> = networkHandler.handleNetworkCall(
        call = {
            val storyDataJson = gson.toJson(StoryCreateJsonDataDto(title, description, tags))
            remoteDataSource.createStory(storyDataJson, coverImageFile)
        },
        transform = { it.toDomain() },
        onSuccess = { createdStoryDto ->
            // Сохраняем созданную историю в БД
            val storyEntity = createdStoryDto.toStoryEntity()
            val authorEntity = createdStoryDto.author.toAuthorEntity()
            val tagEntities = createdStoryDto.tags.map { it.toEntity() }
            val pageEntities = createdStoryDto.pages.map { it.toEntity() }
            val choiceEntities = createdStoryDto.pages.flatMap { pageDto ->
                pageDto.choices.map { choiceDto -> choiceDto.toEntity(pageDto.id) }
            }
            localDataSource.saveFullStory(storyEntity, authorEntity, tagEntities, pageEntities, choiceEntities)
        }
    )

    override suspend fun updateStory(
        storyId: String,
        title: String?,
        description: String?,
        tags: List<String>?,
        coverImageFile: File?
    ): ResultM<StoryFull> = networkHandler.handleNetworkCall(
        call = {
            val storyDataJson = if (title != null || description != null || tags != null) {
                gson.toJson(StoryUpdateJsonDataDto(title, description, tags))
            } else null
            remoteDataSource.updateStory(storyId, storyDataJson, coverImageFile)
        },
        transform = { it.toDomain() },
        onSuccess = { updatedStoryDto ->
            // Обновляем историю в БД (полностью перезаписываем)
            val storyEntity = updatedStoryDto.toStoryEntity()
            val authorEntity = updatedStoryDto.author.toAuthorEntity()
            val tagEntities = updatedStoryDto.tags.map { it.toEntity() }
            val pageEntities = updatedStoryDto.pages.map { it.toEntity() }
            val choiceEntities = updatedStoryDto.pages.flatMap { pageDto ->
                pageDto.choices.map { choiceDto -> choiceDto.toEntity(pageDto.id) }
            }
            localDataSource.saveFullStory(storyEntity, authorEntity, tagEntities, pageEntities, choiceEntities)
        }
    )

    override suspend fun deleteStory(storyId: String): ResultM<Unit> = networkHandler.handleNetworkCall(
        call = { remoteDataSource.deleteStory(storyId) },
        transform = { /* Unit */ },
        onSuccess = {
            // Удаляем из локальной БД
            localDataSource.deleteStory(storyId)
        }
    )

    // --- Reviews ---

    override fun getReviewsStream(storyId: String, forceRefresh: Boolean): Flow<ResultM<List<Review>>> =
        flow {

            repositoryHandler.getData(
                localDataCall = { localDataSource.getReviewsForStoryStream(storyId) },
                networkResult = {
                    networkHandler.handleNetworkCall(
                        call = {
                            // TODO: Реализовать пагинацию при запросе из сети
                            remoteDataSource.getReviewsForStory(storyId, skip = 0, limit = 10)
                        },
                        transform = { response -> response.reviews.map { it.toDomain() } },
                        onSuccess = { response ->
                            // Сохраняем отзывы и их авторов
                            val reviewRelations = response.reviews.map { reviewDto ->
                                val authorEntity = reviewDto.user.toAuthorEntity()
                                ReviewWithAuthor(
                                    review = reviewDto.toEntity(),
                                    author = authorEntity
                                )
                            }
                            // Очищаем старые отзывы для этой истории перед сохранением новых
                            localDataSource.clearReviewsForStory(storyId)
                            localDataSource.saveReviewsWithAuthors(reviewRelations)
                        }
                    )
                },
                localTransform = { it.map { it.toDomain() } },
            ).flowOn(Dispatchers.IO)
        }


    override suspend fun createReview(
        storyId: String,
        rating: Int,
        comment: String?
    ): ResultM<Review> = networkHandler.handleNetworkCall(
        call = {
            remoteDataSource.createReview(storyId, ReviewCreateRequestDto(rating, comment, storyId))
        },
        transform = { it.toDomain() },
        onSuccess = { createdReviewDto ->
            // Сохраняем новый отзыв в БД
            val authorEntity = createdReviewDto.user.toAuthorEntity()
            localDataSource.saveReviewWithAuthor(createdReviewDto.toEntity(), authorEntity)
        }
    )

    override suspend fun updateReview(
        reviewId: String,
        rating: Int?,
        comment: String?
    ): ResultM<Review> = networkHandler.handleNetworkCall(
        call = {
            remoteDataSource.updateReview(reviewId, ReviewUpdateRequestDto(rating, comment))
        },
        transform = { it.toDomain() },
        onSuccess = { updatedReviewDto ->
            // Обновляем отзыв в БД
            val authorEntity = updatedReviewDto.user.toAuthorEntity()
            localDataSource.saveReviewWithAuthor(updatedReviewDto.toEntity(), authorEntity)
        }
    )

    override suspend fun deleteReview(reviewId: String): ResultM<Unit> = networkHandler.handleNetworkCall(
        call = { remoteDataSource.deleteReview(reviewId) },
        transform = { /* Unit */ },
        onSuccess = {
            localDataSource.deleteReview(reviewId)
        }
    )
}