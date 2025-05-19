package com.wolfo.storycraft.data.remote.dto

import com.google.gson.annotations.SerializedName

data class ReviewDto(
    @SerializedName("id") val id: String,
    @SerializedName("rating") val rating: Int,
    @SerializedName("review_text") val reviewText: String?,
    @SerializedName("story_id") val storyId: String,
    @SerializedName("user_id") val userId: String,
    @SerializedName("user") val user: UserSimpleDto,
    @SerializedName("created_at") val createdAt: String,
    @SerializedName("updated_at") val updatedAt: String?
)