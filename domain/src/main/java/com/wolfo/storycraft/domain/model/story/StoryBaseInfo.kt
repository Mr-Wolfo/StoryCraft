package com.wolfo.storycraft.domain.model.story

import com.wolfo.storycraft.domain.model.user.UserSimple

data class StoryBaseInfo(
    val id: String,
    val title: String,
    val description: String?,
    val coverImageUrl: String?,
    val averageRating: Float,
    val publishedTime: String,
    val viewCount: Int,
    val author: UserSimple,
    val tags: List<Tag> = emptyList()
)