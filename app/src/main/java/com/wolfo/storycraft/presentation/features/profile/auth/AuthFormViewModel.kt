package com.wolfo.storycraft.presentation.features.profile.auth

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

class AuthFormViewModel: ViewModel() {
    val loginEmail = mutableStateOf("")
    val loginPassword = mutableStateOf("")

    val registerName = mutableStateOf("")
    val registerEmail = mutableStateOf("")
    val registerPassword = mutableStateOf("")
}