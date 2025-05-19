package com.wolfo.storycraft.data.local.db.entity

import androidx.room.Embedded
import androidx.room.Relation

// Отзыв с его автором (для списков отзывов)
data class ReviewWithAuthor(
    @Embedded val review: ReviewEntity,
    @Relation(
        parentColumn = "user_id",
        entityColumn = "id"
    )
    val author: UserEntity? // Автор может быть null
)