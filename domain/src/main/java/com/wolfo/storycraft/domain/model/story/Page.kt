package com.wolfo.storycraft.domain.model.story

data class Page(
    val id: String,
    val pageText: String,
    val imageUrl: String?,
    val isEndingPage: Boolean,
    val storyId: String,
    val choices: List<Choice> = emptyList()
)
