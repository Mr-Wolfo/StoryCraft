package com.wolfo.storycraft.data.local.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "pages",
    foreignKeys = [
        ForeignKey(
            entity = StoryEntity::class,
            parentColumns = ["id"],
            childColumns = ["story_id"],
            onDelete = ForeignKey.CASCADE // Страницы удаляются вместе с историей
        )
    ],
    indices = [Index("story_id")]
)
data class PageEntity(
    @PrimaryKey val id: String, // UUID
    @ColumnInfo(name = "story_id") val storyId: String, // К какой истории относится
    @ColumnInfo(name = "page_text") val pageText: String,
    @ColumnInfo(name = "image_url") val imageUrl: String?,
    @ColumnInfo(name = "is_ending_page") val isEndingPage: Boolean
)
