package com.wolfo.storycraft.data.local.db.entity

import androidx.room.Embedded
import androidx.room.Relation

data class StoryWithAuthorAndPages(
    @Embedded val story: StoryEntity,
    @Relation(
        parentColumn = "author_id",
        entityColumn = "id"
    )
    val author: UserEntity?,
    @Relation(
        entity = PageEntity::class,
        parentColumn = "id", // story.id
        entityColumn = "story_id" // page.story_id
    )
    val pagesWithChoices: List<PageWithChoices> // Room сам соберет Page -> List<Choice>
)