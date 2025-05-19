package com.wolfo.storycraft.data.local.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.wolfo.storycraft.data.local.db.DateConverter
import com.wolfo.storycraft.data.local.db.ListConverter
import com.wolfo.storycraft.data.remote.dto.PageDto
import java.time.LocalDateTime
import java.util.Date

@Entity(
    tableName = "stories",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class, // Связь с автором
            parentColumns = ["id"],
            childColumns = ["author_id"],
            onDelete = ForeignKey.SET_NULL // Или CASCADE, если нужно удалять истории при удалении юзера
        )
    ],
    indices = [Index("author_id")]
)
data class StoryEntity(
    @PrimaryKey val id: String, // UUID
    val title: String,
    val description: String?,
    @ColumnInfo(name = "cover_image_url") val coverImageUrl: String?,
    @ColumnInfo(name = "average_rating") val averageRating: Float,
    @ColumnInfo(name = "published_time") val publishedTime: String, // Храним как строку (ISO формат)
    @ColumnInfo(name = "view_count") val viewCount: Int,
    @ColumnInfo(name = "author_id") val authorId: String?, // Внешний ключ к UserEntity

    @ColumnInfo(name = "last_refresh") val lastRefresh: Long = System.currentTimeMillis()
    // Теги будут связаны через StoryTagCrossRef
)