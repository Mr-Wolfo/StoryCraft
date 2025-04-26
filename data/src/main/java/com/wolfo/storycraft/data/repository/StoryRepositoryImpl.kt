package com.wolfo.storycraft.data.repository

import android.util.Log
import com.wolfo.storycraft.data.local.db.LocalDataSource
import com.wolfo.storycraft.data.mapper.toDomain
import com.wolfo.storycraft.data.mapper.toEntities
import com.wolfo.storycraft.data.mapper.toEntity
import com.wolfo.storycraft.data.remote.RemoteDataSource
import com.wolfo.storycraft.data.utils.BaseApiResponse
import com.wolfo.storycraft.data.utils.NetworkResult
import com.wolfo.storycraft.domain.model.Story
import com.wolfo.storycraft.domain.model.StoryBase
import com.wolfo.storycraft.domain.repository.StoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

class StoryRepositoryImpl(
    private val remoteDataSource: RemoteDataSource,
    private val localDataSource: LocalDataSource
): StoryRepository, BaseApiResponse() {
    override suspend fun observeStoryList(): Flow<List<StoryBase>> = flow {

        val localDataFlow = localDataSource.observeStoryList().map { entities ->
            entities.map { it.toDomain() }
        }
        var isFirstEmission = true

        localDataFlow.collect { storyListFromDb ->
            emit(storyListFromDb)

            if (isFirstEmission) {
                isFirstEmission = false
                refreshStoryList()
            }
        }
    }

    override suspend fun refreshStoryList() {
        val result = safeApiCall { remoteDataSource.getStoryList() }
        when(result) {
            is NetworkResult.Success -> {
                val stories = result.data?.map { it.toEntity() } ?: emptyList()
                localDataSource.updateStoryList(stories)
            }
            is NetworkResult.Error -> {
                throw Throwable(result.message)
            }
            is NetworkResult.Loading -> {

            }
        }
    }

    override suspend fun observeStoryFull(storyId: Long): Flow<Story> = flow {
        val localDataFlow = localDataSource.observeStoryFull(storyId = storyId).map { it?.toDomain() }

        var isFirstEmission = true

        localDataFlow.collect { story ->

            story?.let { emit(story) }

            if (isFirstEmission) {
                isFirstEmission = false
                loadStoryFull(storyId)
            }
        }
    }

    // override suspend fun loadStoryBase() {}

    override suspend fun observeStoryBase(storyId: Long): Flow<StoryBase> = flow {
        val localDataFlow = localDataSource.observeStoryBase(storyId = storyId).map { it.toDomain() }

        localDataFlow.collect {

            emit(it)
        }

    }

    override suspend fun loadStoryFull(storyId: Long) {
        Log.d("Get", "Loading")
        val result = safeApiCall { remoteDataSource.getStoryFull(storyId) }
        when(result) {
            is NetworkResult.Success -> {
                result.data?.let { storyDto ->
                    val (storyEntity, pageEntities, choiceEntities) = storyDto.toEntities()
                    Log.d("Get", "DESTRUCTED")
                    localDataSource.insertStory(
                        story = storyEntity,
                        pages = pageEntities,
                        choices = choiceEntities
                    )
                }
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