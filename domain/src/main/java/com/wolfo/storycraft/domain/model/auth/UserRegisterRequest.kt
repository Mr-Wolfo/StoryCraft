package com.wolfo.storycraft.domain.model.auth

data class UserRegisterRequest(
    val username: String,
    val email: String,
    val password: String
)