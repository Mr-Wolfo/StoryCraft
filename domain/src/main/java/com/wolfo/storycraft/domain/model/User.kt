package com.wolfo.storycraft.domain.model

data class User(
    val id: String,
    val username: String,
    val email: String,
    val signature: String?,
    val avatarUrl: String?,
    val createdAt: String,
    val stories: List<StoryBaseInfo>,
    val overallRating: Float
)