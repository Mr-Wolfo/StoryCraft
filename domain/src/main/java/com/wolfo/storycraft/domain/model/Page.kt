package com.wolfo.storycraft.domain.model

data class Page(
    val id: String, // UUID
    val pageText: String,
    val imageUrl: String?,
    val isEndingPage: Boolean,
    val storyId: String, // UUID
    val choices: List<Choice> = emptyList()
)
