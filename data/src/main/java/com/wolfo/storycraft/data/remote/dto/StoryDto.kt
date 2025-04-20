package com.wolfo.storycraft.data.remote.dto

import com.google.gson.annotations.SerializedName

data class StoryDto(
    @SerializedName("id")
    val id: Long,
    @SerializedName("author_id")
    val authorId: Long,
    @SerializedName("start_page_id")
    val startPageId: Long,
    @SerializedName("title")
    val title: String,
    @SerializedName("description")
    val description: String? = null,
    @SerializedName("tags")
    val tags: List<String>? = null,
    @SerializedName("cover_image_url")
    val coverImageUrl: String? = null,
    @SerializedName("is_published")
    val isPublished: Boolean,
    @SerializedName("pages")
    val pages: List<PageDto>,
    @SerializedName("average_rating")
    val averageRating: Float? = null
)