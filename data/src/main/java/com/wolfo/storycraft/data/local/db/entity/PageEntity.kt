package com.wolfo.storycraft.data.local.db.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "pages",
    foreignKeys = [ForeignKey(
        entity = StoryEntity::class,
        parentColumns = ["id"],
        childColumns = ["storyId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index(value = ["storyId"])]
)
data class PageEntity(
    @PrimaryKey
    val id: Long,
    val storyId: Long,
    val pageText: String,
    val imageUrl: String?,
    val isEndingPage: Boolean
)