package com.wolfo.storycraft.data.local.db

import android.util.Log
import com.wolfo.storycraft.data.local.db.dao.StoryDao
import com.wolfo.storycraft.data.local.db.dao.UserDao
import com.wolfo.storycraft.data.local.db.entity.ChoiceEntity
import com.wolfo.storycraft.data.local.db.entity.PageEntity
import com.wolfo.storycraft.data.local.db.entity.StoryEntity
import com.wolfo.storycraft.data.local.db.entity.UserEntity

class LocalDataSource(private val storyDao: StoryDao,
    private val userDao: UserDao) {

    fun observeStoryList() = storyDao.observeStoryList()
    fun observeStoryFull(storyId: Long) = storyDao.observeStoryFullById(storyId = storyId)
    fun observeStoryBase(storyId: Long) = storyDao.observeStoryBaseById(storyId = storyId)
    suspend fun insertStory(story: StoryEntity, pages: List<PageEntity>, choices: List<ChoiceEntity>) {
        Log.d("LocalData", "InsertLoading")
        storyDao.insertOrReplaceStoryFull(story = story, pages = pages, choices = choices)
        Log.d("LocalData", "InsertSuccessful")
    }

    suspend fun updateStoryList(storyList: List<StoryEntity>) = storyDao.insertOrReplaceStoryList(storyList)
    suspend fun deleteStory(storyId: Long) = storyDao.deleteStoryById(storyId = storyId)

    fun observeProfile() = userDao.observeUser()
    suspend fun saveUser(user: UserEntity) = userDao.saveUser(user)
    suspend fun clearUser() = userDao.clearUser()

}

