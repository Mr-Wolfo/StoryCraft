package com.wolfo.storycraft.data.remote.dto

import com.google.gson.annotations.SerializedName

data class StoryFullDto(
    @SerializedName("id") val id: String,
    @SerializedName("title") val title: String,
    @SerializedName("description") val description: String?,
    @SerializedName("cover_image_url") val coverImageUrl: String?,
    @SerializedName("average_rating") val averageRating: Float,
    @SerializedName("published_time") val publishedTime: String,
    @SerializedName("view_count") val viewCount: Int,
    @SerializedName("author") val author: UserSimpleDto,
    @SerializedName("tags") val tags: List<TagDto> = emptyList(),
    @SerializedName("pages") val pages: List<PageDto> = emptyList()
)