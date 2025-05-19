package com.wolfo.storycraft.domain.model

data class UserRegisterRequest(
    val username: String,
    val email: String,
    val password: String
)