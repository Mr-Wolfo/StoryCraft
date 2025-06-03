package com.wolfo.storycraft.data.remote.dto

import com.google.gson.annotations.SerializedName

data class ChoiceCreateDto(
    @SerializedName("choice_text") val choiceText: String,
    @SerializedName("target_page_id") val targetPageIndex: Int?
)