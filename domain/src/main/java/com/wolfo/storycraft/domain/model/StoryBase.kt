package com.wolfo.storycraft.domain.model

data class StoryBase(
    val id: Long,
    val title: String,
    val description: String?,
    val authorId: Long,
    val tags: List<String>?,
    val averageRating: Float?
)