package com.wolfo.storycraft.data.remote.dto

import com.google.gson.annotations.SerializedName

data class StoryListResponseDto(
    @SerializedName("total") val total: Int,
    @SerializedName("stories") val stories: List<StoryBaseInfoDto>
)