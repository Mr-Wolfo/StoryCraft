package com.wolfo.storycraft.data.local.db.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "choices",
    foreignKeys = [ForeignKey(
        entity = PageEntity::class,
        parentColumns = ["id"],
        childColumns = ["pageId"],
        onDelete = ForeignKey.CASCADE
    ),
        ForeignKey(
            entity = PageEntity::class,
            parentColumns = ["id"],
            childColumns = ["targetPageId"],
            onDelete = ForeignKey.SET_NULL
        )],
    indices = [Index(value = ["pageId"]), Index(value = ["targetPageId"])]
)
data class ChoiceEntity(
    @PrimaryKey
    val id: Long,
    val pageId: Long,
    val choiceText: String,
    val targetPageId: Long?
)