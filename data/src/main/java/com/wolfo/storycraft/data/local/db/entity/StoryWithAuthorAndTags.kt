package com.wolfo.storycraft.data.local.db.entity

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation

data class StoryWithAuthorAndTags(
    @Embedded val story: StoryEntity,
    @Relation(
        parentColumn = "author_id",
        entityColumn = "id"
    )
    val author: UserEntity?, // Автор может быть null из-за SET_NULL или если author_id был null
    @Relation(
        parentColumn = "id", // story.id
        entityColumn = "id", // tag.id
        associateBy = Junction(StoryTagCrossRef::class, parentColumn = "story_id", entityColumn = "tag_id")
    )
    val tags: List<TagEntity>
)