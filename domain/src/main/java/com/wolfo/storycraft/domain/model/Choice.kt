package com.wolfo.storycraft.domain.model

data class Choice(
    val id: Long,
    val pageId: Long,
    val choiceText: String,
    val targetPageId: Long
)
