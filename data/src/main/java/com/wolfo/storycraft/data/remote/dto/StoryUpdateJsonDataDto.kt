package com.wolfo.storycraft.data.remote.dto

import com.google.gson.annotations.SerializedName

data class StoryUpdateJsonDataDto(
    @SerializedName("title") val title: String?,
    @SerializedName("description") val description: String?,
    @SerializedName("tags") val tags: List<String>? // Список имен тегов
)