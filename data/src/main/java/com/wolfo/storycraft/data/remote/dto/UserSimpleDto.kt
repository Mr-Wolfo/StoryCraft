package com.wolfo.storycraft.data.remote.dto

import com.google.gson.annotations.SerializedName

data class UserSimpleDto(
    @SerializedName("id") val id: String,
    @SerializedName("username") val username: String,
    @SerializedName("avatar_url") val avatarUrl: String?
)