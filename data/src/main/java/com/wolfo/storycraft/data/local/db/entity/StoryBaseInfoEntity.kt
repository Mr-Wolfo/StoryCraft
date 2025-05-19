package com.wolfo.storycraft.data.local.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.wolfo.storycraft.data.local.db.AuthorConverter
import com.wolfo.storycraft.data.local.db.ListConverter

@TypeConverters(AuthorConverter::class, ListConverter::class)
@Entity(tableName = "stories")
data class StoryBaseInfoEntity(
    @PrimaryKey val id: String,
    val title: String,
    val description: String,
    @ColumnInfo(name = "cover_image_url") val coverImageUrl: String?,
    @ColumnInfo(name = "average_rating") val averageRating: Float,
    @ColumnInfo(name = "published_time") val publishedTime: String,
    @ColumnInfo(name = "view_count") val viewCount: Int,
    @ColumnInfo(name = "author_id") val authorId: String,
    val tags: List<String>
)