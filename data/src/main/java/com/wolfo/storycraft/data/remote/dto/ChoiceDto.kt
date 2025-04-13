package com.wolfo.storycraft.data.remote.dto

import com.google.gson.annotations.SerializedName

data class ChoiceDto(
    @SerializedName("id")
    val id: Long,
    @SerializedName("page_id")
    val pageId: Long,
    @SerializedName("target_page_id")
    val targetPageId: Long,
    @SerializedName("choice_text")
    val choiceText: String,
)
