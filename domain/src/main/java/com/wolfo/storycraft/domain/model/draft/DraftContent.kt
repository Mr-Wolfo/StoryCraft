package com.wolfo.storycraft.domain.model.draft

data class DraftContent(
    val id: String, // ID черновика
    val userId: String, // ID пользователя (связь с пользователем)
    val title: String,
    val description: String?,
    val tags: List<String>, // Список имен тегов
    val coverImagePath: String?, // Путь к локальному файлу изображения обложки (String)
    val pages: List<DraftPage> = emptyList(),
    val lastSavedTimestamp: Long // Время последнего сохранения
)