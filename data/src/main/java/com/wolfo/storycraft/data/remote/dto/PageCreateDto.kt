package com.wolfo.storycraft.data.remote.dto

import com.google.gson.annotations.SerializedName

data class PageCreateDto(
    @SerializedName("page_text") val pageText: String,
    @SerializedName("is_ending_page") val isEndingPage: Boolean,
    @SerializedName("choices") val choices: List<ChoiceCreateDto>
)