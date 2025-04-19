package com.wolfo.storycraft.data.local.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "stories")
data class StoryEntity(
    @PrimaryKey
    val id: Long,
    val title: String,
    val description: String?,
    val authorId: Long?,
    val authorName: String,
    val coverImageUrl: String?,
    val startPageId: Long?,
    // val tags: List<String>?,
    val isPublished: Boolean,
    val averageRating: Float?,
    val lastRefreshed: Long = System.currentTimeMillis()
)