package com.wolfo.storycraft.data.local.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.wolfo.storycraft.data.local.db.entity.StoryEntity
import com.wolfo.storycraft.data.local.db.entity.StoryTagCrossRef
import com.wolfo.storycraft.data.local.db.entity.StoryWithAuthorAndTags
import kotlinx.coroutines.flow.Flow

@Dao
interface StoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateStory(story: StoryEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateStories(stories: List<StoryEntity>)

    @Transaction // Обязательно для загрузки связей (@Relation)
    @Query("SELECT * FROM stories ORDER BY published_time DESC") // Уточните поле сортировки, если имя в Entity другое
    fun getStoriesWithAuthorAndTagsStream(): Flow<List<StoryWithAuthorAndTags>>

    // Получаем базовую историю с автором и тегами
    @Transaction
    @Query("SELECT * FROM stories WHERE id = :storyId LIMIT 1")
    suspend fun getStoryWithAuthorAndTagsById(storyId: String): StoryWithAuthorAndTags?

    // Получаем только сущность истории
    @Query("SELECT * FROM stories WHERE id = :storyId LIMIT 1")
    suspend fun getStoryEntityById(storyId: String): StoryEntity?

    @Query("DELETE FROM stories WHERE id = :storyId")
    suspend fun deleteStoryById(storyId: String) // Room обработает каскадное удаление связей

    @Query("DELETE FROM stories")
    suspend fun clearAllStories()

    // Методы для работы со связями (альтернатива StoryTagCrossRefDao)
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertStoryTagCrossRefs(crossRefs: List<StoryTagCrossRef>)

    @Query("DELETE FROM story_tag_cross_ref WHERE story_id = :storyId")
    suspend fun deleteStoryTagCrossRefsForStory(storyId: String)
}