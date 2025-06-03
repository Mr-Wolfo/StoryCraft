package com.wolfo.storycraft.domain.model

import com.wolfo.storycraft.domain.model.user.UserSimple

data class Review(
    val id: String,
    val rating: Int,
    val reviewText: String?,
    val storyId: String,
    val userId: String,
    val user: UserSimple,
    val createdAt: String,
    val updatedAt: String?
)