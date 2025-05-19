package com.wolfo.storycraft.data.local.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.wolfo.storycraft.data.local.db.entity.StoryTagCrossRef

@Dao
interface StoryTagCrossRefDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertCrossRefs(crossRefs: List<StoryTagCrossRef>)

    @Query("DELETE FROM story_tag_cross_ref WHERE story_id = :storyId")
    suspend fun deleteCrossRefsForStory(storyId: String)

    @Query("SELECT tag_id FROM story_tag_cross_ref WHERE story_id = :storyId")
    suspend fun getTagIdsForStory(storyId: String): List<String>
}