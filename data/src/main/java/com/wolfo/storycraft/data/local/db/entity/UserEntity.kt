package com.wolfo.storycraft.data.local.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_profile")
data class UserEntity(
    @PrimaryKey
    val id: Long,
    val userName: String,
    val email: String
)