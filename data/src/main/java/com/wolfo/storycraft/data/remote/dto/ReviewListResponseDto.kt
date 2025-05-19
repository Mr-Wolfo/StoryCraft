package com.wolfo.storycraft.data.remote.dto

import com.google.gson.annotations.SerializedName

data class ReviewListResponseDto(
    @SerializedName("total") val total: Int,
    @SerializedName("reviews") val reviews: List<ReviewDto>
)