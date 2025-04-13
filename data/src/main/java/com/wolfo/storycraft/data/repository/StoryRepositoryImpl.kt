package com.wolfo.storycraft.data.repository

import android.util.Log
import com.wolfo.storycraft.data.mapper.toDomain
import com.wolfo.storycraft.data.remote.RemoteDataSource
import com.wolfo.storycraft.data.remote.dto.StoryDto
import com.wolfo.storycraft.data.utils.BaseApiResponse
import com.wolfo.storycraft.data.utils.NetworkResult
import com.wolfo.storycraft.domain.model.Story
import com.wolfo.storycraft.domain.model.StoryBase
import com.wolfo.storycraft.domain.repository.StoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class StoryRepositoryImpl(
    private val remoteDataSource: RemoteDataSource
): StoryRepository, BaseApiResponse() {
    override suspend fun getStories(): Flow<List<StoryBase>> = flow {
        val result = safeApiCall { remoteDataSource.getStoryList() }
        when(result) {
            is NetworkResult.Success -> {
                val stories = result.data?.map { it.toDomain() } ?: emptyList()
                emit(stories)
            }
            is NetworkResult.Error -> {
                throw Throwable(result.message)
            }
            is NetworkResult.Loading -> {

            }
        }
    }

    override suspend fun getStoryFull(storyId: Long): Story? {
        Log.d("Get", "Loading")
        val result = safeApiCall { remoteDataSource.getStoryFull(storyId) }
        return when(result) {
            is NetworkResult.Success -> {
                Log.d("Data", "${result.data?.description}")
                result.data?.toDomain()
            }

            is NetworkResult.Error -> {
                Log.d("Data", "ERROR")
                throw Throwable(result.message)
            }

            is NetworkResult.Loading -> {
                Log.d("Data", "LOADING")
                null
            }
        }
    }

    override suspend fun createStory(storyId: Long): Long { return -1L }
    override suspend fun updateStory(story: Story) {}
    override suspend fun deleteStory(storyId: Long) {}
}