package com.wolfo.storycraft.data.remote.dto

import com.google.gson.annotations.SerializedName

data class TagDto(
    @SerializedName("id") val id: String, // UUID
    @SerializedName("name") val name: String
)
