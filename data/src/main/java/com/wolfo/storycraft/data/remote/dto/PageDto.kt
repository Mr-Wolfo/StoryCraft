package com.wolfo.storycraft.data.remote.dto

import com.google.gson.annotations.SerializedName

data class PageDto(
    @SerializedName("id") val id: String, // UUID
    @SerializedName("page_text") val pageText: String,
    @SerializedName("image_url") val imageUrl: String?,
    @SerializedName("is_ending_page") val isEndingPage: Boolean,
    @SerializedName("story_id") val storyId: String, // UUID
    @SerializedName("choices") val choices: List<ChoiceDto> = emptyList()
)
