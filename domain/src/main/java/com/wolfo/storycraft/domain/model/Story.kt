package com.wolfo.storycraft.domain.model

data class Story(
    val id: Long,
    val authorId: Long,
    val startPageId: Long,
    val title: String,
    val description: String?,
    val tags: List<String>?,
    val coverImageUrl: String?,
    val isPublished: Boolean,
    val pages: List<Page>
)