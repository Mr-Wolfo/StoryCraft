package com.wolfo.storycraft.domain.model.draft

data class DraftPage(
    val id: String, // Клиентский ID страницы (UUID String)
    val text: String,
    val imagePath: String?, // Путь к локальному файлу изображения страницы (String)
    val isEndingPage: Boolean,
    val choices: List<DraftChoice> = emptyList(),
    val pageOrder: Int
)