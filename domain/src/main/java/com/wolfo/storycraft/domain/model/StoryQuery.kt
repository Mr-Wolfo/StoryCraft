package com.wolfo.storycraft.domain.model

// Класс для параметров запроса историй
data class StoryQuery(
    val skip: Int = 0,
    val limit: Int = 20, // Размер страницы по умолчанию
    val sortBy: String? = "published_time",
    val sortOrder: String? = "desc",
    val searchQuery: String? = null,
    val authorUsername: String? = null,
    val tagNames: List<String>? = null
)