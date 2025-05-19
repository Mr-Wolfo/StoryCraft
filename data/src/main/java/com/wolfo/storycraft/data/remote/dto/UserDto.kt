package com.wolfo.storycraft.data.remote.dto

import com.google.gson.annotations.SerializedName
import java.sql.Time

data class UserDto(
    @SerializedName("id") val id: String,
    @SerializedName("username") val username: String,
    @SerializedName("email") val email: String,
    @SerializedName("signature") val signature: String?,
    @SerializedName("avatar_url") val avatarUrl: String?,
    @SerializedName("created_at") val createdAt: String,
    @SerializedName("stories") val stories: List<StoryBaseInfoDto>,
    @SerializedName("overall_rating") val overallRating: Float
)