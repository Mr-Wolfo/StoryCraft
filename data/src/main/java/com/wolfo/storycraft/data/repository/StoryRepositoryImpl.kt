package com.wolfo.storycraft.data.repository

import android.util.Log
import com.google.gson.Gson
import com.wolfo.storycraft.data.local.db.LocalDataSource
import com.wolfo.storycraft.data.local.db.entity.ReviewWithAuthor
import com.wolfo.storycraft.data.local.db.entity.StoryWithAuthorAndTags
import com.wolfo.storycraft.data.mapper.DraftContentToStoryDraftEntityMapper
import com.wolfo.storycraft.data.mapper.PublishContentToStoryCreateDtoMapper
import com.wolfo.storycraft.data.mapper.StoryDraftEntityToDraftContentMapper
import com.wolfo.storycraft.data.mapper.mapToStoryFullDomain
import com.wolfo.storycraft.data.mapper.toAuthorEntity
import com.wolfo.storycraft.data.mapper.toDomain
import com.wolfo.storycraft.data.mapper.toDomainBaseInfo
import com.wolfo.storycraft.data.mapper.toEntity
import com.wolfo.storycraft.data.mapper.toStoryEntity
import com.wolfo.storycraft.data.remote.RemoteDataSource
import com.wolfo.storycraft.data.remote.dto.ReviewCreateRequestDto
import com.wolfo.storycraft.data.remote.dto.ReviewUpdateRequestDto
import com.wolfo.storycraft.data.remote.dto.StoryUpdateJsonDataDto
import com.wolfo.storycraft.data.utils.NetworkHandler
import com.wolfo.storycraft.data.utils.RepositoryHandler
import com.wolfo.storycraft.domain.DataError
import com.wolfo.storycraft.domain.ResultM
import com.wolfo.storycraft.domain.model.draft.DraftContent
import com.wolfo.storycraft.domain.model.PublishContent
import com.wolfo.storycraft.domain.model.Review
import com.wolfo.storycraft.domain.model.story.StoryBaseInfo
import com.wolfo.storycraft.domain.model.story.StoryFull
import com.wolfo.storycraft.domain.model.StoryQuery
import com.wolfo.storycraft.domain.repository.StoryRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import java.io.File

