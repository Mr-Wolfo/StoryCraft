package com.wolfo.storycraft.data.local.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.wolfo.storycraft.data.local.db.entity.ChoiceEntity

@Dao
interface ChoiceDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateChoices(choices: List<ChoiceEntity>)

    // Не используется напрямую, т.к. выборы получаются через PageWithChoices
    // @Query("SELECT * FROM choices WHERE page_id = :pageId")
    // suspend fun getChoicesForPage(pageId: String): List<ChoiceEntity>

    // Не нужен явный метод, удаление каскадное через PageDao.deletePagesForStory
    // @Query("DELETE FROM choices WHERE page_id IN (SELECT id FROM pages WHERE story_id = :storyId)")
    // suspend fun deleteChoicesForStory(storyId: String)
}