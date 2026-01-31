package com.wolfo.storycraft.data.local.db.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "choice_drafts",
    foreignKeys = [ForeignKey(
        entity = PageDraftEntity::class,
        parentColumns = ["id"],
        childColumns = ["pageDraftId"],
        onDelete = ForeignKey.CASCADE // Удаляем выборы при удалении страницы
    )],
    indices = [Index("pageDraftId")]
)
data class ChoiceDraftEntity(
    @PrimaryKey
    val id: String,
    val pageDraftId: String,
    val text: String,
    val targetPageIndex: Int? // Целевая страница по индексу
)