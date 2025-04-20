package com.wolfo.storycraft.data.remote

import com.wolfo.storycraft.data.remote.dto.AuthRequestDto
import com.wolfo.storycraft.data.remote.dto.AuthResponseDto
import com.wolfo.storycraft.data.remote.dto.RegisterRequestDto
import com.wolfo.storycraft.data.remote.dto.StoryDto
import com.wolfo.storycraft.data.remote.dto.StoryBaseDto
import com.wolfo.storycraft.data.remote.dto.UserDto
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ApiService {
    @GET("/api/v1/stories/")
    suspend fun getStoryList() : Response<List<StoryBaseDto>>

    @GET("/api/v1/stories/{story_id}")
    suspend fun getStoryFull(@Path("story_id") storyId: Long): Response<StoryDto>

    @POST("/api/v1/stories")
    suspend fun putStory(@Body body: StoryDto) : Response<StoryDto>

    @PUT("/api/v1/stories/{story_id}")
    suspend fun updateStory(@Path("story_id") storyId: Long, @Body body: StoryDto): Response<StoryBaseDto>

    @DELETE("/api/v1/stories/{story_id}")
    suspend fun deleteStory(@Path("story_id") storyId: Long): Response<StoryBaseDto>

    @POST("/api/v1/auth/register")
    suspend fun register(@Body registerRequest: RegisterRequestDto): Response<AuthResponseDto>

    @POST("/api/v1/auth/login")
    suspend fun login(@Body authRequest: AuthRequestDto): Response<AuthResponseDto>

    @GET("/api/v1/auth")
    suspend fun getProfile(@Header("Autorization") token: String): Response<UserDto>

    companion object{
        var apiService: ApiService? = null
        fun getInstance(): ApiService {
            if(apiService == null) {
                apiService = Retrofit.Builder().baseUrl("https://localhost/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build().create(ApiService::class.java)
            }
            return apiService!!
        }
    }
}