package com.wolfo.storycraft.data.local.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.wolfo.storycraft.data.local.db.entity.TagEntity
import kotlinx.coroutines.flow.Flow


@Dao
interface TagDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE) // Игнорируем, если тег с таким ID уже есть
    suspend fun insertTags(tags: List<TagEntity>)

    @Query("SELECT * FROM tags WHERE id IN (:tagIds)")
    suspend fun getTagsByIds(tagIds: List<String>): List<TagEntity>

    @Query("SELECT * FROM tags WHERE name IN (:tagNames)")
    suspend fun getTagsByNames(tagNames: List<String>): List<TagEntity>

    @Query("SELECT * FROM tags")
    fun getAllTagsFlow(): Flow<List<TagEntity>>
}