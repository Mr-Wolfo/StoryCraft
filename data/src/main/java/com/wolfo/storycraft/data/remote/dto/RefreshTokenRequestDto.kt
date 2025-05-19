package com.wolfo.storycraft.data.remote.dto

import com.google.gson.annotations.SerializedName

data class RefreshTokenRequestDto(
    @SerializedName("refresh_token") val refreshToken: String
)