package com.wolfo.storycraft.data.local.db.entity

import androidx.room.Embedded
import androidx.room.Relation

data class StoryDraftWithPagesAndChoices(
    @Embedded val storyDraft: StoryDraftEntity,
    @Relation(
        entity = PageDraftEntity::class,
        parentColumn = "id",
        entityColumn = "storyDraftId"
    )
    val pagesWithChoices: List<PageDraftWithChoices>
)