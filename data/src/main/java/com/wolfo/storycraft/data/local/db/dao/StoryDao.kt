package com.wolfo.storycraft.data.local.db.dao

import androidx.room.Dao
import androidx.room.Embedded
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Relation
import androidx.room.Transaction
import com.wolfo.storycraft.data.local.db.entity.ChoiceEntity
import com.wolfo.storycraft.data.local.db.entity.PageEntity
import com.wolfo.storycraft.data.local.db.entity.StoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface StoryDao {
    @Query("SELECT * FROM stories ORDER BY title ASC")
    fun observeStoryList(): Flow<List<StoryEntity>>

    @Transaction
    @Query("SELECT * FROM stories WHERE id = :storyId")
    fun observeStoryFullById(storyId: Long): Flow<StoryWithPagesAndChoices?>

    @Query("SELECT * FROM STORIES WHERE id = :storyId")
    fun observeStoryBaseById(storyId: Long): Flow<StoryEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrReplaceStoryList(storyList: List<StoryEntity>)

    @Transaction
    suspend fun clearAndReplaceStoryList(storyList: List<StoryEntity>) {
        deleteStoryList()
        insertOrReplaceStoryList(storyList)

    }

    @Query("DELETE FROM stories")
    suspend fun deleteStoryList()

    @Transaction
    suspend fun insertOrReplaceStoryFull(
        story: StoryEntity,
        pages: List<PageEntity>,
        choices: List<ChoiceEntity>
    ) {
        insertOrReplaceStory(story)
        deletePagesForStory(story.id)
        insertPages(pages)
        insertChoices(choices)
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrReplaceStory(story: StoryEntity)

    @Query("DELETE FROM pages WHERE storyId = :storyId")
    suspend fun deletePagesForStory(storyId: Long)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPages(pages: List<PageEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChoices(choice: List<ChoiceEntity>)

    @Query("DELETE FROM stories WHERE id = :storyId")
    suspend fun deleteStoryById(storyId: Long)
}

data class StoryWithPagesAndChoices(
    @Embedded
    val story: StoryEntity,
    @Relation(
        entity = PageEntity::class,
        parentColumn = "id",
        entityColumn = "storyId"
    )
    val pages: List<PageWithChoices>
)

data class PageWithChoices(
    @Embedded
    val page: PageEntity,
    @Relation(
        entity = ChoiceEntity::class,
        parentColumn = "id",
        entityColumn = "pageId"
    )
    val choices: List<ChoiceEntity>
)