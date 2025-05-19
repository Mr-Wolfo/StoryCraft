package com.wolfo.storycraft.data.local.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users") // Используем 'users' для хранения всех пользователей/авторов
data class UserEntity(
    @PrimaryKey val id: String, // UUID
    val username: String,
    val email: String?, // Nullable, так как UserSimpleDto не содержит email
    val signature: String?,
    @ColumnInfo(name = "avatar_url") val avatarUrl: String?,
    @ColumnInfo(name = "created_at") val createdAt: String?, // Nullable, UserSimpleDto не содержит
    // val storiesJson: String?, // Не храним список историй пользователя в этой таблице
    @ColumnInfo(name = "overall_rating") val overallRating: Float?, // Nullable

    @ColumnInfo(name = "last_refresh") val lastRefresh: Long = System.currentTimeMillis()
)