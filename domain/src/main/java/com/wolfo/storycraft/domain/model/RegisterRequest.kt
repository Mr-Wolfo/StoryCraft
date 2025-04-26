package com.wolfo.storycraft.domain.model

data class RegisterRequest(
    val userName: String,
    val email: String,
    val password: String
)