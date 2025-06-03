package com.wolfo.storycraft.data.local.db.entity

import androidx.room.Embedded
import androidx.room.Relation

data class PageDraftWithChoices(
    @Embedded val pageDraft: PageDraftEntity,
    @Relation(
        entity = ChoiceDraftEntity::class,
        parentColumn = "id",
        entityColumn = "pageDraftId"
    )
    val choices: List<ChoiceDraftEntity>
)