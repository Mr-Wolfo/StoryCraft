package com.wolfo.storycraft.data.remote

import com.wolfo.storycraft.data.remote.dto.ReviewCreateRequestDto
import com.wolfo.storycraft.data.remote.dto.ReviewDto
import com.wolfo.storycraft.data.remote.dto.ReviewListResponseDto
import com.wolfo.storycraft.data.remote.dto.ReviewUpdateRequestDto
import com.wolfo.storycraft.data.remote.dto.StoryFullDto
import com.wolfo.storycraft.data.remote.dto.StoryListResponseDto
import com.wolfo.storycraft.data.remote.dto.UserAuthResponseDto
import com.wolfo.storycraft.data.remote.dto.UserDto
import com.wolfo.storycraft.data.remote.dto.UserRegisterRequestDto
import com.wolfo.storycraft.data.remote.dto.UserSimpleDto
import com.wolfo.storycraft.data.remote.dto.UserUpdateDto
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    // --- Authentication ---

    @POST("api/v1/auth/register")
    suspend fun registerUser(@Body userRegisterDto: UserRegisterRequestDto): Response<UserAuthResponseDto> // 201

    // Логин использует application/x-www-form-urlencoded
    @FormUrlEncoded
    @POST("api/v1/auth/login")
    suspend fun loginUser(
        @Field("username") username: String,
        @Field("password") password: String,
        @Field("grant_type") grantType: String? = "password",
        @Field("scope") scope: String? = "",
        @Field("client_id") clientId: String? = null,
        @Field("client_secret") clientSecret: String? = null
    ): Response<UserAuthResponseDto> // 200

    // --- Users ---

    @GET("api/v1/users/me")
    suspend fun getCurrentUser(): Response<UserDto> // 200 (Требует токен)

    @PATCH("api/v1/users/me")
    suspend fun updateUserMe(@Body userUpdateDto: UserUpdateDto): Response<UserDto> // 200 (Требует токен)

    @Multipart
    @PUT("api/v1/users/me/avatar")
    suspend fun updateUserAvatar(
        @Part avatarFile: MultipartBody.Part
    ): Response<UserDto> // 200 (Требует токен)

    @GET("api/v1/users/{user_id}/profile")
    suspend fun getUserProfile(@Path("user_id") userId: String): Response<UserSimpleDto> // 200

    // --- Stories ---

    @GET("api/v1/stories/")
    suspend fun getStories(
        @Query("skip") skip: Int = 0,
        @Query("limit") limit: Int = 10,
        @Query("sort_by") sortBy: String? = null, // "published_time", "rating", "views", "title"
        @Query("sort_order") sortOrder: String? = null, // "asc", "desc"
        @Query("search_query") searchQuery: String? = null,
        @Query("author_username") authorUsername: String? = null,
        @Query("tag_names") tagNames: List<String>? = null
    ): Response<StoryListResponseDto> // 200

    @Multipart
    @POST("api/v1/stories/")
    suspend fun createStory(
        @Part parts: List<MultipartBody.Part>
    ): Response<StoryFullDto> // 201

    @GET("api/v1/stories/{story_id}")
    suspend fun getStoryById(@Path("story_id") storyId: String): Response<StoryFullDto> // 200

    @Multipart
    @PATCH("api/v1/stories/{story_id}")
    suspend fun updateStory(
        @Path("story_id") storyId: String,
        @Part parts: List<MultipartBody.Part>
    ): Response<StoryFullDto> // 200

    @DELETE("api/v1/stories/{story_id}")
    suspend fun deleteStory(@Path("story_id") storyId: String): Response<Unit> // 204 (Требует токен)

    // --- Reviews ---

    @GET("api/v1/stories/{story_id}/reviews")
    suspend fun getReviewsForStory(
        @Path("story_id") storyId: String,
        @Query("skip") skip: Int = 0,
        @Query("limit") limit: Int = 10
    ): Response<ReviewListResponseDto> // 200

    @POST("api/v1/stories/{story_id}/reviews")
    suspend fun createReview(
        @Path("story_id") storyId: String,
        @Body reviewCreateDto: ReviewCreateRequestDto
    ): Response<ReviewDto> // 201 (Требует токен)

    @GET("api/v1/reviews/{review_id}")
    suspend fun getReviewById(@Path("review_id") reviewId: String): Response<ReviewDto> // 200

    @PATCH("api/v1/reviews/{review_id}")
    suspend fun updateReview(
        @Path("review_id") reviewId: String,
        @Body reviewUpdateDto: ReviewUpdateRequestDto
    ): Response<ReviewDto> // 200 (Требует токен)

    @DELETE("api/v1/reviews/{review_id}")
    suspend fun deleteReview(@Path("review_id") reviewId: String): Response<Unit> // 204 (Требует токен)

}