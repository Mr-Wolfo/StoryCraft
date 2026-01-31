package com.wolfo.storycraft.data.local.db.entity

import android.net.Uri
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import androidx.room.TypeConverters

class UriConverter {
    @TypeConverter
    fun fromUri(uri: Uri?): String? {
        return uri?.toString()
    }

    @TypeConverter
    fun toUri(uriString: String?): Uri? {
        return uriString?.let { Uri.parse(it) }
    }
}

// Entity для черновика истории
@Entity(tableName = "story_drafts")
@TypeConverters(UriConverter::class)
data class StoryDraftEntity(
    @PrimaryKey
    val id: String, // Используем сгенерированный UUID как ID в Room
    val userId: String,
    val title: String,
    val description: String?,
    val tagsJson: String,
    val coverImageUri: Uri?,
    val lastSavedTimestamp: Long
)