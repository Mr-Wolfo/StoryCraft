package com.wolfo.storycraft.domain.model

data class PublishChoice(
    val text: String,
    val targetPageIndex: Int? // Целевая страница по индексу
)