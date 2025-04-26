package com.wolfo.storycraft.data.remote.dto

import com.google.gson.annotations.SerializedName

data class UserDto(
    @SerializedName("id")
    val id: Long,
    @SerializedName("username")
    val userName: String,
    @SerializedName("email")
    val email: String,
)