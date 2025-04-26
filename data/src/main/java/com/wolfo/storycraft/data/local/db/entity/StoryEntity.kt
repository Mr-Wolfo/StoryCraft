package com.wolfo.storycraft.data.local.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

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
    val tags: List<String>?,
    val isPublished: Boolean,
    val averageRating: Float?,
    val lastRefreshed: Long = System.currentTimeMillis()
) {
    class Converters {
        @TypeConverter
        fun fromStringList(value: List<String>?): String {
            return Gson().toJson(value)
        }

        @TypeConverter
        fun toStringList(value: String): List<String>? {
            return Gson().fromJson(value, object : TypeToken<List<String>>(){}.type)
        }
    }
}