package com.wolfo.storycraft.data.local.db.entity

import android.net.Uri
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.TypeConverters

@Entity(
    tableName = "page_drafts",
    foreignKeys = [ForeignKey(
        entity = StoryDraftEntity::class,
        parentColumns = ["id"],
        childColumns = ["storyDraftId"],
        onDelete = ForeignKey.CASCADE // Удаляем страницы при удалении истории
    )],
    indices = [Index("storyDraftId")]
)
@TypeConverters(UriConverter::class)
data class PageDraftEntity(
    @PrimaryKey
    val id: String, // Используем клиентский ID
    val storyDraftId: String,
    val text: String,
    val imageUri: Uri?,
    val isEndingPage: Boolean,
    val pageOrder: Int // Порядок страницы внутри истории
)
