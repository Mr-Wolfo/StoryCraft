package com.wolfo.storycraft.domain.repository

import com.wolfo.storycraft.domain.model.Story
import com.wolfo.storycraft.domain.model.StoryBase
import kotlinx.coroutines.flow.Flow

interface StoryRepository {
    suspend fun observeStoryList(): Flow<List<StoryBase>>
    suspend fun refreshStoryList()
//    suspend fun loadStoryBase()
    suspend fun observeStoryBase(storyId: Long): Flow<StoryBase>
    suspend fun loadStoryFull(storyId: Long)
    suspend fun observeStoryFull(storyId: Long): Flow<Story>
    suspend fun createStory(storyId: Long): Long
    suspend fun updateStory(story: Story)
    suspend fun deleteStory(storyId: Long)
}