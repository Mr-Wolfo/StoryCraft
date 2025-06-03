package com.wolfo.storycraft.domain.model.auth

data class AuthRequest(
    val name: String,
    val password: String
)