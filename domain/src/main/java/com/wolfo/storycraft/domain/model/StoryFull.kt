package com.wolfo.storycraft.domain.model

data class StoryFull(
    val id: String,
    val title: String,
    val description: String?,
    val coverImageUrl: String?,
    val averageRating: Float,
    val publishedTime: String,
    val viewCount: Int,
    val author: UserSimple,
    val tags: List<Tag> = emptyList(),
    val pages: List<Page> = emptyList()
)