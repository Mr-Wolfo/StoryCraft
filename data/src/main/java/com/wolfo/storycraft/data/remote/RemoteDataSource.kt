package com.wolfo.storycraft.data.remote

import com.wolfo.storycraft.data.remote.dto.ReviewCreateRequestDto
import com.wolfo.storycraft.data.remote.dto.ReviewDto
import com.wolfo.storycraft.data.remote.dto.ReviewListResponseDto
import com.wolfo.storycraft.data.remote.dto.ReviewUpdateRequestDto
import com.wolfo.storycraft.data.remote.dto.StoryCreateJsonDataDto
import com.wolfo.storycraft.data.remote.dto.StoryFullDto
import com.wolfo.storycraft.data.remote.dto.StoryListResponseDto
import com.wolfo.storycraft.data.remote.dto.StoryUpdateJsonDataDto
import com.wolfo.storycraft.data.remote.dto.UserAuthResponseDto
import com.wolfo.storycraft.data.remote.dto.UserDto
import com.wolfo.storycraft.data.remote.dto.UserRegisterRequestDto
import com.wolfo.storycraft.data.remote.dto.UserSimpleDto
import com.wolfo.storycraft.data.remote.dto.UserUpdateDto
import com.wolfo.storycraft.data.utils.NetworkResult
import java.io.File

interface RemoteDataSource {

    // --- Auth ---
    suspend fun registerUser(request: UserRegisterRequestDto): NetworkResult<UserAuthResponseDto>
    suspend fun loginUser(username: String, password: String): NetworkResult<UserAuthResponseDto>
    // Метод refresh не выносим сюда, он используется внутри Authenticator

    // --- User ---
    suspend fun getCurrentUser(): NetworkResult<UserDto>
    suspend fun updateUser(request: UserUpdateDto): NetworkResult<UserDto>
    suspend fun updateUserAvatar(avatarFile: File): NetworkResult<UserDto>
    suspend fun getUserProfile(userId: String): NetworkResult<UserSimpleDto>

    // --- Story ---
    suspend fun getStories(
        skip: Int,
        limit: Int,
        sortBy: String?,
        sortOrder: String?,
        searchQuery: String?,
        authorUsername: String?,
        tagNames: List<String>?
    ): NetworkResult<StoryListResponseDto>

    suspend fun getStoryById(storyId: String): NetworkResult<StoryFullDto>

    suspend fun deleteStory(storyId: String): NetworkResult<Unit> // Ожидаем 204 No Content

    suspend fun createStory(
        storyData: StoryCreateJsonDataDto,
        coverImageFile: File?,
        pageImageFiles: List<File>,
        pageImageIndexes: List<String> // Принимаем список строковых индексов страниц
    ): NetworkResult<StoryFullDto>

    suspend fun updateStory(
        storyId: String,
        storyData: StoryUpdateJsonDataDto?,
        coverImageFile: File?
        // Если сделаем поддержку обновления страниц:
        // pageImageFiles: List<File>?,
        // pageImageIndexes: List<String>?,
        // ... и т.д.
    ): NetworkResult<StoryFullDto>

    // --- Review ---
    suspend fun getReviewsForStory(
        storyId: String,
        skip: Int,
        limit: Int
    ): NetworkResult<ReviewListResponseDto>

    suspend fun createReview(
        storyId: String,
        request: ReviewCreateRequestDto
    ): NetworkResult<ReviewDto>

    suspend fun getReviewById(reviewId: String): NetworkResult<ReviewDto>

    suspend fun updateReview(
        reviewId: String,
        request: ReviewUpdateRequestDto
    ): NetworkResult<ReviewDto>

    suspend fun deleteReview(reviewId: String): NetworkResult<Unit> // Ожидаем 204 No Content
}