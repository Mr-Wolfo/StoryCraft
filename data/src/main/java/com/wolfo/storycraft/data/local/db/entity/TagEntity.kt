package com.wolfo.storycraft.data.local.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tags")
data class TagEntity(
    @PrimaryKey val id: String, // UUID от сервера
    @ColumnInfo(index = true) val name: String // Имя тега, индексируем для поиска
)