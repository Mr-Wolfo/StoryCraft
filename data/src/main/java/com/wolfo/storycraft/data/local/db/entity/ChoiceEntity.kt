package com.wolfo.storycraft.data.local.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "choices",
    foreignKeys = [
        ForeignKey(
            entity = PageEntity::class,
            parentColumns = ["id"],
            childColumns = ["page_id"],
            onDelete = ForeignKey.CASCADE // Выборы удаляются со страницей
        )
        // Связь с target_page_id делаем логически в репозитории/домене, не в FK Room
    ],
    indices = [Index("page_id")]
)
data class ChoiceEntity(
    @PrimaryKey val id: String, // UUID
    @ColumnInfo(name = "page_id") val pageId: String, // К какой странице относится
    @ColumnInfo(name = "choice_text") val choiceText: String,
    @ColumnInfo(name = "target_page_id") val targetPageId: String // ID следующей страницы (Nullable UUID)
)