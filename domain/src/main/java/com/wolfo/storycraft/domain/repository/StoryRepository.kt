package com.wolfo.storycraft.domain.repository

import com.wolfo.storycraft.domain.ResultM
import com.wolfo.storycraft.domain.model.Review
import com.wolfo.storycraft.domain.model.StoryBaseInfo
import com.wolfo.storycraft.domain.model.StoryFull
import com.wolfo.storycraft.domain.model.StoryQuery
import kotlinx.coroutines.flow.Flow
import java.io.File

interface StoryRepository {
    /**
     * Получает поток списка историй (базовая информация).
     * Реализует стратегию "Single Source of Truth" с кэшированием.
     * TODO: Добавить параметры фильтрации, сортировки, пагинации (Paging 3).
     *
     * @param forceRefresh Принудительно обновить данные из сети.
     * @param query Параметры запроса (поиск, фильтры, сортировка).
     * @return Flow<Result<List<StoryBaseInfoDto>>> Поток с результатом.
     */
    fun getStoriesStream(forceRefresh: Boolean = false, query: StoryQuery = StoryQuery()): Flow<ResultM<List<StoryBaseInfo>>> // Пока без Paging 3

    // TODO: fun getStoriesPagingSource(query: StoryQuery): PagingSource<Int, StoryBaseInfoDto>

    /**
     * Получает полную информацию об истории по ID.
     * Сначала отдает кэш (если есть), потом запрашивает сеть и обновляет кэш.
     *
     * @param storyId ID истории.
     * @param forceRefresh Принудительно обновить данные из сети.
     * @return Flow<Result<StoryFullDto>> Поток с полной историей.
     */
    fun getStoryDetailsStream(storyId: String, forceRefresh: Boolean = false): Flow<ResultM<StoryFull>>


    /**
     * Получает базовую информацию об истории по Id
     * Загружает кэшированную информацию из локальной БД
     *
     * @param storyId Id запрашеваемой истории.
     * @return ResultM<StoryBaseInfo> Результат с базовой информации об истории
     */
    suspend fun getBaseStoryById(storyId: String): ResultM<StoryBaseInfo>

    /**
     * Создает новую историю.
     *
     * @param title Название.
     * @param description Описание.
     * @param tags Список имен тегов.
     * @param coverImageFile Файл обложки (опционально).
     * @return Result<StoryFullDto> Результат с созданной историей.
     */
    suspend fun createStory(
        title: String,
        description: String?,
        tags: List<String>,
        // TODO: Добавить страницы/выборы при создании, если API поддерживает
        coverImageFile: File?
    ): ResultM<StoryFull>

    /**
     * Обновляет существующую историю.
     *
     * @param storyId ID истории.
     * @param title Новое название (опционально).
     * @param description Новое описание (опционально).
     * @param tags Новый список имен тегов (опционально).
     * @param coverImageFile Новый файл обложки (опционально).
     * @return Result<StoryFullDto> Результат с обновленной историей.
     */
    suspend fun updateStory(
        storyId: String,
        title: String?,
        description: String?,
        tags: List<String>?,
        coverImageFile: File?
    ): ResultM<StoryFull>

    /**
     * Удаляет историю.
     *
     * @param storyId ID истории.
     * @return Result<Unit> Успех или ошибка.
     */
    suspend fun deleteStory(storyId: String): ResultM<Unit>


    // --- Reviews ---

    /**
     * Получает поток отзывов для истории.
     * TODO: Добавить пагинацию (Paging 3).
     *
     * @param storyId ID истории.
     * @param forceRefresh Принудительно обновить данные из сети.
     * @return Flow<Result<List<ReviewDto>>> Поток со списком отзывов.
     */
    fun getReviewsStream(storyId: String, forceRefresh: Boolean = false): Flow<ResultM<List<Review>>> // Пока без Paging 3

    // TODO: fun getReviewsPagingSource(storyId: String): PagingSource<Int, ReviewDto>

    /**
     * Создает отзыв для истории.
     *
     * @param storyId ID истории.
     * @param rating Оценка (1-5).
     * @param comment Текст отзыва (опционально).
     * @return Result<ReviewDto> Результат с созданным отзывом.
     */
    suspend fun createReview(storyId: String, rating: Int, comment: String?): ResultM<Review>

    /**
     * Обновляет отзыв.
     *
     * @param reviewId ID отзыва.
     * @param rating Новая оценка (опционально).
     * @param comment Новый текст отзыва (опционально).
     * @return Result<ReviewDto> Результат с обновленным отзывом.
     */
    suspend fun updateReview(reviewId: String, rating: Int?, comment: String?): ResultM<Review>

    /**
     * Удаляет отзыв.
     *
     * @param reviewId ID отзыва.
     * @return Result<Unit> Успех или ошибка.
     */
    suspend fun deleteReview(reviewId: String): ResultM<Unit>
}
