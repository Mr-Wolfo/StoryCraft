package com.wolfo.storycraft.data.local.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "story_tag_cross_ref",
    primaryKeys = ["story_id", "tag_id"], // Составной первичный ключ
    foreignKeys = [
        ForeignKey(
            entity = StoryEntity::class,
            parentColumns = ["id"],
            childColumns = ["story_id"],
            onDelete = ForeignKey.CASCADE // Запись связи удаляется при удалении истории
        ),
        ForeignKey(
            entity = TagEntity::class,
            parentColumns = ["id"],
            childColumns = ["tag_id"],
            onDelete = ForeignKey.CASCADE // Запись связи удаляется при удалении тега (если теги могут удаляться)
        )
    ],
    indices = [Index("story_id"), Index("tag_id")]
)
data class StoryTagCrossRef(
    @ColumnInfo(name = "story_id") val storyId: String,
    @ColumnInfo(name = "tag_id") val tagId: String
)