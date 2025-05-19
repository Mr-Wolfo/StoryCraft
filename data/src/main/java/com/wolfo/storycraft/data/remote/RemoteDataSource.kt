package com.wolfo.storycraft.data.remote

import com.wolfo.storycraft.data.remote.dto.AuthRequestDto
import com.wolfo.storycraft.data.remote.dto.ReviewCreateRequestDto
import com.wolfo.storycraft.data.remote.dto.ReviewDto
import com.wolfo.storycraft.data.remote.dto.ReviewListResponseDto
import com.wolfo.storycraft.data.remote.dto.ReviewUpdateRequestDto
import com.wolfo.storycraft.data.remote.dto.UserRegisterRequestDto
import com.wolfo.storycraft.data.remote.dto.StoryFullDto
import com.wolfo.storycraft.data.remote.dto.StoryListResponseDto
import com.wolfo.storycraft.data.remote.dto.UserAuthResponseDto
import com.wolfo.storycraft.data.remote.dto.UserDto
import com.wolfo.storycraft.data.remote.dto.UserSimpleDto
import com.wolfo.storycraft.data.remote.dto.UserUpdateDto
import com.wolfo.storycraft.data.utils.NetworkResult
import com.wolfo.storycraft.data.utils.safeApiCall
import com.wolfo.storycraft.data.utils.safeEmptyApiCall
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
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

    suspend fun createStory(
        storyJsonData: String, // JSON строка с данными истории
        coverImageFile: File? // Опциональный файл обложки
    ): NetworkResult<StoryFullDto>

    suspend fun getStoryById(storyId: String): NetworkResult<StoryFullDto>

    suspend fun updateStory(
        storyId: String,
        storyJsonData: String?, // JSON строка с обновляемыми полями
        coverImageFile: File? // Опциональный новый файл обложки
    ): NetworkResult<StoryFullDto>

    suspend fun deleteStory(storyId: String): NetworkResult<Unit> // Ожидаем 204 No Content

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


class RemoteDataSourceImpl(
    private val apiService: ApiService
) : RemoteDataSource {

    // --- Auth ---
    override suspend fun registerUser(request: UserRegisterRequestDto): NetworkResult<UserAuthResponseDto> {
        return safeApiCall { apiService.registerUser(request) }
    }

    override suspend fun loginUser(username: String, password: String): NetworkResult<UserAuthResponseDto> {
        // Здесь используем @Field параметры для FormUrlEncoded запроса
        return safeApiCall { apiService.loginUser(username = username, password = password) }
    }

    // --- User ---
    override suspend fun getCurrentUser(): NetworkResult<UserDto> {
        return safeApiCall { apiService.getCurrentUser() }
    }

    override suspend fun updateUser(request: UserUpdateDto): NetworkResult<UserDto> {
        return safeApiCall { apiService.updateUserMe(request) }
    }

    override suspend fun updateUserAvatar(avatarFile: File): NetworkResult<UserDto> {
        return try {
            // Создаем RequestBody из файла
            val requestFile = avatarFile.asRequestBody("image/*".toMediaTypeOrNull())
            // Создаем MultipartBody.Part
            val body = MultipartBody.Part.createFormData("avatar_file", avatarFile.name, requestFile)
            // Выполняем безопасный вызов
            safeApiCall { apiService.updateUserAvatar(body) }
        } catch (e: Exception) {
            NetworkResult.Exception(e) // Ошибка при работе с файлом
        }
    }

    override suspend fun getUserProfile(userId: String): NetworkResult<UserSimpleDto> {
        return safeApiCall { apiService.getUserProfile(userId) }
    }

    // --- Story ---
    override suspend fun getStories(
        skip: Int, limit: Int, sortBy: String?, sortOrder: String?,
        searchQuery: String?, authorUsername: String?, tagNames: List<String>?
    ): NetworkResult<StoryListResponseDto> {
        return safeApiCall {
            apiService.getStories(
                skip = skip,
                limit = limit,
                sortBy = sortBy,
                sortOrder = sortOrder,
                searchQuery = searchQuery,
                authorUsername = authorUsername,
                tagNames = tagNames
            )
        }
    }

    override suspend fun createStory(
        storyJsonData: String,
        coverImageFile: File?
    ): NetworkResult<StoryFullDto> {
        return try {
            // Создаем RequestBody для JSON строки
            val storyDataRequestBody = storyJsonData.toRequestBody("application/json".toMediaTypeOrNull())

            // Создаем MultipartBody.Part для файла обложки, если он есть
            val coverImagePart: MultipartBody.Part? = coverImageFile?.let { file ->
                val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
                MultipartBody.Part.createFormData("cover_image_file", file.name, requestFile)
            }

            // Выполняем безопасный вызов API
            safeApiCall { apiService.createStory(storyData = storyDataRequestBody, coverImageFile = coverImagePart) }
        } catch (e: Exception) {
            NetworkResult.Exception(e) // Ошибка при подготовке запроса (например, файл не найден)
        }
    }


    override suspend fun getStoryById(storyId: String): NetworkResult<StoryFullDto> {
        return safeApiCall { apiService.getStoryById(storyId) }
    }

    override suspend fun updateStory(
        storyId: String,
        storyJsonData: String?,
        coverImageFile: File?
    ): NetworkResult<StoryFullDto> {
        return try {
            // Создаем RequestBody для JSON строки, если она есть
            val storyDataRequestBody: RequestBody? = storyJsonData?.toRequestBody("application/json".toMediaTypeOrNull())

            // Создаем MultipartBody.Part для файла обложки, если он есть
            val coverImagePart: MultipartBody.Part? = coverImageFile?.let { file ->
                val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
                MultipartBody.Part.createFormData("cover_image_file", file.name, requestFile)
            }

            // Выполняем безопасный вызов API
            safeApiCall { apiService.updateStory(storyId = storyId, storyData = storyDataRequestBody, coverImageFile = coverImagePart) }
        } catch (e: Exception) {
            NetworkResult.Exception(e)
        }
    }

    override suspend fun deleteStory(storyId: String): NetworkResult<Unit> {
        // Используем safeEmptyApiCall для запросов без тела ответа при успехе (204)
        return safeEmptyApiCall { apiService.deleteStory(storyId) }
    }

    // --- Review ---
    override suspend fun getReviewsForStory(
        storyId: String,
        skip: Int,
        limit: Int
    ): NetworkResult<ReviewListResponseDto> {
        return safeApiCall { apiService.getReviewsForStory(storyId = storyId, skip = skip, limit = limit) }
    }

    override suspend fun createReview(
        storyId: String,
        request: ReviewCreateRequestDto
    ): NetworkResult<ReviewDto> {
        // Добавляем storyId в тело запроса, если API этого требует (согласно OpenAPI)
        val requestWithStoryId = request.copy(storyId = storyId) // OpenAPI схема ReviewCreate ожидает story_id
        return safeApiCall { apiService.createReview(storyId = storyId, reviewCreateDto = requestWithStoryId) }
    }

    override suspend fun getReviewById(reviewId: String): NetworkResult<ReviewDto> {
        return safeApiCall { apiService.getReviewById(reviewId) }
    }

    override suspend fun updateReview(
        reviewId: String,
        request: ReviewUpdateRequestDto
    ): NetworkResult<ReviewDto> {
        return safeApiCall { apiService.updateReview(reviewId = reviewId, reviewUpdateDto = request) }
    }

    override suspend fun deleteReview(reviewId: String): NetworkResult<Unit> {
        return safeEmptyApiCall { apiService.deleteReview(reviewId) }
    }
}