class StoryRepositoryImpl(
    private val localDataSource: LocalDataSource,
    private val remoteDataSource: RemoteDataSource,
    private val networkHandler: NetworkHandler,
    private val repositoryHandler: RepositoryHandler,
    private val storyDraftEntityToDraftContentMapper: StoryDraftEntityToDraftContentMapper, // Entity Draft -> Domain DraftContent
    private val draftContentToStoryDraftEntityMapper: DraftContentToStoryDraftEntityMapper, // Domain DraftContent -> Entity Draft
    private val publishContentToStoryCreateDtoMapper: PublishContentToStoryCreateDtoMapper, // Domain PublishContent -> DTO Create

    private val gson: Gson
) : StoryRepository {

    override fun getStoriesStream(
        forceRefresh: Boolean,
        query: StoryQuery
    ): Flow<ResultM<List<StoryBaseInfo>>> =
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
                        localDataSource.saveUsers(response.stories.map { story -> story.author.toAuthorEntity() })
                        localDataSource.saveStoriesWithDetails(storyRelations)
                    }
                )
            },
            localTransform = { it.map { it.toDomainBaseInfo() } },
        ).flowOn(Dispatchers.IO)

    override fun getStoryDetailsStream(
        storyId: String,
        forceRefresh: Boolean
    ): Flow<ResultM<StoryFull>> =
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
                localTransform = { it!! },
                needToRefresh = { cachedData ->
                    forceRefresh || cachedData == null || networkHandler.needToRefresh(lastRefresh = System.currentTimeMillis()) // ДОБАВИТЬ ПРОВЕРКУ
                },
                dataNullError = DataError.Unknown("Story not found")
            ).flowOn(Dispatchers.IO)


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

    // --- Draft Stories ---

    override fun getDraftStoriesForUser(userId: String): Flow<ResultM<List<StoryBaseInfo>>> = flow {
        emit(ResultM.Loading)
        try {
            // Получаем черновики из локальной БД (Draft Entity Projection)
            val draftEntities = localDataSource.getStoryBaseInfoDraftsForUser(userId)
            // Маппим Draft Entity Projection -> Domain BaseInfo (используя StoryDraftEntityToDraftContentMapper)
            val drafts = draftEntities.map { storyDraftEntityToDraftContentMapper.mapBaseInfo(it) }
            emit(ResultM.success(drafts)) // Успех с Domain моделями
        } catch (e: Exception) {
            emit(ResultM.failure(DataError.Database(e.message))) // Ошибка с Domain моделью DataError
        }
    }.flowOn(Dispatchers.IO)

    override suspend fun getStoryDraft(draftId: String): ResultM<DraftContent> { // Возвращаем Domain DraftContent
        return withContext(Dispatchers.IO) {
            try {
                // Получаем черновик с деталями из локальной БД (Draft Entity WithRelations)
                val draftWithDetails = localDataSource.getStoryDraftWithDetails(draftId)
                if (draftWithDetails != null) {
                    // Маппим Draft Entity WithRelations -> Domain Model (DraftContent)
                    ResultM.success(storyDraftEntityToDraftContentMapper.map(draftWithDetails))
                } else {
                    ResultM.failure(DataError.Database("Not found")) // Ошибка с Domain моделью DataError
                }
            } catch (e: Exception) {
                ResultM.failure(DataError.Database(e.message)) // Ошибка с Domain моделью DataError
            }
        }
    }

    override suspend fun saveStoryDraft(draftContent: DraftContent): ResultM<Unit> { // Принимаем Domain DraftContent
        return withContext(Dispatchers.IO) {
            try {
                // Маппим Domain Model (DraftContent) -> Draft Entities
                val storyEntity = draftContentToStoryDraftEntityMapper.map(draftContent)
                val pageEntities = draftContentToStoryDraftEntityMapper.mapPages(draftContent)
                val choiceEntities = draftContentToStoryDraftEntityMapper.mapChoices(draftContent)

                // Сохраняем в локальную БД
                localDataSource.saveFullStoryDraft(storyEntity, pageEntities, choiceEntities)
                ResultM.success(Unit)
            } catch (e: Exception) {
                ResultM.failure(DataError.Database(e.message))
            }
        }
    }

    override suspend fun deleteStoryDraft(draftId: String): ResultM<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                // Удаляем из локальной БД
                localDataSource.deleteStoryDraftById(draftId)
                ResultM.success(Unit)
            } catch (e: Exception) {
                ResultM.failure(DataError.Database(e.message))
            }
        }
    }


    // --- Publish Story ---
    override suspend fun publishStory(
        publishContent: PublishContent // Принимаем Domain модель PublishContent
    ): ResultM<StoryFull> = networkHandler.handleNetworkCall(
        // call: Подготовка данных для отправки и вызов remoteDataSource
        call = {
            // Маппим Domain PublishContent -> Remote DTO (для создания JSON части)
            val storyDataDto = publishContentToStoryCreateDtoMapper.mapJsonData(publishContent)

            // Вызываем RemoteDataSource с подготовленными DTO и File-ами из PublishContent
            remoteDataSource.createStory(
                storyData = storyDataDto,
                coverImageFile = publishContent.coverImageFile, // Берем File из Domain model
                pageImageFiles = publishContent.pages.mapNotNull { it.imageFile }, // Берем File из Domain model
                pageImageIndexes = publishContent.pages.mapIndexedNotNull { index, page -> if (page.imageFile != null) index.toString() else null } // Генерируем индексы из Domain model
            )
            // RemoteDataSourceImpl сам займется Multipart запросом и очисткой временных файлов
        },
        // transform: Маппинг ответа Remote DTO -> Domain
        transform = { it.toDomain() }, // StoryFullDto.toDomain() (extension function DTO->Domain)
        // onSuccess: Действия при успешной публикации (например, кэширование опубликованной истории)
        onSuccess = { createdStoryDto ->
            // Сохраняем опубликованную историю в локальную БД (Remote DTO -> Entity)
            // Используем общие extension mappers DTO -> Entity
            val storyEntity = createdStoryDto.toStoryEntity()
            val authorEntity = createdStoryDto.author.toAuthorEntity()
            val tagEntities = createdStoryDto.tags.map { it.toEntity() }
            val pageEntities = createdStoryDto.pages.map { it.toEntity() }
            val choiceEntities = createdStoryDto.pages.flatMap { pageDto ->
                pageDto.choices.map { choiceDto -> choiceDto.toEntity(pageDto.id) }
            }
            // Используем транзакционный метод LocalDataSource для сохранения полной истории
            localDataSource.saveFullStory(storyEntity, authorEntity, tagEntities, pageEntities, choiceEntities)

            // Удаление черновика происходит в Use Case после получения успешного результата от репозитория.
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
            remoteDataSource.updateStory(storyId, StoryUpdateJsonDataDto(title, description, tags), coverImageFile)
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
        repositoryHandler.getData(
            localDataCall = {
                Log.d("DB", "Reading from DB")
                localDataSource.getReviewsForStoryStream(storyId)
            },
            networkResult = {
                Log.d("API", "Fetching from network")
                networkHandler.handleNetworkCall(
                    call = { remoteDataSource.getReviewsForStory(storyId, skip = 0, limit = 10) },
                    transform = { response ->
                        val reviews = response.reviews.map { it.toDomain() }
                        Log.d("MAPPING", "Mapped ${reviews.size} reviews")
                        reviews
                    },
                    onSuccess = { response ->
                        Log.d("DB", "Starting DB update")

                            localDataSource.clearReviewsForStory(storyId)
                            Log.d("DB", "Cleared old reviews")
                            val relations = response.reviews.map {
                                ReviewWithAuthor(it.toEntity(), it.user.toAuthorEntity())
                            }
                            localDataSource.saveReviewsWithAuthors(relations)
                            Log.d("DB", "Saved ${relations.size} new reviews")

                    }
                )
            },
            localTransform = { entities ->
                val reviews = entities.map { it.toDomain() }
                Log.d("MAPPING", "Local mapped ${reviews.size} reviews")
                reviews
            },
            sourcesDataMergeTransform = { _, fresh -> fresh }
        ).flowOn(Dispatchers.IO)



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