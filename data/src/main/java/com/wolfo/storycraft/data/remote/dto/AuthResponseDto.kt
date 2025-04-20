package com.wolfo.storycraft.data.remote.dto

import com.google.gson.annotations.SerializedName

data class AuthResponseDto(
    @SerializedName("token")
    val token: String,
    @SerializedName("user")
    val user: UserDto
)