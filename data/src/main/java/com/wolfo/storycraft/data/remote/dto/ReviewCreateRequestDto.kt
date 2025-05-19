package com.wolfo.storycraft.data.remote.dto

import com.google.gson.annotations.SerializedName

data class ReviewCreateRequestDto(
    @SerializedName("rating") val rating: Int,
    @SerializedName("review_text") val reviewText: String?,
    @SerializedName("story_id") val storyId: String
)
