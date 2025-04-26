package com.wolfo.storycraft.data.remote.dto

import com.google.gson.annotations.SerializedName

data class AuthRequestDto(
    @SerializedName("grant_type")
    val grantType: String = "password",
    @SerializedName("username")
    val name: String,
    @SerializedName("password")
    val password: String,
    @SerializedName("scope")
    val scope: String? = null,
    @SerializedName("client_id")
    val clientId: String? = null,
    @SerializedName("client_secret")
    val clientSecret: String? = null
)