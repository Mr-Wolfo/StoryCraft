package com.wolfo.storycraft.domain.model.story

data class Choice(
    val id: String,
    val pageId: String,
    val choiceText: String,
    val targetPageId: String?
)
