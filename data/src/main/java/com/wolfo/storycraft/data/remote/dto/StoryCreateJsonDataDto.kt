package com.wolfo.storycraft.data.remote.dto

import com.google.gson.annotations.SerializedName

data class StoryCreateJsonDataDto(
    @SerializedName("title") val title: String,
    @SerializedName("description") val description: String?,
    @SerializedName("tags") val tags: List<String> // Список имен тегов
    // Добавьте другие поля, если они нужны при создании (напр., первая страница)
    // OpenAPI не детализирует StoryCreate схему внутри Body_create_story...
    // Предполагаем пока только базовые поля. Если нужны страницы/выборы сразу:
    // @SerializedName("pages") val pages: List<PageCreateDto>
)