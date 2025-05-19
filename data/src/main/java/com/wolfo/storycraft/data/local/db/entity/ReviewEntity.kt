package com.wolfo.storycraft.data.local.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.wolfo.storycraft.data.local.db.DateConverter
import java.util.Date


@Entity(
    tableName = "reviews",
    foreignKeys = [
        ForeignKey(
            entity = StoryEntity::class,
            parentColumns = ["id"],
            childColumns = ["story_id"],
            onDelete = ForeignKey.CASCADE // Отзывы удаляются с историей
        ),
        ForeignKey(
            entity = UserEntity::class, // Связь с автором отзыва
            parentColumns = ["id"],
            childColumns = ["user_id"],
            onDelete = ForeignKey.CASCADE // Или SET_NULL, если юзер может быть удален, а отзыв останется
        )
    ],
    indices = [Index("story_id"), Index("user_id")]
)
data class ReviewEntity(
    @PrimaryKey val id: String, // UUID
    val rating: Int,
    @ColumnInfo(name = "review_text") val reviewText: String?,
    @ColumnInfo(name = "story_id") val storyId: String, // Внешний ключ к StoryEntity
    @ColumnInfo(name = "user_id") val userId: String, // Внешний ключ к UserEntity (автор)
    @ColumnInfo(name = "created_at") val createdAt: String, // Храним как строку (ISO формат)
    @ColumnInfo(name = "updated_at") val updatedAt: String? // Храним как строку (ISO формат)
)