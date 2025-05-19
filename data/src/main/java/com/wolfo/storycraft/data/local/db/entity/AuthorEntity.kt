package com.wolfo.storycraft.data.local.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "author")
data class AuthorEntity(
    @PrimaryKey
    val id: String,
    val username: String,
    @ColumnInfo("avatar_url") val avatarUrl: String?
)