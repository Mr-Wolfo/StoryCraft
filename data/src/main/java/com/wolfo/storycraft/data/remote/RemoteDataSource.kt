package com.wolfo.storycraft.data.remote

import com.wolfo.storycraft.data.remote.dto.StoryDto

class RemoteDataSource(private val apiService: ApiService) {

    suspend fun getStoryList() = apiService.getStoryList()
    suspend fun getStoryFull(storyId: Long) = apiService.getStoryFull(storyId = storyId)
    suspend fun putStory(body: StoryDto) = apiService.putStory(body = body)
    suspend fun updateStory(storyId: Long, body: StoryDto) = apiService.updateStory(storyId = storyId, body = body)
    suspend fun deleteStory(storyId: Long) = apiService.deleteStory(storyId = storyId)
}