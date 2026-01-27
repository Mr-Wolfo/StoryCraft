package com.wolfo.storycraft.data.remote.dto

import com.google.gson.annotations.SerializedName

data class ChoiceDto(
    @SerializedName("id") val id: String, // UUID
    @SerializedName("page_id") val pageId: String,
    @SerializedName("choice_text") val choiceText: String,
    @SerializedName("target_page_id") val targetPageId: String // UUID следующей страницы
)
