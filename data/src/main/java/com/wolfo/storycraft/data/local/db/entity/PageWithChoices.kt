package com.wolfo.storycraft.data.local.db.entity

import androidx.room.Embedded
import androidx.room.Relation

// Страница с ее выборами (для деталей истории)
data class PageWithChoices(
    @Embedded val page: PageEntity,
    @Relation(
        parentColumn = "id", // page.id
        entityColumn = "page_id" // choice.page_id
    )
    val choices: List<ChoiceEntity>
)
