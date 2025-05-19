package com.wolfo.storycraft.data.remote.dto

import com.google.gson.annotations.SerializedName

data class UserUpdateDto(
    @SerializedName("email") val email: String?,
    @SerializedName("signature") val signature: String?
)