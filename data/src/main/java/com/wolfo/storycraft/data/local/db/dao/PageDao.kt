package com.wolfo.storycraft.data.local.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.wolfo.storycraft.data.local.db.entity.PageEntity
import com.wolfo.storycraft.data.local.db.entity.PageWithChoices

@Dao
interface PageDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdatePages(pages: List<PageEntity>)

    // Получение страниц с выборами для истории
    @Transaction
    @Query("SELECT * FROM pages WHERE story_id = :storyId ORDER BY id ASC") // Уточните порядок, если нужно
    suspend fun getPagesWithChoicesForStory(storyId: String): List<PageWithChoices>

    @Query("DELETE FROM pages WHERE story_id = :storyId")
    suspend fun deletePagesForStory(storyId: String)
}