package com.wolfo.storycraft.domain.model

data class Page(
    val id: Long,
    val storyId: Long,
    val pageText: String,
    val isEndingPage: Boolean,
    val choices: List<Choice>
)
