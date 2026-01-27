package com.wolfo.storycraft.data.remote

import android.content.Context
import android.net.Uri
import com.google.gson.Gson
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
import com.wolfo.storycraft.data.utils.safeApiCall
import com.wolfo.storycraft.data.utils.safeEmptyApiCall
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

class RemoteDataSourceImpl(
    private val apiService: ApiService,
    private val gson: Gson,
    private val context: Context
) : RemoteDataSource {

    // --- Auth ---
    override suspend fun registerUser(request: UserRegisterRequestDto): NetworkResult<UserAuthResponseDto> {
        return safeApiCall { apiService.registerUser(request) }
    }

    override suspend fun loginUser(username: String, password: String): NetworkResult<UserAuthResponseDto> {
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

            val mimeType = when (avatarFile.extension.lowercase()) {
                "jpg", "jpeg" -> "image/jpeg"
                "png" -> "image/png"
                "gif" -> "image/gif"
                "webp" -> "image/webp"
                else -> throw IllegalArgumentException("Unsupported image format")
            }

            // Создаем RequestBody из файла
            val requestFile = avatarFile.asRequestBody(mimeType.toMediaTypeOrNull())
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

    override suspend fun getStoryById(storyId: String): NetworkResult<StoryFullDto> {
        return safeApiCall { apiService.getStoryById(storyId) }
    }

    override suspend fun createStory(
        storyData: StoryCreateJsonDataDto,
        coverImageFile: File?,
        pageImageFiles: List<File>,
        pageImageIndexes: List<String>
    ): NetworkResult<StoryFullDto> {
        // Используем try/catch для обработки возможных ошибок (хотя основные ошибки File должны быть обработаны выше в VM)
        return try {
            // Сериализуем структурированный DTO в JSON
            val storyDataJson = gson.toJson(storyData)
            val storyDataRequestBody = storyDataJson.toRequestBody("application/json".toMediaTypeOrNull())

            // Подготавливаем MultipartBody.Part для файла обложки
            val coverImagePart: MultipartBody.Part? = coverImageFile?.let { file ->
                val mimeType = when (file.extension.lowercase()) {
                    "jpg", "jpeg" -> "image/jpeg"
                    "png" -> "image/png"
                    "gif" -> "image/gif"
                    "webp" -> "image/webp"
                    else -> throw IllegalArgumentException("Unsupported image format")
                }
                val requestFile = file.asRequestBody(mimeType.toMediaTypeOrNull())
                MultipartBody.Part.createFormData("cover_image_file", file.name, requestFile)
            }

            // Подготавливаем MultipartBody.Part для файлов изображений страниц
            val pageImageParts: List<MultipartBody.Part> = pageImageFiles.map { file ->
                val mimeType = when (file.extension.lowercase()) {
                    "jpg", "jpeg" -> "image/jpeg"
                    "png" -> "image/png"
                    "gif" -> "image/gif"
                    "webp" -> "image/webp"
                    else -> throw IllegalArgumentException("Unsupported image format")
                }
                val requestFile = file.asRequestBody(mimeType.toMediaTypeOrNull())
                // Имя поля в multipart должно быть "page_images" для списка файлов
                MultipartBody.Part.createFormData("page_images", file.name, requestFile)
            }

            // Подготавливаем MultipartBody.Part для индексов страниц для изображений
            val pageImageIndexParts: List<MultipartBody.Part> = pageImageIndexes.map { indexString ->
                // Имя поля должно быть "page_image_indexes"
                MultipartBody.Part.createFormData("page_image_indexes", indexString)
            }

            // Комбинируем все части для отправки
            val allParts = mutableListOf<MultipartBody.Part>(
                MultipartBody.Part.createFormData("story_data", null, storyDataRequestBody) // Поле story_data
            ).apply {
                if (coverImagePart != null) add(coverImagePart) // Файл обложки (опционально)
                addAll(pageImageParts) // Файлы страниц (может быть несколько частей)
                addAll(pageImageIndexParts) // Индексы страниц (может быть несколько частей)
            }

            // Выполняем безопасный вызов API
            safeApiCall { apiService.createStory(allParts) } // Вызываем модифицированный apiService метод
        } catch (e: Exception) {
            // Обрабатываем оставшиеся ошибки (например, сетевые, сериализации)
            NetworkResult.Exception(e) // Преобразуем исключение в NetworkResult.Exception
        }
    }


    override suspend fun updateStory(
        storyId: String,
        storyData: StoryUpdateJsonDataDto?,
        coverImageFile: File?
    ): NetworkResult<StoryFullDto> {
        return try {
            val storyDataRequestBody: RequestBody? = storyData?.let {
                val storyDataJson = gson.toJson(it)
                storyDataJson.toRequestBody("application/json".toMediaTypeOrNull())
            }

            val coverImagePart: MultipartBody.Part? = coverImageFile?.let { file ->
                val mimeType = when (file.extension.lowercase()) {
                    "jpg", "jpeg" -> "image/jpeg"
                    "png" -> "image/png"
                    "gif" -> "image/gif"
                    "webp" -> "image/webp"
                    else -> throw IllegalArgumentException("Unsupported image format")
                }
                val requestFile = file.asRequestBody(mimeType.toMediaTypeOrNull())
                MultipartBody.Part.createFormData("cover_image_file", file.name, requestFile)
            }

            val allParts = mutableListOf<MultipartBody.Part>().apply {
                if (storyDataRequestBody != null) add(MultipartBody.Part.createFormData("story_data", null, storyDataRequestBody))
                if (coverImagePart != null) add(coverImagePart)
            }

            if (allParts.isEmpty()) {
                return NetworkResult.Error(code = 400, message = "No update data provided")
            }

            safeApiCall { apiService.updateStory(storyId, allParts) }
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
        val requestWithStoryId = request.copy(storyId = storyId)
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

    // Вспомогательная функция для получения расширения файла по Uri
    private fun getFileExtension(context: Context, uri: Uri): String {
        val fileType = context.contentResolver.getType(uri)
        return when {
            fileType?.startsWith("image/jpeg") == true -> "jpg"
            fileType?.startsWith("image/png") == true -> "png"
            fileType?.startsWith("image/gif") == true -> "gif"
            fileType?.startsWith("image/webp") == true -> "webp"
            else -> "tmp" // Fallback
        }
    }
}