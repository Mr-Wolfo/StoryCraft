package com.wolfo.storycraft.data.remote.dto

import com.google.gson.annotations.SerializedName

data class UserAuthResponseDto(
    @SerializedName("user") val user: UserDto,
    @SerializedName("tokens") val token: TokenDto
)