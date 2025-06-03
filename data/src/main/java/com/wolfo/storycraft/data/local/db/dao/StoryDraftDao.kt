package com.wolfo.storycraft.data.local.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.wolfo.storycraft.data.local.db.entity.ChoiceDraftEntity
import com.wolfo.storycraft.data.local.db.entity.PageDraftEntity
import com.wolfo.storycraft.data.local.db.entity.StoryDraftEntity
import com.wolfo.storycraft.data.local.db.entity.StoryDraftWithPagesAndChoices

@Dao
interface StoryDraftDao {

    @Transaction
    @Query("SELECT * FROM story_drafts WHERE userId = :userId ORDER BY lastSavedTimestamp DESC")
    suspend fun getStoryDraftsForUser(userId: String): List<StoryDraftEntity>

    @Transaction
    @Query("SELECT * FROM story_drafts WHERE id = :draftId")
    suspend fun getStoryDraftWithDetails(draftId: String): StoryDraftWithPagesAndChoices?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStoryDraft(story: StoryDraftEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPageDrafts(pages: List<PageDraftEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChoiceDrafts(choices: List<ChoiceDraftEntity>)

    @Update
    suspend fun updateStoryDraft(story: StoryDraftEntity)

    @Update
    suspend fun updatePageDrafts(pages: List<PageDraftEntity>)

    @Update
    suspend fun updateChoiceDrafts(choices: List<ChoiceDraftEntity>)

    @Delete
    suspend fun deleteStoryDraft(story: StoryDraftEntity)

    @Query("DELETE FROM page_drafts WHERE storyDraftId = :storyDraftId")
    suspend fun deletePageDraftsForStory(storyDraftId: String)

    @Query("DELETE FROM choice_drafts WHERE pageDraftId IN (SELECT id FROM page_drafts WHERE storyDraftId = :storyDraftId)")
    suspend fun deleteChoiceDraftsForStory(storyDraftId: String)

    @Transaction
    suspend fun saveFullStoryDraft(story: StoryDraftEntity, pages: List<PageDraftEntity>, choices: List<ChoiceDraftEntity>) {
        // Для простоты обновления, удаляем старые страницы и выборы и вставляем новые.
        // Это не оптимально для больших историй, но проще в реализации.
        // Более оптимально: находить измененные, добавленные, удаленные и применять точечные операции.
        // Удаляем сначала выборы, зависящие от страниц, затем страницы
        deleteChoiceDraftsForStory(story.id)
        deletePageDraftsForStory(story.id)

        insertStoryDraft(story)
        if (pages.isNotEmpty()) {
            insertPageDrafts(pages)
        }
        if (choices.isNotEmpty()) {
            insertChoiceDrafts(choices)
        }
    }

    @Query("DELETE FROM story_drafts WHERE id = :draftId")
    suspend fun deleteStoryDraftById(draftId: String)

    @Query("SELECT id, title, lastSavedTimestamp FROM story_drafts WHERE userId = :userId ORDER BY lastSavedTimestamp DESC")
    suspend fun getStoryBaseInfoDraftsForUser(userId: String): List<StoryBaseInfoDraft> // Вспомогательная структура
}

// Вспомогательная структура для списка черновиков
data class StoryBaseInfoDraft(
    val id: String,
    val title: String,
    val lastSavedTimestamp: Long
)
// TODO: Map StoryBaseInfoDraft to StoryBaseInfo