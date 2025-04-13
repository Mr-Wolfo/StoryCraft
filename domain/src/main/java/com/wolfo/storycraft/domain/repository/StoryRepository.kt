package com.wolfo.storycraft.domain.repository

import com.wolfo.storycraft.domain.model.Story
import com.wolfo.storycraft.domain.model.StoryBase
import kotlinx.coroutines.flow.Flow

interface StoryRepository {
    suspend fun getStories(): Flow<List<StoryBase>>
    suspend fun getStoryFull(storyId: Long): Story?
    suspend fun createStory(storyId: Long): Long
    suspend fun updateStory(story: Story)
    suspend fun deleteStory(storyId: Long)
}