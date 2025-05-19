package com.wolfo.storycraft.data.remote.dto

import com.google.gson.annotations.SerializedName

data class UserRegisterRequestDto(
    @SerializedName("username") val username: String,
    @SerializedName("email") val email: String,
    @SerializedName("password") val password: String
)