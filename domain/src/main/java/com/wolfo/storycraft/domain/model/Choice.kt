package com.wolfo.storycraft.domain.model

data class Choice(
    val id: String, // UUID
    val pageId: String,
    val choiceText: String,
    val targetPageId: String? // UUID следующей страницы
)
