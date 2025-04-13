package com.wolfo.storycraft.data.remote.dto

import com.google.gson.annotations.SerializedName

data class PageDto(
    @SerializedName("id")
    val id: Long,
    @SerializedName("story_id")
    val storyId: Long,
    @SerializedName("page_text")
    val pageText: String,
    @SerializedName("image_url")
    val imageUrl: String? = null,
    @SerializedName("is_ending_page")
    val isEndingPage: Boolean,
    @SerializedName("choices")
    val choices: List<ChoiceDto>
)
