package com.wolfo.storycraft.domain.model.draft

data class DraftChoice(
    val id: String, // Клиентский ID выбора (UUID String)
    val text: String,
    val targetPageIndex: Int? // Целевая страница по индексу
)